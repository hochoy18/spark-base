package com.hochoy.sparktest.spark.job.broadcast

import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.immutable.HashMap

/**
  * Created by Cobub on 2018/6/1.
  * https://blog.csdn.net/baolibin528/article/details/54406049
  *
  * https://blog.csdn.net/lxhandlbb/article/details/51931713
  */
object BroadCastTest {

  def main(args: Array[String]) {

    val conf:SparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("local")

    val sc = new SparkContext(conf)

    val input = "E:\\work\\sparktest\\src\\main\\scala\\com\\cobub\\sparktest\\spark\\job\\broadcast\\broadcast.txt";
    val data = sc.textFile(input).map(_.split("\\|",100)).map(line =>{
      val Array(privateIP, account,timeFormat, timeType) = line
      (privateIP, (account, timeFormat.toLong, timeType.toInt))
    })

    var accountHash = new HashMap[String,Set[(String,Long,Int)]]()
    data.groupByKey().collect().foreach(x =>{
      accountHash +=(x._1 ->x._2.toSet)
    })
    val broadcast = sc.broadcast(accountHash)

    println(broadcast.id)
    println(broadcast.toString())

    val value = broadcast.value
    for (entry <- value){
      println(entry._1+ " : " +entry._2)
    }

  }
}
