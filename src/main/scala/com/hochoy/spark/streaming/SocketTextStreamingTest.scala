package com.hochoy.spark.streaming

import com.hochoy.spark.utils.SparkUtils._
object SocketTextStreamingTest {

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val ssc = createSparkStreamingContext("SocketTextStreamingTest",1,2)

    val lines = ssc.socketTextStream("localhost",9999)

    val words = lines.flatMap(_.split(" "))

    val pairs = words.map(w=>(w,1))
    val wcs = pairs.reduceByKey(_+_)
    wcs.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
