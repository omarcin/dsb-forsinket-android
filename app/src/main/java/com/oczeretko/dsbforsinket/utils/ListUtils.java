package com.oczeretko.dsbforsinket.utils;

import java.util.*;

public final class ListUtils {
    private ListUtils() {
    }

    public static <Source, Result> List<Result> map(Iterable<Source> sourceItems, MapFunc<Source, Result> func) {
        ArrayList<Result> results = new ArrayList<>();
        for (Source s : sourceItems) {
            results.add(func.invoke(s));
        }

        return results;
    }

    public interface MapFunc<Source, Result> {
        Result invoke(Source item);
    }
}
