package com.hochoy.sparktest.spark.job.accumulator

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Cobub on 2018/6/1.
  * https://blog.csdn.net/baolibin528/article/details/54406049
  */
object AccumulatorDemo {

  def main(args: Array[String]) {
    val conf: SparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("local")
    val sc: SparkContext = new SparkContext(conf)
    val accum = sc.accumulator(0) //统计奇数的个数
    val sum = sc.parallelize(Array(1, 2, 3, 4, 5, 6, 7, 8, 9), 1).filter(n => {
        if (n % 2 != 0) accum += 1
        n % 2 == 0
      }).reduce(_ + _)
    println("sum:  "+ sum)
    println("accum:   "+ accum.value)
  }
}
