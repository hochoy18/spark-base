package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Cobub on 2018/6/1.
  */
object KVSparkTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[1]").setAppName("spark test1 ")
    val sc = new SparkContext(conf)
    val arr = List(("a",1),("b",2),("c",1),("d",2),("a",1),("b",2),("c",1),("d",2),("c",1),("d",2),("c",1),("d",2))
    val rdd = sc.parallelize(arr)
    val countbykeyRdd = rdd.countByKey()
    println("countbykey: " + countbykeyRdd + " is ")
    countbykeyRdd.foreach(x=>print(x + "  "))

    val countByValueRdd = rdd.countByValue()
    println("countByValueRdd  " + countByValueRdd )
    countByValueRdd.foreach(x=>print(x+"   "))

    val collectAsMapRdd = rdd.collectAsMap()
    print("collectAsMapRdd\n")
    collectAsMapRdd.foreach(x=>print(x+"  "))

    val collectRdd = rdd.collect()
    println("collectRdd")
    collectRdd.foreach(x=> print(x + " , "))

//    RDD[(String,Int)] dists = rdd.distinct(2)

  }
}

