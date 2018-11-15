package com.hochoy.spark.utils

import org.apache.spark.{SparkConf, SparkContext}


/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 10:12
  * @version :  V1.0
  */
object SparkUtils {

  def createSparkContext(appName: String): SparkContext = {
    System.setProperty("hadoop.home.dir", System.getProperty("user.dir")+"\\..\\..\\hadoop-common-2.2.0-bin");
    val conf = new SparkConf().setMaster("local").setAppName(appName)
    val sc = new SparkContext(conf)
    sc
  }

}