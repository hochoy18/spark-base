package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Cobub on 2018/6/1.
  */
object FuncSparkTest {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[3]").setAppName("reduce")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(1 to 10)
    val rddAdd = rdd.reduce(_ + _)
    println("func+ : "+rddAdd)//func+ : 55

    val rddRed = rdd.reduce(_-_)
    println("func - :"+rddRed)//func - :-53
    //如果conf.setMaster("local[2]"),输出  func - :-15
    //如果conf.setMaster("local[3]"),输出  func - :-17

    val rddCount = rdd.count()
    println("count:"+rddCount)//count:10

    val rddFirst = rdd.first()
    println("first: "+rddFirst)//first: 1

    val rddtake = rdd.take(4)
    print("take4: " + rddtake + " : ")
    rddtake.foreach(x => print(x + "   "))//take4: [I@67110f71 : 1   2   3   4

    val rddTop = rdd.top(3)
    print("top3: " + rddTop + "==========  ")
    rddTop.foreach(x => print(x + "   "))//top3: [I@6e8a9c30==========  10   9   8

    val takeOrderRdd = rdd.takeOrdered(8)
    print("takeOrderRdd :  ")
    takeOrderRdd.foreach(x=> print(x + "  "))//takeOrderRdd :  1  2  3  4  5  6  7  8



    sc.stop();
  }
}
//SparkTest.main(null)
