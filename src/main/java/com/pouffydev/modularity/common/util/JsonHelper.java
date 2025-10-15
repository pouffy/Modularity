package com.pouffydev.modularity.common.util;

import net.minecraft.resources.ResourceLocation;

public class JsonHelper {

    public static String localize(String path, String folder, String extension) {
        return path.substring(folder.length() + 1, path.length() - extension.length());
    }

    public static ResourceLocation localize(ResourceLocation location, String folder, String extension) {
        return location.withPath(localize(location.getPath(), folder, extension));
    }
}
