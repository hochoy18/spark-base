//package com.hochoy.job;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.PairFunction;
//import org.apache.spark.api.java.function.VoidFunction;
//import scala.Tuple2;
//
//import java.util.Arrays;
//
///**
// * Created by Cobub on 2018/6/1.
// */
//public class WordCount {
//
//    public static void main(String[] args) {
//        SparkConf conf = new SparkConf().setMaster("local").setAppName("word count");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        JavaRDD<String> wcRDD = sc.textFile("E:\\hu.txt");
//        wccal(wcRDD);
//
//        sc.stop();
//
//    }
//    public static void wccal(JavaRDD<String> wcrdd){
//        JavaRDD<String> wordFlatMap = wcrdd.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterable<String> call(String s) throws Exception {
//                return Arrays.asList(s.split(" "));
//            }
//        });
//        JavaPairRDD<String,Integer> wcMap2Pair = wordFlatMap.mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) throws Exception {
//                return new Tuple2<String, Integer>(s,1);
//            }
//        });
//
//        JavaPairRDD<String,Integer> wcReduceByKey = wcMap2Pair.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 +v2;
//            }
//        });
//
//        wcReduceByKey.sortByKey().foreach(new VoidFunction<Tuple2<String, Integer>>() {
//            @Override
//            public void call(Tuple2<String, Integer> t) throws Exception {
//                System.out.println("key: " + t._1 + " , value: "+t._2);
//            }
//        });
//
//    }
//
//
//}
