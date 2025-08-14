package com.pouffydev.modularity.api.material.info;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.TextColor;

public record MaterialInfo(TextColor color, String assetName) {
    public static final Codec<MaterialInfo> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextColor.CODEC.fieldOf("color").forGetter(MaterialInfo::color),
            Codec.STRING.fieldOf("asset_name").forGetter(MaterialInfo::assetName)
    ).apply(instance, MaterialInfo::new));
}
