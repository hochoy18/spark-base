package com.hochoy.spark.streaming

import com.hochoy.spark.utils.SparkUtils
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka.KafkaUtils

object DirectKafkaWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length < 3){
      System.err.println(
        s"""
           |Usage: DirectKafkaWordCount <brokers>  <groupId>   <topics>
           |  <brokers> is a list of one or more kafka brokers
           |  <groupId> is a consumer group name to consume from topic
           |  <topic> is a list of one or more Kafka topics to consume from topic
           |
         """.stripMargin)
      System.exit(1)
    }
    val Array(brokers,groupId,topics) =args
    val ssc = SparkUtils.createSparkStreamingContext("DirectKafkaWordCount",2l,2)
    val topicSet = topics.split(",").toSet
    val kafkaParam = Map[String,Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG ->brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )

//    val messages = KafkaUtils.createDirectStream[String,String](
//      ssc, LocationStrategies.PreferConsistent,
//      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))
  }


}
