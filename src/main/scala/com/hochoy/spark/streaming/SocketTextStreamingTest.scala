package com.hochoy.spark.streaming

import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.streaming.dstream.DStream
object SocketTextStreamingTest {

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val ssc = createSparkStreamingContext("SocketTextStreamingTest",1,2)

    val lines = ssc.socketTextStream("localhost",9999)
    wordcount(lines)
    ssc.start()
    ssc.awaitTermination()
  }
  def wordcount(dStream:DStream[String]):DStream[(String,Long)]={
    val counts = dStream.flatMap(_.split(" ")).map(w⇒(w,1L)).reduceByKey(_+_)
    counts.print
    counts
  }
}
