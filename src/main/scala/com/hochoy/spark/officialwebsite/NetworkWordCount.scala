package com.hochoy.spark.officialwebsite

import java.io.File

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.DStream
object NetworkWordCount {

  System.setProperty("hadoop.home.dir", System.getProperty("user.dir") + File.separator + "hadoop-common-2.2.0-bin");
  val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")

  private val ssc: StreamingContext = new StreamingContext(conf,Seconds(5))
//  new StreamingContext(new SparkContext(),Seconds(1))
//
//  new StreamingContext(new SparkContext(),new Duration(1))
//
//  new StreamingContext(new SparkContext(), Durations.seconds(1))


  ssc.sparkContext.setLogLevel("WARN")
  def main(args: Array[String]): Unit = {


    // nc -l -p 9999
    val line: DStream[String] = ssc.socketTextStream("localhost",9999)


    val pairs: DStream[(String, Int)] = line  .flatMap(_.split(" "))  .map(word => (word, 1))

    val wc: DStream[(String, Int)] = pairs.reduceByKey(_+_)

    wc.print(10000)

    ssc.start() // Start receiving data and processing it

    ssc.awaitTermination()

    ssc.stop()

  }


}
