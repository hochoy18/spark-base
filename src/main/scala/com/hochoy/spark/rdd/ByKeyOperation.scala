package com.hochoy.spark.rdd

import com.hochoy.spark.utils.SparkUtils

object ByKeyOperation {
  val sc = SparkUtils.createSparkContext(this.getClass.getName)
  //  sc.getConf.set("spark.io.compression.codec", "snappy")
  sc.setLogLevel("WARN")
  val score = List(
    ("math", 100), ("Chinese", 100),
    ("math", 90), ("Chinese", 98),
    ("hbase", 100), ("hadoop", 56),
    ("math", 100), ("hadoop", 34),
    ("hbase", 200))
  val rdd1 = sc.makeRDD(score)


}
