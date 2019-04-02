package com.hochoy.java8;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/2
 */
public class MapTest {


    /**
     * computeIfAbsent的方法有两个参数 第一个是所选map的key，第二个是需要做的操作。这个方法当key值不存在时才起作用。
     * {@link java.util.Map#computeIfAbsent(Object key, Function<? super Object, ? extends Object > mappingFunction)}
     *
     *
     * {@link java.util.Map#computeIfPresent}：
     * V computeIfPresent(K key, BiFunction < ? super K, ? super V, ? extends V> remappingFunction)
     * computeIfPresent 的方法,对 指定的 在map中已经存在的key的value进行操作。只对已经存在key的进行操作，其他不操作
     *
     */

    static Map<Integer, Integer> cache = new ConcurrentHashMap<>(10);

    public static void main(String[] args) {
        test2();


        System.exit(-1);
        mapComputeIfAbsent();
    }

    static void test2() {
        Map<String, Integer> wordCounts = new ConcurrentHashMap<>(10);
        String s =
                "Lorem ipsum dolor sit amet consetetur iam nonumy sadipscing " +
                        " elitr, sed diam nonumy eirmod tempor invidunt ut erat sed " +
                        "labore et dolore magna dolor sit amet aliquyam erat sed diam";

        wordCounts.put("sed", 0);
        System.out.println(wordCounts);
        for (String t : s.split(" ")) {
            wordCounts.compute(t, (k, v) -> {
                if (null == v) {
                    v = 0;
                }
                v = v + 1;
                return v;

            });
        }

        System.out.println(wordCounts);
        wordCounts.clear();
        System.out.println(wordCounts);
        wordCounts.put("sed", 0);
        wordCounts.put("aliquyam", 0);
        Arrays.asList(s.split(" ")).forEach(x -> wordCounts.computeIfPresent(x,(k,v)-> v+1));
        System.out.println(wordCounts);
    }

    static void test1() {
        System.out.println(cache);
        cache.computeIfAbsent(1, k -> new Integer(5));
        System.out.println(cache);
    }

    static void mapComputeIfAbsent() {
        cache.put(0, 0);
        cache.put(1, 1);

        System.out.println("fibonacci(7) = " + fibonncci(7));
    }

    static int fibonncci(int i) {
        if (i == 0 || i == 1) {
            return i;
        }
        System.out.println("calculating fibonacci( " + i + " ）");
        return fibonncci(i - 2) + fibonncci(i - 1);
    }
}
