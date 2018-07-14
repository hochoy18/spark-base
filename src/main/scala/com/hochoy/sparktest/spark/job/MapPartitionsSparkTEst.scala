package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Cobub on 2018/6/1.
  */
object MapPartitionsSparkTEst {

  def partitionsFun(iter:Iterator[(String,String)]):Iterator[String] = {
    var woman = List[String]()
    while (iter.hasNext){
      val next = iter.next()
      next match {
        case (_,"famale") => woman = next._1 :: woman
        case _=>
      }
    }
    return woman.iterator
  }

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local")
    val sc = new SparkContext(conf)
    val list = List(("abb","female"),("acc","male"),("add","male"),("aee","female"))
    val rdd = sc.parallelize(list)
    val mp = rdd.mapPartitions(x=>x.filter(_._2 == "female")).map(x=>x._1)
    mp.collect.foreach(x=>println(x))
  }
}
