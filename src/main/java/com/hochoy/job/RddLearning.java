package com.hochoy.job;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Cobub on 2018/6/1.
 */
public class RddLearning {
    private static Logger logger = LoggerFactory.getLogger(RddLearning.class);

    /**
     * single transform
     */
    public static void singleOperateRdd() {

        Integer[] nums = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<Integer> list = Arrays.asList(nums);

        JavaRDD<Integer> listRdd = new JavaSparkContext(new SparkConf().setAppName("singleOperateRdd").setMaster("local")).parallelize(list);
        JavaRDD<Integer> mapRDD = listRdd.map(new Function<Integer, Integer>() {
            //            @Override
            public Integer call(Integer v1) throws Exception {
                return v1 + 1;
            }
        });

    }


}
