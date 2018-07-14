//package com.hochoy.job.pair;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.*;
//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONObject;
//import scala.Int;
//import scala.Tuple2;
//
//import java.util.*;
//
///**
// * Created by Cobub on 2018/6/7.
// */
//public class PairRDDCreate1 {
//
//    static JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local").setAppName("PairRDD"));
//
//    public static void main(String[] args) {
////        wcList();
////        wcFIle();
//        jsonFile();
//
////        transformation();
////        transformation1();
//        sc.stop();
//
//    }
//
//
//    static void transformation1() {
//
//        List<Tuple2<Integer, Integer>> mapList = new ArrayList<>();
//        Tuple2<Integer, Integer> map = new Tuple2<>(1, 2);
//        mapList.add(map);
//        map = new Tuple2<>(3, 4);
//        mapList.add(map);
//
//        map = new Tuple2<>(3, 6);
//        mapList.add(map);
//
//        JavaPairRDD pairRDD = sc.parallelizePairs(mapList);
//        System.out.println("------" + pairRDD.collect());
//
//        JavaPairRDD reducebykey = pairRDD.reduceByKey(new Function2() {
//            @Override
//            public Object call(Object v1, Object v2) throws Exception {
//                return (Integer) v1 + (Integer) v2;
//            }
//        });
//        System.out.println("reducebykey========" + reducebykey.collect());
//
//        System.out.println("groupbykey========" + pairRDD.groupByKey().collect());
//
////        pairRDD.combineByKey()
//
//        JavaPairRDD mapValues = pairRDD.mapValues(new Function() {
//            @Override
//            public Object call(Object v1) throws Exception {
//                return (Integer) v1 + 4;
//            }
//        });
//        System.out.println("mapValues======" + mapValues.collect());
//
//        System.out.println("sortByKey========" + pairRDD.sortByKey().collect());
//
//        map = new Tuple2<>(3, 100);
//
//        mapList = new ArrayList<>();
//        ;
//        mapList.add(map);
//        JavaPairRDD other = sc.parallelizePairs(mapList);
//
//        System.out.println("subtractByKey=====" + pairRDD.subtractByKey(other).collect());
//        System.out.println("subtractByKey=====" + other.subtractByKey(pairRDD).collect());
//        System.out.println("join========" + pairRDD.join(other).collect());
//        System.out.println("join========" + other.join(pairRDD).collect());
//
//
//        System.out.println("rightOuterJoin========" + pairRDD.rightOuterJoin(other).collect());
//
//        System.out.println("leftOuterJoin......." + pairRDD.leftOuterJoin(other).collect());
//
//        System.out.println("cogroup......." + pairRDD.cogroup(other).collect());
//
//
////        JavaPairRDD flatMapvalues = pairRDD.flatMapValues(new Function() {
////            @Override
////            public Object call(Object v1) throws Exception {
////                int x = 5;
////                while ((Integer) v1 < x) {
////                    v1 =(Integer)v1 +1;
////                    return v1;
////                }
////
////                return v1;
////            }
////        });
////
////        System.out.println("flatmapvalues=="+flatMapvalues.collect());
//
//
//    }
//
//    static void transformation() {
//        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("haha", "heihei"));
//        JavaPairRDD<String, Integer> line = rdd.mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) throws Exception {
//                return new Tuple2(s, 1);
//            }
//        });
//        System.out.println("line.........." + line.collectAsMap());
//
//
//        List<Tuple2<String, Integer>> lt2 = new ArrayList<>();
//        Tuple2<String, Integer> t2 = new Tuple2<>("pina", 2);
//        lt2.add(t2);
//        t2 = new Tuple2<>("gank", 3);
//        lt2.add(t2);
//        t2 = new Tuple2<>("dsa", 3);
//        lt2.add(t2);
//        t2 = new Tuple2<>("gank", 3);
//        lt2.add(t2);
//        t2 = new Tuple2<>("dsa", 3);
//        lt2.add(t2);
//
//        t2 = new Tuple2<>("gank", 2);
//        lt2.add(t2);
//
//        JavaPairRDD<String, Integer> pairRDD = sc.parallelizePairs(lt2);
//        JavaPairRDD rbk = pairRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 + v2;
//            }
//        });
//        System.out.println("reduceByKey............." + rbk.collectAsMap());
//
//        System.out.println("groupByKey.........." + pairRDD.groupByKey().collectAsMap());
//
////        pairRDD.combineByKey(new Function<Integer, Object>() {
////        });
//
//        JavaPairRDD r = pairRDD.mapValues(new Function<Integer, Integer>() {
//            public Integer call(Integer v1) throws Exception {
//                return v1 + 2;
//            }
//
//        });
//        System.out.println("mapValues..........." + r.collectAsMap());
//
//
//    }
//
//    static void jsonFile() {
//        final JavaRDD<String> lines = sc.textFile("E:\\work\\sparktest\\src\\main\\java\\com\\hochoy\\sparktest\\spark\\job\\pair\\Razor.1528355554262.log");
//
//        JavaPairRDD<String, String> pairRDD = lines.mapToPair(new PairFunction<String, String, String>() {
//            @Override
//            public Tuple2<String, String> call(String s) throws Exception {
//                return new Tuple2<String, String>(s, s);
//            }
//        });
//        long count = lines.count();
//        System.out.println("count......." + count);
//
//        Function<Tuple2<String, String>, Boolean> longWordFilter =
//                new Function<Tuple2<String, String>, Boolean>() {
//                    public Boolean call(Tuple2<String, String> keyValue) {
//                        return (keyValue._2().length() > 20);
//                    }
//                };
//        JavaPairRDD<String, String> result = pairRDD.filter(longWordFilter);
//        System.out.println("ddddddddddddd     " + result.collect());
//
//
//        JavaRDD<String> input = sc.textFile("E:\\work\\sparktest\\src\\main\\java\\com\\hochoy\\sparktest\\spark\\job\\pair\\wc.txt");
//
//        JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterator<String> call(String s) throws Exception {
//                List<String> list = Arrays.asList(s.split(" "));
//                Iterator<String> iterator = list.iterator();
//                return iterator;
//            }
//        });
//
//        JavaPairRDD<String, Integer> result1 = words.mapToPair(
//                new PairFunction<String, String, Integer>() {
//                    @Override
//                    public Tuple2<String, Integer> call(String s) throws Exception {
//                        return new Tuple2<String, Integer>(s, 1);
//                    }
//                }
//
//        ).reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 + v2;
//            }
//        });
//        System.out.println("wc........" + result1.collect());
//
//
//    }
//
//    static void wcFIle() {
//
//        JavaRDD<String> lines = sc.textFile("E:\\work\\sparktest\\src\\main\\java\\com\\hochoy\\sparktest\\spark\\job\\pair\\wc.txt");
//        JavaRDD<String> flatMapRdd = lines.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterator<String> call(String s) throws Exception {
//                List<String> list = Arrays.asList(s.split(" "));
//                Iterator<String> iterator = list.iterator();
//                return iterator;
//            }
//        });
//        System.out.println("flatMapRdd===========" + flatMapRdd.collect());
//
//        PairFunction<String, String, Integer> keydata = new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) throws Exception {
//                return new Tuple2<>(s, 1);
//            }
//        };
//
//        JavaPairRDD<String, Integer> pairs = flatMapRdd.mapToPair(keydata);
//        System.out.println("pairsRDD============" + pairs.collect());
//
//        JavaPairRDD<String, Integer> count = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 + v2;
//            }
//        });
//        System.out.println("count===========" + count.collectAsMap());
//
//
//    }
//
//    static void wcList() {
//
//        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("aaaaaaa", "bbbbbbbb", "bbbbbbbb", "ddddddd"));
//
//        JavaPairRDD<String, Integer> pairRDD = rdd.mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) throws Exception {
//                return new Tuple2<String, Integer>(s, 1);
//            }
//        });
//        JavaPairRDD d = pairRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer v1, Integer v2) throws Exception {
//                return v1 + v2;
//            }
//        });
//        List l = d.collect();
//        System.out.println(l);
//
//
//    }
//}
