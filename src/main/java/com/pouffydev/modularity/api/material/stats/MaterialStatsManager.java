package com.pouffydev.modularity.api.material.stats;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.data.MergingJsonDataLoader;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import com.pouffydev.modularity.common.util.ModUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MaterialStatsManager extends MergingJsonDataLoader<Map<ResourceLocation, JsonObject>> {
    private static final Gson GSON =
            (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceKey<ToolPartType<?>>, ToolPartType<?>> statTypes = new HashMap<>();
    private Map<ResourceKey<ToolMaterial>, Map<ResourceKey<ToolPartType<?>>, IToolPart>> materialToStatsPerType = Collections.emptyMap();

    private final Runnable onLoaded;

    public MaterialStatsManager(Runnable onLoaded) {
        super(GSON, "modularity/tool_materials/stats", id -> new HashMap<>());
        this.onLoaded = onLoaded;
    }

    public <T extends IToolPart> void registerStatType(ToolPartType<T> type) {
        statTypes.put(type.builtInRegistryHolder().key(), type);
    }

    public Collection<ResourceKey<ToolPartType<?>>> getAllStatTypeIds() {
        return statTypes.keySet();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends IToolPart> ToolPartType<T> getStatType(ResourceKey<ToolPartType<?>> id) {
        return (ToolPartType<T>) statTypes.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends IToolPart> Optional<T> getStats(ResourceKey<ToolMaterial> materialId, ResourceKey<ToolPartType<?>> statId) {
        Map<ResourceKey<ToolPartType<?>>, IToolPart> materialStats = materialToStatsPerType.getOrDefault(materialId, Map.of());
        IToolPart stats = materialStats.get(statId);
        // class will always match, since it's only filled by deserialization, which only puts it in if it's the registered type
        return Optional.ofNullable((T) stats);
    }

    public Collection<IToolPart> getAllStats(ResourceKey<ToolMaterial> materialId) {
        return materialToStatsPerType.getOrDefault(materialId, Map.of()).values();
    }

    public void updatePartStatsFromServer(Map<ResourceKey<ToolMaterial>, Collection<IToolPart>> materialStats) {
        this.materialToStatsPerType = materialStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .collect(Collectors.toMap(
                                        (m) -> m.getType().builtInRegistryHolder().key(),
                                        Function.identity()
                                )))
                );
        onLoaded.run();
    }

    @Override
    protected void parse(Map<ResourceLocation, JsonObject> builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException {
        MaterialStatsJson json = GSON.fromJson(element, MaterialStatsJson.class);
        for (Map.Entry<ResourceLocation,JsonElement> entry : json.getStats().entrySet()) {
            ResourceLocation key = entry.getKey();
            JsonElement valueElement = entry.getValue();
            if (valueElement.isJsonNull()) {
                builder.remove(key);
            } else {
                JsonObject value = GsonHelper.convertToJsonObject(valueElement, key.toString());
                JsonObject existing = builder.get(key);
                if (existing != null) {
                    for (Map.Entry<String,JsonElement> jsonEntry : value.entrySet()) {
                        existing.add(jsonEntry.getKey(), jsonEntry.getValue());
                    }
                } else {
                    builder.put(key, value);
                }
            }
        }
    }

    @Override
    protected void finishLoad(Map<ResourceLocation, Map<ResourceLocation, JsonObject>> map, ResourceManager manager) {
        materialToStatsPerType = map.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> ResourceKey.create(ModularityRegistries.TOOL_MATERIAL, entry.getKey()),
                        entry -> deserializeMaterialStatsFromContent(entry.getValue())));
        Modularity.LOGGER.debug("Loaded part stats for materials:{}",
                ModUtil.toIndentedStringList(materialToStatsPerType.entrySet().stream()
                        .map(entry -> String.format("%s - %s", entry.getKey(), Arrays.toString(entry.getValue().keySet().toArray())))
                        .collect(Collectors.toList())));
        onLoaded.run();
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        long time = System.nanoTime();
        super.onResourceManagerReload(manager);
        Modularity.LOGGER.info("{} part stats loaded for {} materials in {} ms",
                materialToStatsPerType.values().stream().mapToInt(stats -> stats.keySet().size()).sum(),
                materialToStatsPerType.size(), (System.nanoTime() - time) / 1000000f);
    }

    public UpdateMaterialStatsPayload getUpdatePacket() {
        Map<ResourceKey<ToolMaterial>, Collection<IToolPart>> networkPayload =
                materialToStatsPerType.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().values()));
        return new UpdateMaterialStatsPayload(networkPayload);
    }

    private Map<ResourceKey<ToolPartType<?>>, IToolPart> deserializeMaterialStatsFromContent(Map<ResourceLocation, JsonObject> contentsMap) {
        ImmutableMap.Builder<ResourceKey<ToolPartType<?>>, IToolPart> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonObject> entry : contentsMap.entrySet()) {
            ResourceKey<ToolPartType<?>> statType = ResourceKey.create(ModularityRegistries.TOOL_PART_TYPE, entry.getKey());
            ToolPartType<?> type = getStatType(statType);
            if (type == null) {
                Modularity.LOGGER.error("The part stat of type '" + statType + "' has not been registered");
                continue;
            }
            builder.put(statType, type.getCodec().codec().decode(JsonOps.INSTANCE, entry.getValue()).getOrThrow().getFirst());
        }
        return builder.build();
    }

    record MaterialStatsJson(@Nullable Map<ResourceLocation, JsonElement> stats) {

        @Override
        public Map<ResourceLocation, JsonElement> stats() {
            return Objects.requireNonNullElse(stats, Collections.emptyMap());
        }

        public Map<ResourceLocation, JsonElement> getStats() {
            return stats;
        }
    }

    public record UpdateMaterialStatsPayload(Map<ResourceKey<ToolMaterial>, Collection<IToolPart>> materialToStats) implements CustomPacketPayload {
        public static final Type<UpdateMaterialStatsPayload> TYPE = new Type<>(Modularity.modularityPath("material_part_stats"));

        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMaterialStatsPayload> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, UpdateMaterialStatsPayload>() {
            @Override
            public UpdateMaterialStatsPayload decode(RegistryFriendlyByteBuf buffer) {
                int materialCount = buffer.readInt();
                Map<ResourceKey<ToolMaterial>, Collection<IToolPart>> materialToStats = new HashMap<>(materialCount);
                for (int i = 0; i < materialCount; i++) {
                    ResourceKey<ToolMaterial> key = buffer.readResourceKey(ModularityRegistries.TOOL_MATERIAL);
                    int statCount = buffer.readInt();
                    List<IToolPart> partList = new ArrayList<>();
                    for (int j = 0; j < statCount; j++) {
                        try {
                            ToolPartType<?> partType = ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE).decode(buffer);
                            partList.add(partType.getStreamCodec().decode(buffer));
                        } catch (Exception e) {
                            Modularity.LOGGER.error("Could not deserialize part stat. Are client and server in sync?", e);
                        }
                    }
                    materialToStats.put(key, partList);
                }
                return new UpdateMaterialStatsPayload(materialToStats);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, UpdateMaterialStatsPayload payload) {
                buffer.writeInt(payload.materialToStats.size());
                payload.materialToStats.forEach((materialKey, parts) -> {
                    buffer.writeResourceKey(materialKey);
                    buffer.writeInt(parts.size());
                    parts.forEach(part -> encodePart(buffer, part, part.getType()));
                });
            }

            @SuppressWarnings("unchecked")
            private <T extends IToolPart> void encodePart(RegistryFriendlyByteBuf buffer, IToolPart part, ToolPartType<T> type) {
                ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE).encode(buffer, type);
                type.getStreamCodec().encode(buffer, (T) part);
            }
        };

        public void handle(IPayloadContext context) {
            MaterialStatsManager.updatePartStatsFromServer(this);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
