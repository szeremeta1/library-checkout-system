package com.librarysystem.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A generic implementation of a HashMap using a hash table.
 * Uses separate chaining for collision resolution.
 */
public class HashMap<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMap() {
        this.table = new Entry[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Compute hash code for key
     */
    private int hash(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % table.length;
    }

    /**
     * Put key-value pair into map
     */
    public V put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if ((entry.key == null && key == null) || (entry.key != null && entry.key.equals(key))) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
            entry = entry.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;

        return null;
    }

    /**
     * Get value by key
     */
    public V get(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];

        while (entry != null) {
            if ((entry.key == null && key == null) || (entry.key != null && entry.key.equals(key))) {
                return entry.value;
            }
            entry = entry.next;
        }

        return null;
    }

    /**
     * Remove key-value pair
     */
    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if ((entry.key == null && key == null) || (entry.key != null && entry.key.equals(key))) {
                if (prev == null) {
                    table[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }

        return null;
    }

    /**
     * Check if key exists
     */
    public boolean containsKey(K key) {
        return get(key) != null || (key == null && get(key) == null && size > 0);
    }

    /**
     * Get all values
     */
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                values.add(entry.value);
                entry = entry.next;
            }
        }
        return values;
    }

    /**
     * Get all keys
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        return keys;
    }

    /**
     * Get size
     */
    public int size() {
        return size;
    }

    /**
     * Check if empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clear the map
     */
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    /**
     * Resize the hash table
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    /**
     * Inner class for hash table entries
     */
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
