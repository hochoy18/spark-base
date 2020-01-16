package com.hochoy.java8.hashmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashMapResearch {


    public static void main(String[] args) {
        mapTestKey();

    }

    /**
     * 测试 key的hashCode 值一致时 Map是以链表的形式存储的
     */
    static  void mapTestKey(){
        Map map = new HashMap<MapTestKey,String>();
        map.put(new MapTestKey("111"),"111");
        map.put(new MapTestKey("1111"),"111");
        map.put(new MapTestKey("222"),"112");
        map.put(new MapTestKey("222222"),"111");
        map.put(new MapTestKey("223"),"113");
        map.put(new MapTestKey("224"),"114");
        map.put(new MapTestKey("1113"),"113");
        System.out.println(map);
    }




    class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

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

class MapTestKey{
    private String key_test;

    public MapTestKey(String key_test) {
        this.key_test = key_test;
    }

    @Override
    public String toString() {
        return "MapTestKey{" +
                "key_test='" + key_test + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return key_test.length();
    }
}