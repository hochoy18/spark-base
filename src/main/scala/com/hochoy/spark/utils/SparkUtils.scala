package com.hochoy.spark.utils

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 10:12
  * @version :  V1.0
  */
private [spark] object SparkUtils {

  def hadoopHomeSet:String = System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + "\\..\\..\\hadoop-common-2.2.0-bin");

  def conf(appName: String): SparkConf = new SparkConf().setMaster("local").setAppName(appName)

  def createSparkContext(appName: String): SparkContext = {
    hadoopHomeSet
    val sc = new SparkContext(conf(appName))
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

  def createSparkStreamingContext(appName: String, seconds: Long): StreamingContext = {
    hadoopHomeSet
    val ssc = new StreamingContext(conf(appName), Seconds(seconds))
    ssc
  }

}