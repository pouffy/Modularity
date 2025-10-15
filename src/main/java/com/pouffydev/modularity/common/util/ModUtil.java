package com.pouffydev.modularity.common.util;

import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.stream.Collectors;

public class ModUtil {

    public static boolean isNumeric(Tag tag) {
        byte type = tag.getId();
        return type == Tag.TAG_BYTE || type == Tag.TAG_SHORT || type == Tag.TAG_INT || type == Tag.TAG_LONG || type == Tag.TAG_FLOAT || type == Tag.TAG_DOUBLE;
    }

    public static String toIndentedStringList(Collection<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\t", "\n\t", ""));
    }
}
