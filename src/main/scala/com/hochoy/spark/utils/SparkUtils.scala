package com.hochoy.spark.utils

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import com.hochoy.spark.utils.Constants._

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 10:12
  * @version :  V1.0
  */
private[spark] object SparkUtils {

  def hadoopHomeSet: String = System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + "\\..\\..\\hadoop-common-2.2.0-bin");

  def conf(appName: String, num: Int = 1): SparkConf = {
    val conf = new SparkConf().setAppName(appName)
    if (num == 1)
      conf.setMaster("local")
    else conf.setMaster(s"local[${num}]")
    conf
  }

  def createSparkContext(appName: String): SparkContext = {
    hadoopHomeSet
    val sc = new SparkContext(conf(appName))
    sc
  }

  def createSparkSession(appName: String, conf: SparkConf*)(implicit num:Int = 1): SparkSession = {
    hadoopHomeSet
    val spark = SparkSession.builder()
      .master(s"local[${num}]")
      .appName(appName)
      .config("spark.some.config.option", "some-value")
      .config(SPARK_SQL_WAREHOUSE_DIR, "target/spark-warehouse")

    if (conf != null) {
      conf.foreach(x => spark.config(x))
    }
    spark.getOrCreate()
  }

  def createSparkStreamingContext(appName: String, seconds: Long, num: Int = 1): StreamingContext = {
    hadoopHomeSet
    val ssc = new StreamingContext(conf(appName, num), Seconds(seconds))
    ssc
  }

}