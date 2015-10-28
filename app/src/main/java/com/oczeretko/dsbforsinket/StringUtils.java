package com.oczeretko.dsbforsinket;

import java.util.*;

public final class StringUtils {

    private StringUtils() {
    }

    public static <T> String join(String delimiter, T[] strings) {
        return join(delimiter, Arrays.asList(strings));
    }

    public static <T> String join(String delimiter, Iterable<T> strings) {

        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter");
        }

        if (strings == null) {
            throw new IllegalArgumentException("strings");
        }

        Iterator<T> iterator = strings.iterator();
        if (!iterator.hasNext()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(delimiter).append(iterator.next());
        }

        return builder.toString();
    }
}