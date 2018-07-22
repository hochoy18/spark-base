package com.hochoy.spark.sharedVariables;

import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Cobub on 2018/7/22.
 */
public class BroadcastAccumulator {

    private static volatile Broadcast<List<String>> broadcasts = null;
    private static volatile Accumulator<Integer> accumulator = null;

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local[2]")
                .setAppName("WC-onlineBroadcast");
        JavaSparkContext sc = new JavaSparkContext(conf);

        broadcasts = sc.broadcast(Arrays.asList("Hadoop", "Mahout", "Hive"));
        accumulator = sc.accumulator(0, "onlineBlackListCounter");


        String input = System.getProperty("user.dir") + "/src/main/java/com/hochoy/spark/sharedVariables/wc.txt";
        JavaRDD<String> lines = sc.textFile(input);
        JavaPairRDD<String, Integer> pairs = lines.mapToPair(new PairFunction<String, String, Integer>() {

            public Tuple2<String, Integer> call(String word) {
                return new Tuple2<String, Integer>(word, 1);

            }
        });
        final JavaPairRDD<String, Integer> wordsCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        wordsCount.foreach(new VoidFunction<Tuple2<String, Integer>>() {
            public void call(Tuple2<String, Integer> tuple2) throws Exception {
                if (broadcasts.value().contains(tuple2._1)){
                    accumulator.add(tuple2._2);

                }

            }
        });
        wordsCount.collect();
        System.out.println("广播器里面的值"+broadcasts.value());
        System.out.println("计时器里面的值"+accumulator.value());



    }
}
