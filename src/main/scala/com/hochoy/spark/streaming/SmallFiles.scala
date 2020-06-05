package com.hochoy.spark.streaming

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SmallFiles {


  def apply: SmallFiles = new SmallFiles()
  def main(args: Array[String]): Unit = {


    apply.smallFiles();


  }
}

class SmallFiles{

  def smallFiles(): Unit ={
    val conf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(conf, Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "DESKTOP-HQQ4VIP:9092",
      "key.deserializer"->classOf[StringDeserializer],
      "value.deserializer"-> classOf[StringDeserializer],
      "group.id"->"1111",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit"->(false: java.lang.Boolean))

    val messages:DStream[ConsumerRecord[String,String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set("partitions3-topic"), kafkaParams))

    messages.foreachRDD(rdd=>{
      rdd.map(kv=>kv.value())
    })

  }

}