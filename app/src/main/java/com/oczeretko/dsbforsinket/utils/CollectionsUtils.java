package com.oczeretko.dsbforsinket.utils;

import android.support.v4.util.*;

import java.util.*;

public final class CollectionsUtils {
    private CollectionsUtils() {
    }

    public static <Source, Result> List<Result> map(Iterable<Source> sourceItems, Func<Source, Result> func) {
        ArrayList<Result> results = new ArrayList<>();
        for (Source s : sourceItems) {
            results.add(func.invoke(s));
        }

        return results;
    }

    public static <TSource> TSource firstOrNull(Iterable<TSource> sourceItems, Func<TSource, Boolean> predicate) {
        for (TSource s : sourceItems) {
            if (predicate.invoke(s)) {
                return s;
            }
        }

        return null;
    }

    public static <TSource> TSource first(Iterable<TSource> sourceItems, Func<TSource, Boolean> predicate) {
        for (TSource s : sourceItems) {
            if (predicate.invoke(s)) {
                return s;
            }
        }

        throw new IndexOutOfBoundsException("No matching element found");
    }

    public static <TSourceA, TSourceB> Collection<Pair<TSourceA, TSourceB>> zip(TSourceA[] sourceA, TSourceB[] sourceB) {
        return zip(Arrays.asList(sourceA), Arrays.asList(sourceB));
    }

    public static <TSourceA, TSourceB> Collection<Pair<TSourceA, TSourceB>> zip(Collection<TSourceA> sourceA, Collection<TSourceB> sourceB) {
        if (sourceA.size() != sourceB.size()) {
            throw new IllegalArgumentException();
        }

        ArrayList<Pair<TSourceA, TSourceB>> results = new ArrayList<>();
        Iterator<TSourceA> iteratorA = sourceA.iterator();
        Iterator<TSourceB> iteratorB = sourceB.iterator();

        while (iteratorA.hasNext() && iteratorB.hasNext()) {
            results.add(new Pair<>(iteratorA.next(), iteratorB.next()));
        }

        return results;
    }

    public static <TSource, TKey, TValue> Map<TKey, TValue> toMap(Iterable<TSource> sourceItems, Func<TSource, TKey> keyFunc, Func<TSource, TValue> valueFunc) {
        Map<TKey, TValue> result = new HashMap<>();
        for (TSource s : sourceItems) {
            result.put(keyFunc.invoke(s), valueFunc.invoke(s));
        }

        return result;
    }

    public static int indexOf(int[] array, int element) {
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == element) {
                return i;
            }
        }

        return -1;
    }

}
