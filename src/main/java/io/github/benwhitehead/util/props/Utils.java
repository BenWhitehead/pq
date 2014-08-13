package io.github.benwhitehead.util.props;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Ben Whitehead
 */
public final class Utils {
    static Map<String, List<String>> parseArgs(@NotNull final String[] args) {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            switch (arg) {
                case "-":
                    appendToMultiMap(map, "in", "system.in");
                    break;
                case "-f":
                case "--file":
                    final String filename = args[i+1];
                    i++;
                    appendToMultiMap(map, "files", filename);
                    break;
                default:
                    appendToMultiMap(map, "props", arg);
                    break;
            }
        }
        return map;
    }

    static <A extends Comparable> TreeSet<A> newTreeSet(@NotNull final Collection<A> collection) {
        final TreeSet<A> retVal = new TreeSet<>();
        retVal.addAll(collection);
        return retVal;
    }

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
    public static <T extends Comparable<T>> List<T> sortedList(@NotNull final Collection<T> iter) {
        final List<T> ts = newArrayList(iter);
        Collections.sort(ts);
        return ts;
    }

    @NotNull
    public static <T> List<T> sortedList(@NotNull final Collection<T> iter, @NotNull final Comparator<T> comparator) {
        final List<T> ts = newArrayList(iter);
        Collections.sort(ts, comparator);
        return ts;

    }

    @NotNull
    public static <T> ArrayList<T> newArrayList(@NotNull final T e) {
        final ArrayList<T> list = new ArrayList<>();
        list.add(e);
        return list;
    }

    @NotNull
    public static <T> ArrayList<T> newArrayList(@NotNull final Collection<T> e) {
        final ArrayList<T> list = new ArrayList<>();
        list.addAll(e);
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
