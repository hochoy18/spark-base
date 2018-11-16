package com.hochoy.spark.rdd

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._

import scala.collection.mutable.ListBuffer

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月16日 9:23
  * @version :  V1.0
  */
object MapPartitionTest {

  val sc = createSparkContext("mapPartition test ")

  def main(args: Array[String]) {
    val rdd1 = sc.parallelize(List(1, 2, 3, 9, 8, 7, 4, 5, 6))
    val rdd2 = rdd1.repartition(2).mapPartitions(mapPart(_))
    rdd2.coalesce(3)
    println(rdd2.collect().mkString(","))
//    rdd1.mapPartitionsWithIndex(mapPart(_),true)
  }

  def mapPart(iterator: Iterator[Int]): Iterator[Int] = {
    val res = iterator.flatMap(x => {
      var pairList = ListBuffer[Int]()
      if (x > 4 && x %2 ==0) pairList += (x)
      pairList
    })
    res
  }

}
