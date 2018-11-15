package com.hochoy.spark.rdd

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.rdd.RDD

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 10:27
  * @version :  V1.0
  */
object TransformRdd {
  val sc = createSparkContext("Transform operation")
  val rddInt: RDD[Int] = sc.parallelize(List(1, 3, 5, 7, 9, 8, 6, 4, 2))
  val rddStr: RDD[String] = sc.parallelize(Array("a", "c", "e", "g", "f", "d", "b"))
  val rddFile: RDD[String] = sc.textFile(USER_SPARK_PATH + "rdd\\README.md")

  def main(args: Array[String]) {
    println(rddFile.flatMap(_.split(" ")).collect().mkString(" + "))
    println(rddFile.flatMap(_.split(" ")).distinct().collect().mkString(" - "))
    println(rddInt.map(_.toString).union(rddStr).collect().mkString(" | "))
    println(rddInt.collect().mkString(","))
    println(rddInt.sortBy(x=>x).collect().mkString(" <  "))
    println(rddInt.sortBy(intSort(_)).collect().mkString("  -  "))
    println(rddInt.sortBy(intSort(_)).sortBy(x=>x).collect().mkString(" <<  "))
  }

  def intSort(v: Int): Int ={
    if (v > 5) v
    else v + 2
  }


}