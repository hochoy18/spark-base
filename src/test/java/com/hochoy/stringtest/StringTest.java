package com.hochoy.stringtest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/6/14
 */
public class StringTest {

    public static void main(String[] args) {
        test2();
    }
    static void test1(){
        String f = String.format("select %s  from  %s  where %s  group by %s","action,username","parquetTmpTable","age > 10 and age < 20","country");
        System.out.println(f);
    }
    static void test2(){

        StringJoiner joiner = new StringJoiner(", ");
        String string = "{ \"filter\": { \"conditions\": [{ \"type\": \"event.country\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"isRegion\": \"isFalse\", \"params\": [\"中国\"], \"inputForInt\": \"\", \"divForInt\": \"\" }, { \"type\": \"event.region\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"isRegion\": \"isFalse\", \"params\": [\"江苏\"], \"inputForInt\": \"\", \"divForInt\": \"\", \"input\": \"\" }, { \"type\": \"event.platform\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"isRegion\": \"isFalse\", \"params\": [\"sys_android01\"], \"inputForInt\": \"\", \"divForInt\": \"\", \"input\": \"\" }], \"relation\": \"and\" }, \"unit\": \"week\", \"from_date\": \"20190516\", \"by_fields\": [\"event.country\", \"event.region\", \"event.city\"], \"to_date\": \"20190614\", \"productId\": \"11088\", \"action\": [{ \"eventOriginal\": \"$appClick\", \"eventType\": \"acc\" }] }";
        JSONObject jo = JSONObject.parseObject(string);
        JSONArray ja = jo.getJSONObject("filter").getJSONArray("conditions");
        ja.forEach(x ->{
            JSONObject j =JSONObject.parseObject(String.valueOf(x));
            System.out.println(j);
            joiner.add(j.getString("type"));
        });

        StringBuilder builder = new StringBuilder();
        System.out.println(joiner);
        String string1 = "abc";
        String string2 = new String("abc");
        String string3 = "abc";

//== 相当于判断两个地址是否一样
//        字符串中equals方法 比较的是 两个字符串中得每一个字符

        System.out.println(string1 == string2);  //false
        System.out.println(string1 == string3);  //true
        System.out.println(string1.equals(string2));  //true
        Collectors.joining();

//        string1 和 string2 有什么不同 分别代表几个对象?
//                string1是一个对象  常量池里的一个对象abc
//        string2是两个对象  "abc"是一个对象 又new了一个堆区的对象
    }

    static void test00(){
        Set set ;
        Map  map;
        List list;
        TreeMap treeMap;


        /**
         * {@link TreeMap}
         */
        TreeSet treeSet;
        HashSet hashSet; // 底层以 HashMap实现
        LinkedHashSet linkedHashSet; // 继承了HashSet 底层 以 LinkedHashMap 实现


        java.util.Hashtable hashTable; // 官方建议使用 ConcurrentHashMap

        /**
         *
         * {@link Map}
         * {@link TreeMap}
         *
         * {@link   HashMap }
         *
         * {@link LinkedHashMap}
         *
         */

        /**
         * {@linkplain Queue}
         *
         * {@linkplain Deque}
         *
         * {@linkplain LinkedList}
         *
         * {@linkplain AbstractQueue}
         *
         * {@linkplain PriorityQueue}
         * {@linkplain java.util.concurrent.ConcurrentLinkedQueue}
         *
         * {@linkplain java.util.concurrent.LinkedBlockingQueue}
         * {@linkplain java.util.concurrent.LinkedBlockingDeque}
         *
         * {@linkplain java.util.concurrent.PriorityBlockingQueue}
         * {@linkplain java.util.concurrent.ArrayBlockingQueue}
         * {@linkplain java.util.concurrent.DelayQueue}
         * {@linkplain java.util.concurrent.SynchronousQueue}
         *
         *
         *
         */



    }

    static void sort(){


    }


}
