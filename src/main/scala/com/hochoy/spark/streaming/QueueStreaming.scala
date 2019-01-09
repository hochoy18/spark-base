package com.hochoy.spark.streaming

import com.hochoy.spark.utils.SparkUtils
import org.apache.spark.rdd.RDD

import scala.collection.mutable

object QueueStreaming {

  def main(args: Array[String]): Unit = {

    val ssc = SparkUtils.createSparkStreamingContext("QueueStreaming", 2, 2)

    val rddQueue = new mutable.Queue[RDD[Int]]()

    val input = ssc.queueStream(rddQueue)

    val map = input.map(x ⇒ {
      (x % 3, 1)
    })

    val reduce = map.reduceByKey(_ + _)

    reduce.print
    ssc.start()
    for (i ← 1 to 1000) {
      rddQueue.synchronized {
        rddQueue += ssc.sparkContext.makeRDD(1 to 1000, 2)
        Thread.sleep(1000L)
        println(s"i ....... $i")
      }
    }
//    ssc.stop()
  }


}
