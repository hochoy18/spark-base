package com.hochoy.sparktest.spark.job

import org.apache.spark.{SparkContext, SparkConf}
/**
  * Created by Cobub on 2018/6/1.
  */
object FlatMapSparkTest {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[1]")
    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(1 to 4)
    rdd.map(x=>1 to x*2).foreach(x=> print(x+ "  "))
    println("-------------")
    val fm = rdd.flatMap(x => (1 to x*2))
    fm.foreach(x=> print(x+ "  "))
    sc.stop()
  }
}
//FlatMapSparkTest.main(null)
