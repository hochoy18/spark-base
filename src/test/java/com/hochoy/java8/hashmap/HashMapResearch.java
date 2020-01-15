package com.hochoy.java8.hashmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashMapResearch {


class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
    public Node(int hash, K key, V value,Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }
    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

}

}
