package com.hochoy.spark.streaming

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaOffsetTest {

  //nc -l -p 9999

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()

     

    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val brokers = "tdhtest01:9092, tdhtest02:9092, tdhtest03:9092"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers,
      "serializer.class" -> "kafka.serializer.StringDecoder",
      "auto.offset.reset" -> "smallest") // todo smallest
    val fromOffsets: Map[TopicAndPartition, Long] = Map(TopicAndPartition("actions-topic2",1) -> 1L)
    val messageHandler = (mmd: MessageAndMetadata[Array[Byte], String]) => (mmd.key, mmd.message())
    /**
      *
      * createDirectStream { @link  org.apache.spark.streaming.kafka.KafkaUtils#createDirectStream}
      * * @tparam K type of Kafka message key
      * * @tparam V type of Kafka message value
      * * @tparam KD type of Kafka message key decoder
      * * @tparam VD type of Kafka message value decoder
      * * @tparam R type returned by messageHandler
      * * @return DStream of R
      */
    val kafkaStream: InputDStream[(Array[Byte], String)] =
      KafkaUtils.createDirectStream[
        Array[Byte],
        String,
        DefaultDecoder,
        StringDecoder,
        (Array[Byte], String)
        ](
        ssc,
        kafkaParams,
        fromOffsets,
        messageHandler
      )

    kafkaStream.foreachRDD(p => {
      p.foreachPartition(x => {
        println("x........... ", x)
      })
    })


    ssc.start()
    ssc.awaitTermination()
  }

}
