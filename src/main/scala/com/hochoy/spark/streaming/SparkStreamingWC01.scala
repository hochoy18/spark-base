package com.hochoy.spark.streaming

import java.io.{BufferedReader, InputStreamReader}

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01_WC {

  def main(args: Array[String]): Unit = {

    testReceiver
  }

  def testReceiver(): Unit ={
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(SparkStreaming01_WC.getClass.getName)

    val ssc = new StreamingContext(conf,Seconds(5))

    val ds: ReceiverInputDStream[String] = ssc.receiverStream(new TestReceiver[String]("localhost",9999))

    val res: DStream[(String, Int)] = ds
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    res.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def socketTextStream:Unit={
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(SparkStreaming01_WC.getClass.getName)

    val ssc = new StreamingContext(conf,Seconds(5))

    val ds: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",9999)

    val res: DStream[(String, Int)] = ds
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    res.print()

    ssc.start()
    ssc.awaitTermination()
  }


  def textFileStream: Unit ={
    val conf = new SparkConf()
      .setAppName(this.getClass.getName)
      .setMaster("local[*]")
      .set("spark.ui.port","4050")

    val ssc = new StreamingContext(conf,Seconds(5))

    val ds: DStream[String] = ssc.textFileStream("target/generated-sources")

    ds.foreachRDD(rdd=>{
      rdd.flatMap(line=>line.split(" "))
        .map((_,1))
        .reduceByKey(_+_)
        .foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
class TestReceiver[T]( host:String,
                       port :Int,
                       storageLevel: StorageLevel = StorageLevel.MEMORY_ONLY
                     ) extends Receiver[String](storageLevel) {
  import java.net.Socket
  private var socket:Socket = _


  def onStart(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = receive()
    }).start()
  }

  def receive(): Unit = {
    socket = new Socket(host,port)
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream,"UTF-8"))

    var line :String = null
    while ((line = reader.readLine()) != null){
      if ("END".equals(line)) return
        this.store(line)
    }
  }


  def onStop(): Unit = {
    if (socket != null)
      socket.close()
  }
}
