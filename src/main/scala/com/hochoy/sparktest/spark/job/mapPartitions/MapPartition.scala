package com.hochoy.sparktest.spark.job.mapPartitions

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Cobub on 2018/6/11.
  *
  * spark mapPartition方法与map方法的区别
  * https://blog.csdn.net/vipyeshuai/article/details/51774117
  *
  *
  */
object MapPartition {

  val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[1]")
  val sc = new SparkContext(conf)
  val a = sc.parallelize(1 to 9, 3)

  def main(args: Array[String]) {

    var s1 = System.currentTimeMillis()
    mapTest()
    var s2 = System.currentTimeMillis()
    printf("mapTest  %d "  , s2 - s1)

     s1 = System.currentTimeMillis()
    mapPartitions()
    s2 = System.currentTimeMillis()
    printf("mapPartitions  %d  ", s2 - s1)

  }

  def mapPartitions() {
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
