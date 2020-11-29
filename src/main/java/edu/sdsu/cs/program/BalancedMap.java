package edu.sdsu.cs.program;

import java.util.LinkedList;
import java.util.TreeMap;

// Class to be implemented using built in data structures and algorithms of java.util
public class BalancedMap<K extends Comparable<K>, V> implements IMap<K, V> {

    private TreeMap<K, V> map;

    public BalancedMap() {
        super();
        this.map = new TreeMap<K, V>();
    }

    public BalancedMap(IMap<K, V> map) {
        this.map = new TreeMap<>();
        Iterable<K> keys = map.keyset();
        for (K k : keys)
            this.map.put(k, map.getValue(k));
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean add(K key, V value) throws ClassCastException, NullPointerException {
        return map.put(key, value) != null;
    }

    @Override
    public V delete(K key) {
        return map.remove(key);
    }

    @Override
    public V getValue(K key) throws ClassCastException, NullPointerException {
        return map.get(key);
    }

    @Override
    public K getKey(V value) {
        for (K key : map.keySet()) {
            V temp = map.get(key);
            if (value.equals(temp))
                return key;
        }
        return null;
    }

    @Override
    public Iterable<K> getKeys(V value) {
        LinkedList<K> list = new LinkedList<>();
        for (K key : map.keySet()) {
            V temp = map.get(key);
            if (value.equals(temp))
                list.add(key);
        }
        return (Iterable<K>) list;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();

    }

    @Override
    public Iterable<K> keyset() {
        return map.keySet();
    }

    @Override
    public Iterable<V> values() {
        return map.values();
    }

}
