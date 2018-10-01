package com.hochoy.spark.rdd

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by hochoy on 2018/9/15.
  */
object WordCount {

  val conf = new SparkConf().setMaster("local").setAppName("wc")
  val sc = new SparkContext(conf)
  val rdd = sc.parallelize(1 to 10)
}
