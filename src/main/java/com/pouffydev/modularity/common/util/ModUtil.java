package com.pouffydev.modularity.common.util;

import net.minecraft.nbt.Tag;

public class ModUtil {

    public static boolean isNumeric(Tag tag) {
        byte type = tag.getId();
        return type == Tag.TAG_BYTE || type == Tag.TAG_SHORT || type == Tag.TAG_INT || type == Tag.TAG_LONG || type == Tag.TAG_FLOAT || type == Tag.TAG_DOUBLE;
    }
}
