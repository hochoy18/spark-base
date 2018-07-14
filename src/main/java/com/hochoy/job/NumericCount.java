package com.hochoy.job;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Cobub on 2018/5/31.
 */
public class NumericCount {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[2]");
        conf.setAppName("Test1");
        JavaSparkContext sc = null;
        try {
            sc = new JavaSparkContext(conf);
            List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
            JavaRDD<Integer> rdd = sc.parallelize(list,3);
            Integer i =  rdd.reduce(new Function2<Integer, Integer, Integer>() {
//                @Override
                public Integer call(Integer x, Integer y) throws Exception {
                    int sum = x +y;
                    System.out.println(Thread.currentThread().getName()+":  "+  x + " +  " +y + "  = "+sum);
                    return sum;
                }
            });
            System.out.println("________"+i);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (sc!=null)
                sc.close();
        }


    }
}
