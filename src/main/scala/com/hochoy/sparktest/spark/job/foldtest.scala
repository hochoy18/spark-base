package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Cobub on 2018/7/11.
  */
object foldtest {
  val conf = new SparkConf().setMaster("local[3]").setAppName("reduce")
  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    val rdd = sc.parallelize(Array(1,2,3))
    val reduce = rdd.reduce((x,y)=>{println("........x:"+x +", y:"+y );x+y})
    println(reduce)

    val rdd1 = sc.parallelize(Array(1))
    val reduce1 =  rdd1.reduce((x,y)=>{println("........x:"+x +", y:"+y );x+y})
    println(reduce1)

    val fold = rdd1.fold(0)((x,y)=>{println("........x:"+x +", y:"+y );x+y})
    println(fold)

  }


}
