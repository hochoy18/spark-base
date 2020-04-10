package com.hochoy.spark.streaming


import com.hochoy.spark.utils.{JSON, Utils}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.hochoy.spark.utils.EnhancedMethods._

object KafkaOffsetTest {

  //nc -l -p 9999

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("test")
      .set("spark.serializer", classOf[org.apache.spark.serializer.KryoSerializer].getName)


    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val brokers = "localhost:9092"
    val topics = Array("partitions3-topic")
    val kafkaParams = Map[String, Object](

      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean),
      ConsumerConfig.GROUP_ID_CONFIG -> "use_a_separate_group_id_for_each_stream"

      //      "metadata.broker.list" -> brokers,
      //      "serializer.class" -> "kafka.serializer.StringDecoder",

      //      "auto.offset.reset" -> "smallest") // todo smallest
    )


    val dStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(
      ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))

    dStream.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {

        val res0: RDD[(String, String, String, String, String, String, Boolean)] = rdd.map(record => (record.key(), record.value()))
          .flatMap(e => JSON.toListMap(e._2))
          .filter(j => j.containsKeysAndNotEmpty("country", "productid", "servertime"))
          .mapPartitions(p => {
            p.map { case json =>
              val action = json("action").toString
              val productId = json("productid").toString
              val deviceId = json("deviceid").toString
              val userId = json("userid").toString
              val sessionId = json("sessionid").toString
              val serverTime = json("servertime").toString
              val isUpdate = Utils.parseBoolean(json.getOrElse("is_update", null))

              (action, productId, deviceId, userId, sessionId, serverTime, isUpdate)
            }
          })


        res0.collect().foreach(e => printf("action : %s,productId : %s , deviceId : %s ,userId:  %s sessionId : %s, serverTime : %s , is_update : %s%n"
          , e._1,e._2,e._3,e._4,e._5,e._6,e._7))



      }

    })

//    dStream.foreachRDD(p => {
//      p.collect().foreach(e => println("xx" + e.key() + "\n" + e.value()))
//    })


    ssc.start()
    ssc.awaitTermination()
  }


}
