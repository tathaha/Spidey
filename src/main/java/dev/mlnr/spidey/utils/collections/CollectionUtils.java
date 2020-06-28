package dev.mlnr.spidey.utils.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionUtils
{
    private CollectionUtils()
    {
        super();
    }

    private static <K, V> List<V> getValue(final Map<K, List<V>> map, final K key)
    {
        return map.computeIfAbsent(key, ignored -> new ArrayList<>());
    }

    public static <K, V> void add(final Map<K, List<V>> map, final K key, final V element)
    {
        getValue(map, key).add(element);
    }

    public static <K, V> boolean remove(final Map<K, List<V>> map, final K key, final V element)
    {
        return getValue(map, key).remove(element);
    }

    public static <K, V> boolean contains(final Map<K, List<V>> map, final K key, final V element)
    {
        return getValue(map, key).contains(element);
    }
}