package com.hochoy.spark.utils

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}


/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 10:12
  * @version :  V1.0
  */
object SparkUtils {

  def hadoopHomeSet = System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + "\\..\\..\\hadoop-common-2.2.0-bin");

  def createSparkContext(appName: String): SparkContext = {
    hadoopHomeSet
    val conf = new SparkConf().setMaster("local").setAppName(appName)
    val sc = new SparkContext(conf)
    sc
  }

  def createSparkSession(appName: String, conf: SparkConf*): SparkSession = {
    hadoopHomeSet
    val spark = SparkSession.builder()
      .master("local")
      .appName(appName)
      .config("spark.some.config.option", "some-value")
      .config("spark.sql.warehouse.dir", "target/spark-warehouse")

    if (conf != null) {
      conf.foreach(x => spark.config(x))
    }
    spark.getOrCreate()
  }

}