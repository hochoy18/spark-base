package com.hochoy.sparktest.spark.job.broadcast

import org.apache.spark.streaming.{StreamingContext, Duration}
import org.apache.spark.{SparkConf, SparkContext, Accumulator}
import org.apache.spark.broadcast.Broadcast

/**
  * Created by Cobub on 2018/7/22.
  */
object BroadcastAccumulatorStreaming {

  private var broadcastList: Broadcast[List[String]] = _

  private var accumulator: Accumulator[Int] = _

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setMaster("local[4]").setAppName("broadcasttest")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Duration(2000))

    broadcastList = ssc.sparkContext.broadcast(List("Hadoop", "Mahout", "Hive"))
    accumulator = ssc.sparkContext.accumulator(0, "broadcasttest")
    val input = System.getProperty("user.dir") + "\\src\\main\\scala\\com\\hochoy\\sparktest\\spark\\job\\broadcast\\wc.txt"

    val lines = ssc.textFileStream(input)
    val words = lines.flatMap(line => {
      line.split(" ")
    })

    val wordPair = words.map(word => (word, 1))
    wordPair.filter(record => {
      broadcastList.value.contains(record._1)
    })

    val pair = wordPair.reduceByKey(_ + _)
    pair.foreachRDD(rdd => {
      rdd.foreach(x=>println("......"+x._1+"   :   "+x._2))
    })
    println("b累加器的值" + accumulator.value)

//    pair.foreachRDD(rdd => {
//      rdd.filter(x => {
//        if (broadcastList.value.contains(x._1)) {
//          accumulator.add(1)
//          return true
//        } else
//          return false
//      })
//    })

    val filtedpair = pair.filter(record => {
      if (broadcastList.value.contains(record._1)) {
        accumulator.add(record._2)
        true
      } else {
        false
      }

    }).print

    println("累加器的值"+accumulator.value)

    pair.filter(record => {broadcastList.value.contains(record._1)})
    val keypair = pair.map(pair => (pair._2,pair._1))
    keypair.transform(rdd => {
      rdd.sortByKey(false)//TODO
    })
    pair.print()
    ssc.start()
    ssc.awaitTermination()
    println("c累加器的值" + accumulator.value)
  }
}
