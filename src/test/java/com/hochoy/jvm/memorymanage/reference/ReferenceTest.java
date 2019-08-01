package com.hochoy.jvm.memorymanage.reference;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/07/30
 */
public class ReferenceTest {

    public static void main(String[] args) {
        Service s = new Service();
        Cache<Long, Person> c = new Cache<Long, Person>() {
            @Override
            public Person init(Long key) {
                return s.getPerson(key);
            }

        };

        for(long i = 0;;i++) {
            c.add(s.getPerson(i));
        }

    }


}
class Service {
    public Person getPerson(long id){
        Person p = new Person();
        p.setId(id);
        return p;
    }
}
class Person implements Cacheable<Long> {

    private long id;
    private String name;
    private boolean sex;
    private int age;

    @Override
    public Long cacheID() {
        return id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
abstract class Cache<X, T extends Cacheable<X>> {
    /**
     * Define how to initiate a T according to X.
     */
    public abstract T init(X key);

    public static final int MAX_REF_SIZE = 6000;

    public Cache() {
        refMap = new ConcurrentHashMap<X, MyRef>();
        refQueue = new ReferenceQueue<T>();
    }

    private class MyRef extends SoftReference<T> {

        private X id;

        public MyRef(T o, ReferenceQueue<T> q) {
            super(o, q);
            this.id = o.cacheID();
        }

    }

    private ConcurrentHashMap<X, MyRef> refMap;
    private ReferenceQueue<T> refQueue;

    public void add(T o) {
        if (refMap.size() > MAX_REF_SIZE) {
            clean();
        }
        MyRef r = new MyRef(o, refQueue);
        refMap.put(r.id, r);
    }

    public T get(X key) {
        MyRef r = refMap.get(key);
        if (r != null) {//if there are key and object in the cache
            T o = r.get();
            if (o != null)
                return o;
        }
        T o = init(key);//else execute init()
        this.add(o);
        return o;
    }

    @SuppressWarnings("unchecked")
    public void clean() {
//        new Thread() {
//            public void run() {
        MyRef r = null;
        while ((r = (MyRef) refQueue.poll()) != null) {
            refMap.remove(r.id);
        }
        System.out.println(refMap.size());
//            }
//        }.start();
    }

    public void clear() {
        refMap.clear();
        System.gc();
        System.runFinalization();
    }
}

interface Cacheable<X> {
    X cacheID();
}