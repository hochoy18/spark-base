package com.hochoy.spark.rdd

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月16日 14:05
  * @version :  V1.0
  */
object RddActionTest {

  val sc = createSparkContext("RDD Action operation ")

  val rdd1 = sc.textFile(USER_SPARK_PATH + "rdd\\README.md")

  def main(args: Array[String]) {
    reduceT
  }

  def reduceT(): Unit = {
    val rdd2 = rdd1.reduce(_ + _)
    println()
  }


}