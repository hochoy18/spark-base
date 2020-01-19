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
        map.put(new MapTestKey("1110"),"1110");
        map.put(new MapTestKey("1111"),"1111");
        map.put(new MapTestKey("1112"),"1112");
        map.put(new MapTestKey("1113"),"1113");
        map.put(new MapTestKey("1114"),"1114");
        map.put(new MapTestKey("1115"),"1115");
        map.put(new MapTestKey("1116"),"1116");
        map.put(new MapTestKey("1117"),"1117");
        map.put(new MapTestKey("1118"),"1118");
        map.put(new MapTestKey("1119"),"1119");
        map.put(new MapTestKey("1120"),"1120");
        map.put(new MapTestKey("1121"),"1121");
        map.put(new MapTestKey("1122"),"1122");
        map.put(new MapTestKey("1123"),"1123");
        map.put(new MapTestKey("1124"),"1124");
        map.put(new MapTestKey("1125"),"1125");
        map.put(new MapTestKey("1126"),"1126");
        map.put(new MapTestKey("1127"),"1127");
        map.put(new MapTestKey("1128"),"1128");
        map.put(new MapTestKey("1129"),"1129");
        map.put(new MapTestKey("1130"),"1130");
        map.put(new MapTestKey("1131"),"1131");
        map.put(new MapTestKey("1132"),"1132");
        map.put(new MapTestKey("1133"),"1133");
        map.put(new MapTestKey("1134"),"1134");
        map.put(new MapTestKey("1135"),"1135");



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
   static class MapTestKey{
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
}

