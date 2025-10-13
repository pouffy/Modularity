package com.pouffydev.modularity.api.tier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.*;
import com.pouffydev.modularity.Modularity;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.toposort.TopologicalSort;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TierSortingRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ITEM_TIER_ORDERING_JSON = Modularity.modularityPath("item_tier_ordering.json");
    private static boolean hasCustomTiers = false;
    private static final BiMap<ResourceLocation, Tier> tiers = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();
    private static final List<Tier> sortedTiers;
    private static final List<Tier> sortedTiersUnmodifiable;
    private static final ResourceLocation CHANNEL_NAME;
    private static final String PROTOCOL_VERSION = "1.0";

    public static synchronized Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before) {
        if (tiers.containsKey(name)) {
            throw new IllegalStateException("Duplicate tier name " + name);
        } else {
            processTier(tier, name, after, before);
            hasCustomTiers = true;
            return tier;
        }
    }

    public static List<Tier> getSortedTiers() {
        return sortedTiersUnmodifiable;
    }

    public static @Nullable Tier byName(ResourceLocation name) {
        return tiers.get(name);
    }

    public static @Nullable ResourceLocation getName(Tier tier) {
        return tiers.inverse().get(tier);
    }

    public static boolean isTierSorted(Tier tier) {
        return getName(tier) != null;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        if (!isTierSorted(tier)) {
            return false;
        } else {
            for(int x = sortedTiers.indexOf(tier) + 1; x < sortedTiers.size(); ++x) {
                TagKey<Block> tag = sortedTiers.get(x).getIncorrectBlocksForDrops();
                if (state.is(tag)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static List<Tier> getTiersLowerThan(Tier tier) {
        return !isTierSorted(tier) ? List.of() : sortedTiers.stream().takeWhile((t) -> t != tier).toList();
    }



    private static void processTier(Tier tier, ResourceLocation name, List<Object> afters, List<Object> befores) {
        tiers.put(name, tier);

        for(Object after : afters) {
            ResourceLocation other = getTierName(after);
            edges.put(other, name);
        }

        for(Object before : befores) {
            ResourceLocation other = getTierName(before);
            edges.put(name, other);
        }
    }

    private static ResourceLocation getTierName(Object entry) {
        if (entry instanceof String s) {
            return ResourceLocation.parse(s);
        } else if (entry instanceof ResourceLocation rl) {
            return rl;
        } else if (entry instanceof Tier t) {
            return Objects.requireNonNull(getName(t), "Can't have sorting dependencies for tiers not registered in the TierSortingRegistry");
        } else {
            throw new IllegalStateException("Invalid object type passed into the tier dependencies " + entry.getClass());
        }
    }

    static boolean allowVanilla() {
        return !hasCustomTiers;
    }

    public static void init(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(TierSortingRegistry::playerLoggedIn);
        TierSortingRegistry.NetworkEvents.init(modBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            TierSortingRegistry.ClientEvents.init();
        }
    }

    public static PreparableReloadListener getReloadListener() {
        return new SimplePreparableReloadListener<JsonObject>() {
            final Gson gson = (new GsonBuilder()).create();

            protected @NotNull JsonObject prepare(@NotNull ResourceManager resourceManager, ProfilerFiller p) {
                Optional<Resource> res = resourceManager.getResource(TierSortingRegistry.ITEM_TIER_ORDERING_JSON);
                if (res.isEmpty()) {
                    return new JsonObject();
                } else {
                    try (Reader reader = res.get().openAsReader()) {
                        return this.gson.fromJson(reader, JsonObject.class);
                    } catch (IOException e) {
                        TierSortingRegistry.LOGGER.error("Could not read Tier sorting file {}", TierSortingRegistry.ITEM_TIER_ORDERING_JSON, e);
                        return new JsonObject();
                    }
                }
            }

            protected void apply(@NotNull JsonObject data, @NotNull ResourceManager resourceManager, ProfilerFiller p) {
                try {
                    if (!data.isEmpty()) {
                        JsonArray order = GsonHelper.getAsJsonArray(data, "order");
                        List<Tier> customOrder = new ArrayList<>();
                        for(JsonElement entry : order) {
                            ResourceLocation id = ResourceLocation.parse(entry.getAsString());
                            Tier tier = TierSortingRegistry.byName(id);
                            if (tier == null) {
                                throw new IllegalStateException("Tier not found with name " + id);
                            }

                            customOrder.add(tier);
                        }

                        List<Tier> missingTiers = TierSortingRegistry.tiers.values().stream().filter((tierx) -> !customOrder.contains(tierx)).toList();
                        if (!missingTiers.isEmpty()) {
                            Stream<String> missings = missingTiers.stream().map((tierx) -> Objects.toString(TierSortingRegistry.getName(tierx)));
                            throw new IllegalStateException("Tiers missing from the ordered list: " + missings.collect(Collectors.joining(", ")));
                        }

                        TierSortingRegistry.setTierOrder(customOrder);
                        return;
                    }
                } catch (Exception e) {
                    TierSortingRegistry.LOGGER.error("Error parsing Tier sorting file " + TierSortingRegistry.ITEM_TIER_ORDERING_JSON, e);
                }

                TierSortingRegistry.recalculateItemTiers();
            }
        };
    }

    private static void recalculateItemTiers() {
        MutableGraph<Tier> graph = GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).build();

        for(Tier tier : tiers.values()) {
            graph.addNode(tier);
        }

        edges.forEach((key, value) -> {
            if (tiers.containsKey(key) && tiers.containsKey(value)) {
                graph.putEdge(tiers.get(key), tiers.get(value));
            }

        });
        List<Tier> tierList = TopologicalSort.topologicalSort(graph, null);
        setTierOrder(tierList);
    }

    private static void setTierOrder(List<Tier> tierList) {
        runInServerThreadIfPossible((hasServer) -> {
            sortedTiers.clear();
            sortedTiers.addAll(tierList);
            if (hasServer) {
                syncToAll();
            }

        });
    }

    private static void runInServerThreadIfPossible(BooleanConsumer runnable) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(() -> runnable.accept(true));
        } else {
            runnable.accept(false);
        }

    }

    private static void syncToAll() {
        for(ServerPlayer serverPlayer : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            syncToPlayer(serverPlayer);
        }

    }

    private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player var2 = event.getEntity();
        if (var2 instanceof ServerPlayer serverPlayer) {
            syncToPlayer(serverPlayer);
        }

    }

    private static void syncToPlayer(ServerPlayer serverPlayer) {
        if (!serverPlayer.connection.getConnection().isMemoryConnection()) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncPacket(sortedTiers.stream().map(TierSortingRegistry::getName).toList()));
        }
    }

    static {
        ResourceLocation wood = ResourceLocation.parse("wood");
        ResourceLocation stone = ResourceLocation.parse("stone");
        ResourceLocation iron = ResourceLocation.parse("iron");
        ResourceLocation diamond = ResourceLocation.parse("diamond");
        ResourceLocation netherite = ResourceLocation.parse("netherite");
        ResourceLocation gold = ResourceLocation.parse("gold");
        processTier(Tiers.WOOD, wood, List.of(), List.of());
        processTier(Tiers.GOLD, gold, List.of(wood), List.of(stone));
        processTier(Tiers.STONE, stone, List.of(wood), List.of(iron));
        processTier(Tiers.IRON, iron, List.of(stone), List.of(diamond));
        processTier(Tiers.DIAMOND, diamond, List.of(iron), List.of(netherite));
        processTier(Tiers.NETHERITE, netherite, List.of(diamond), List.of());
        vanillaEdges.putAll(edges);
        sortedTiers = new ArrayList<>();
        sortedTiersUnmodifiable = Collections.unmodifiableList(sortedTiers);
        CHANNEL_NAME = Modularity.modularityPath("tier_sorting");
    }

    private record SyncPacket(List<ResourceLocation> tiers) implements CustomPacketPayload {
        public static final Type<SyncPacket> TYPE = new Type<>(CHANNEL_NAME);

        public static final StreamCodec<ByteBuf, SyncPacket> STREAM_CODEC = ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SyncPacket::new, SyncPacket::tiers);

        public void handle(IPayloadContext context) {
            setTierOrder(tiers.stream().map(TierSortingRegistry::byName).toList());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static class ClientEvents {
        public static void init() {
            NeoForge.EVENT_BUS.addListener(ClientEvents::clientLogInToServer);
        }

        private static void clientLogInToServer(ClientPlayerNetworkEvent.LoggingIn event) {
            if (!event.getConnection().isMemoryConnection()) {
                TierSortingRegistry.recalculateItemTiers();
            }

        }
    }

    private static class NetworkEvents {
        public static void init(IEventBus modBus) {
            modBus.addListener(NetworkEvents::registerPackets);
        }

        private static void registerPackets(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(Modularity.MODULARITY).versioned(PROTOCOL_VERSION);

            registrar.playToClient(SyncPacket.TYPE, SyncPacket.STREAM_CODEC, SyncPacket::handle);
        }
    }
}
