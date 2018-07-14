package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Cobub on 2018/6/1.
  */
object MapSparkTest {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[1]").setAppName(this.getClass.getName)

    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(1 to 5)
    val map = rdd.map(_ * 2)
    map.foreach(println)
    sc.stop()
  }
}
