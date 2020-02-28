package com.hochoy.spark.streaming

import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.streaming.dstream.DStream
object SocketTextStreamingTest {

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val ssc = createSparkStreamingContext("SocketTextStreamingTest",5,2)
    ssc.sparkContext.setLogLevel("ERROR")
    ssc.checkpoint("file:///opt/checkpoint")
    val lines = ssc.socketTextStream("localhost",9999)
    val result = lines.flatMap(_.split(" ")).map((_,1))

//    val value: DStream[(String, Int)] = result.updateStateByKey(updateFunction _).checkpoint(Seconds(10))
        wordcount(lines)
//    value.print()

//    println("========>")
//    value.foreachRDD(rdd=>{
//      rdd.keys.collect.foreach(println)
//    })
    ssc.start()
    ssc.awaitTermination()
  }
  def updateFunction(currentValues:Seq[Int],preValues:Option[Int]):Option[Int] = {
    val current = currentValues.sum
    val pre = preValues.getOrElse(0)
    Some(current + pre)
  }


  def wordcount(lines:DStream[String]):DStream[(String,Long)]={
    val counts = lines.flatMap(_.split(" ")).map(w⇒(w,1L)).reduceByKey(_+_)
    counts.print

    counts

  }

}
