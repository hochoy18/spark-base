package com.hochoy.spark.rdd

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
/**
  * Created by hochoy on 2018/9/15.
  */
object CreateRdd extends App{

  val sc = createSparkContext("createRdd")
  val rdd = sc.textFile(USER_SPARK_PATH+"rdd/README.md")
  val rdd1 = rdd.flatMap(x=>{x.split(" ")}).map((_,1))
  rdd1.foreach(x=>println(x._1,x._2))
  val rdd2 = sc.parallelize(List("hello","world","good","morning"))
  rdd2.map(x=>(x,1)).foreach(y=>println(y._1,y._2))

  val rdd3 = sc.makeRDD(List("hello","world","good","morning"))
  rdd3.map((_,3)).foreach(x=>(print(x._1,x._2)))

  val rdd4 = sc.makeRDD(List(1,2,3,4,5,6,7))
  println(rdd4.map(Math.pow(_,2)).collect().mkString(","))
}
