package com.hochoy.cobub3_test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/07/31
 */


public class ReferenceCacheUtils<K,V> {

    //基于软引用实现的缓存，当内存不够使会自动释放缓存内容，以避免OOM
    private ConcurrentHashMap<K,InnerSoftReference<V>> cacheMap;

    // 引用队列，当GC执行后被回收的缓存对象的软引用将被入队，以方便从缓存池中清除失效的软引用。
    private ReferenceQueue<V> referenceQueue;//

    public ReferenceCacheUtils(){
        cacheMap = new ConcurrentHashMap<K,InnerSoftReference<V>>();
        referenceQueue = new ReferenceQueue<V>();
    }

    /**
     * 往缓存添加对象
     * @param key
     * @param value
     */
    public void put(K key,V value){
        cacheMap.put(key, new InnerSoftReference<V>(key, value, referenceQueue));
        //清除垃圾引用
        clearInvalidReference();
    }


    /**
     * @param
     * @return
     */
    public V get(K key){
        synchronized (ReferenceCacheUtils.class) {
            InnerSoftReference<V> ref = cacheMap.get(key);
            if ( ref != null ){
                return ref.get();
            }
        }
        return null;
    }

    public boolean contain(K key){
        return cacheMap.containsKey(key);
    }

    /**
     * 获取缓存大小
     * @return
     */
    public int size(){
        return cacheMap.size();
    }

    /**
     * 清除失效的软引用
     */
    @SuppressWarnings("unchecked")
    private void clearInvalidReference(){
        InnerSoftReference<V> innerSoftReference;
        while((innerSoftReference = (InnerSoftReference<V>) referenceQueue.poll()) != null){
            cacheMap.remove(innerSoftReference.getKey());//删除掉无效的软引用

        }
    }


    /**
     * 清空缓存
     */
    public void clear(){
        cacheMap.clear();
        clearInvalidReference();
    }



    private class InnerSoftReference<V> extends SoftReference<V> {

        private K key;
        public InnerSoftReference(K key, V value, ReferenceQueue<V> referenceQueue){
            super(value, referenceQueue);
            this.key = key;
        }

        public K getKey() {
            return key;
        }

    }

}