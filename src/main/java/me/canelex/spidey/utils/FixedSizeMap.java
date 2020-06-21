package me.canelex.spidey.utils;

import java.util.HashMap;
import java.util.Map;

public class FixedSizeMap<K, V>
{
    private final Map<K, V> cache = new HashMap<>();
    private final K[] keys;
    private int currentIndex = 0;

    @SuppressWarnings("unchecked")
    public FixedSizeMap(final int size)
    {
        this.keys = (K[]) new Object[size];
    }

    public void put(final K key, final V value)
    {
        if (!containsKey(key))
        {
            if (keys[currentIndex] != null)
                remove(keys[currentIndex]);
            keys[currentIndex] = key;
            currentIndex = (currentIndex + 1) % keys.length;
        }
        cache.put(key, value);
    }

    public boolean containsKey(final K key)
    {
        return cache.containsKey(key);
    }

    public V get(final K key)
    {
        return cache.get(key);
    }

    public void remove(final K key)
    {
        cache.remove(key);
    }
}