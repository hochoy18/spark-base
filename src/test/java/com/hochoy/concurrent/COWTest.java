package com.hochoy.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月20日 11:25
 */

public class COWTest {

    public static void main(String[] args) {

    }

}

class BlackListServiceImpl {
    private static CopyOnWriteMap<String, Boolean> blackListMap = new CopyOnWriteMap<String, Boolean>(1000);

    static boolean isBlackList(String id){
        return blackListMap.get(id) == null ? false:true;
    }

    static void addBlackList(String id,Boolean value){
        blackListMap.put(id,value);
    }
    static void addBlackList(Map<String,Boolean> blacks){
        blackListMap.putAll(blacks);
    }
}

class CopyOnWriteMap<K, V> extends HashMap<K, V> implements Cloneable {
    private volatile Map<K, V> internalMap;

    public CopyOnWriteMap() {
        internalMap = new HashMap<K, V>();
    }
    public CopyOnWriteMap(int i){
        internalMap = new HashMap<K, V>(i);
    }

    public V put(K key, V value) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<K, V>(internalMap);
            V val = newMap.put(key, value);
            internalMap = newMap;
            return val;
        }
    }

    public V get(Object key) {
        return internalMap.get(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<K, V>(internalMap);
            newMap.putAll(m);
            internalMap = newMap;
        }
    }

}