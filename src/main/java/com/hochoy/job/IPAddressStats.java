//package com.hochoy.job;
//
///**
// * Created by Cobub on 2018/5/31.
// */
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.Function2;
//import org.apache.spark.api.java.function.PairFunction;
//import org.apache.spark.api.java.function.VoidFunction;
//import scala.Serializable;
//import scala.Tuple2;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class IPAddressStats implements Serializable {
//
//    public static void main(String[] args) throws Exception {
//        SparkConf conf = new SparkConf();
//        conf.setMaster("local[1]");
//        conf.setAppName("Spark WordCount written by Java");
//        JavaSparkContext sc = null;
//
//        try {
//            sc = new JavaSparkContext(conf);
//            JavaRDD<String> lines = sc.textFile("E:/hu.txt");
//            JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
//                @Override
//                public Iterable<String> call(String s) throws Exception {
//                    return Arrays.asList(s.split(" "));
//                }
//            });
//            System.out.println("=======================");
//            List<String> wordlist = words.collect();
//            for (int i =0 ;i<wordlist.size();i++){
//                System.out.println(i + "   :  "+wordlist.get(i));
//            }
//
//            JavaPairRDD<String, Integer> paris = words.mapToPair(new PairFunction<String, String, Integer>() {
//                @Override
//                public Tuple2<String, Integer> call(String word) throws Exception {
//                    return new Tuple2<String, Integer>(word, 1);
//                }
//            });
//            JavaPairRDD<String,Integer>wordCount = paris.reduceByKey(new Function2<Integer, Integer, Integer>() {
//                @Override
//                public Integer call(Integer x, Integer y) throws Exception {
//                    return x + y;
//                }
//            });
//            System.out.println("!!!!!!!!!!!!!!!!!!!!");
//           wordCount.foreach(new VoidFunction<Tuple2<String, Integer>>() {
//               @Override
//               public void call(Tuple2<String, Integer> pairs) throws Exception {
//                   System.out.println(pairs._1+":"+pairs._2);
//               }
//           });
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (sc != null) {
//                sc.close();
//                sc.stop();
//            }
//        }
//
//
//
//
//    }
//
//
//}