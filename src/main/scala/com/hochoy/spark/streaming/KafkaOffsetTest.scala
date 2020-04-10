package com.hochoy.spark.streaming


import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaOffsetTest {

  //nc -l -p 9999

  //nc -l -p 9999
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("test")
      .set("spark.serializer",classOf[org.apache.spark.serializer.KryoSerializer].getName)



    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val brokers = "localhost:9092"// "tdhtest01:9092, tdhtest02:9092, tdhtest03:9092"
    val topics = Array("partitions3-topic")
    val kafkaParams = Map[String, Object](

      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false:java.lang.Boolean),
      ConsumerConfig.GROUP_ID_CONFIG ->"use_a_separate_group_id_for_each_stream"

//      "metadata.broker.list" -> brokers,
//      "serializer.class" -> "kafka.serializer.StringDecoder",

//      "auto.offset.reset" -> "smallest") // todo smallest
    )


    val dstream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(
      ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams))

    dstream.foreachRDD(p=>{
      p.map(r=>{
        println(r.key(),"\n",r.value())
      })
    })

    dstream.foreachRDD(p=>{
      p.collect().foreach(e=>println("xx" + e.key() + "\n" + e.value()))
    })

//    dstream.foreachRDD(rdd=>{
//      rdd.foreach(recood=>{
//        println("key : ",recood.key(),"     value :  " ,recood.value())
//      })
//    })


//    val fromOffsets: Map[TopicAndPartition, Long] = Map(TopicAndPartition("partitions3-topic",1) -> 1L)
//    val messageHandler = (mmd: MessageAndMetadata[Array[Byte], String]) => (mmd.key, mmd.message())
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
//    val kafkaStream: InputDStream[(Array[Byte], String)] =
//      KafkaUtils.createDirectStream[
//        Array[Byte],
//        String,
//        DefaultDecoder,
//        StringDecoder,
//        (Array[Byte], String)
//        ](
//        ssc,
//        kafkaParams,
//        fromOffsets,
//        messageHandler
//      )
//
//    kafkaStream.foreachRDD(p => {
//      p.foreachPartition(x => {
//        println("x........... ", x)
//      })
//    })


    ssc.start()
    ssc.awaitTermination()
  }
//  def getLastOffsets(kafkaParams:Map[String,Object],topics: String):Map[TopicAndPartition,Long] = {
//    import scala.collection.JavaConverters._
//
//    val prop = new Properties()
//    prop.putAll(kafkaParams.asJava)
//
//    val consumer = new KafkaConsumer[String,String](prop)
//    consumer.subscribe(topics)
//
//
//  }


}
