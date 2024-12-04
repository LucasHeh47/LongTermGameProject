package com.lucasj.gamedev.mathutils;

import java.util.HashMap;
import java.util.Map;

public class BiMap<K, V> {
    private final Map<K, V> keyToValue = new HashMap<>();
    private final Map<V, K> valueToKey = new HashMap<>();

    public void put(K key, V value) {
        if (keyToValue.containsKey(key) || valueToKey.containsKey(value)) {
            throw new IllegalArgumentException("Either key or value already exists!");
        }
        keyToValue.put(key, value);
        valueToKey.put(value, key);
    }

    public V getValue(K key) {
        return keyToValue.get(key);
    }

    public K getKey(V value) {
        return valueToKey.get(value);
    }

    public void removeKey(K key) {
        V value = keyToValue.remove(key);
        if (value != null) {
            valueToKey.remove(value);
        }
    }

    public void removeValue(V value) {
        K key = valueToKey.remove(value);
        if (key != null) {
            keyToValue.remove(key);
        }
    }
}

