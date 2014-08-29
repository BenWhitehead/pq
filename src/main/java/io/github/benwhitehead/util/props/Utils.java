package io.github.benwhitehead.util.props;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Ben Whitehead
 */
public final class Utils {

    static void appendToMultiMap(@NotNull final Map<String, List<String>> map, @NotNull final String key, @NotNull final String newValue) {
        final List<String> list = getOrElse(map, key, new ArrayList<String>());
        list.add(newValue);
        map.put(key, list);
    }

    /**
     * @param map          The map to try and get the value from
     * @param key          The key query against
     * @param otherwise    The default value of the specified map does not contain the specified key
     * @param <K>          The type of the keys of the map
     * @param <V>          The Type of the values of the map
     * @return
     * For the specified {@code map} check if it contains {@code key}
     * <ul>
     *     <li>if {@code true}: return the value of that key from {@code map}</li>
     *     <li>if {@code false}: return the value {@code otherwise}</li>
     * </ul>
     */
    static <K, V> V getOrElse(@NotNull final Map<K, V> map, @NotNull final K key, @NotNull final V otherwise) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return otherwise;
        }
    }

    @NotNull
    public static <T> ArrayList<T> newArrayList(@NotNull final T e) {
        final ArrayList<T> list = new ArrayList<>();
        list.add(e);
        return list;
    }

    @NotNull
    public static <T> T head(@NotNull final List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Empty list");
        }
        return list.get(0);
    }

}
