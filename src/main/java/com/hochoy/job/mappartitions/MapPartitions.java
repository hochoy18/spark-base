package com.hochoy.job.mappartitions;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Cobub on 2018/6/11.
 */
public class MapPartitions {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("MapPartitions.........").setMaster("local");
//        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
//            JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 6, 7, 8, 9), 3);
////            rdd.mapPartitions(new FlatMapFunction<Iterator<Integer>, Object>() {
//            });
//
//
//        }

    }

}
