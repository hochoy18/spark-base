package com.hochoy.sparktest.spark.job.mapPartitions

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Cobub on 2018/6/11.
  */
object MapPartition {

  val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[1]")
  val sc = new SparkContext(conf)
  val a = sc.parallelize(1 to 9, 3)

  def main(args: Array[String]) {

    mapTest()
    mapPartions()


  }

  def mapPartions() {
    def doubleFunc(iter: Iterator[Int]) : Iterator[(Int,Int)] = {
      var res = List[(Int,Int)]()
      while (iter.hasNext)
      {
        val cur = iter.next;
//        res   .::(cur,cur*2)
        res .::= (cur,cur*2)
//        res
      }
      res.iterator
    }

    println("@@@@@@@@@@@@@"+a.mapPartitions(doubleFunc).collect().mkString)

//    def doubleFunc(iter : Iterator[(Int,Int)]) = {
//      var res = List[(Int,Int)]()
//      while (iter.hasNext){
//        val  cur = iter.next()
//        res .::= (cur,cur*2)
//      }
//      res.iterator
//    }

  }

  def mapDoubleFunc(a: Int): (Int, Int) = (a, a * 2)
  def mapTest() {
    val mapResult = a.map(mapDoubleFunc)
    println("..........."+mapResult.collect().mkString)
  }



}
