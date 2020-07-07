package com.hochoy.spark.streaming





object SparkStreamingKafka_08_Case {
  import kafka.common.TopicAndPartition
  import kafka.message.MessageAndMetadata
  import kafka.serializer.StringDecoder
  import org.apache.kafka.clients.consumer.ConsumerConfig
  import org.apache.spark.rdd.RDD
  import org.apache.spark.storage.StorageLevel
  import org.apache.spark.streaming.dstream.{InputDStream, ReceiverInputDStream}
  import org.apache.spark.streaming.kafka.{Broker, KafkaUtils, OffsetRange}
  import org.apache.spark.streaming.{Seconds, StreamingContext}
  import org.apache.spark.{SparkConf, SparkContext}

  val sparkConf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("test")
    .set("spark.serializer", classOf[org.apache.spark.serializer.KryoSerializer].getName)


  val ssc = new StreamingContext(sparkConf, Seconds(10))
  ssc.checkpoint("checkpoint")
  def main(args: Array[String]): Unit = {


    val Array(zkQuorum, group, topics, numThreads) = args


    val topicMap: Map[String, Int] = topics.split(",").map((_, numThreads.toInt)).toMap
    ///////////////// TODO createStream
    val createStream1: ReceiverInputDStream[(String, String)] = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    ///////////////// TODO createStream
    val kafkaParams = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.GROUP_ID_CONFIG -> "groupid"
    )

    val createStream2: ReceiverInputDStream[(String, String)] =
      KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topicMap, StorageLevel.MEMORY_AND_DISK_SER_2)

    ///////////////// TODO  createRDD
    val sc: SparkContext = ssc.sparkContext
    val offsetRanges: Array[OffsetRange] = Array[OffsetRange](
      OffsetRange(TopicAndPartition("gd-event", 1), 1l, 2l),
      OffsetRange(TopicAndPartition("gd-cd", 1), 1l, 2l)
    )
    val kafkaRDD: RDD[(String, String)] = KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder](sc,
      kafkaParams,
      offsetRanges: Array[OffsetRange])


    val leaders: Map[TopicAndPartition, Broker] = Map()

    val messageHandler = (mmd: MessageAndMetadata[String, String]) => mmd.message
    // (mmd.key, mmd.message)
    ///////////////// TODO  createRDD
    val kafkaRDD2: RDD[String] = KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder, String](sc,
      kafkaParams,
      offsetRanges,
      leaders: Map[TopicAndPartition, Broker],
      messageHandler)
    ///////////////// TODO  createRDD
    val messageHandler1 = (mmd: MessageAndMetadata[String, String]) => (mmd.key, mmd.message)
    val kafkaRDD3: RDD[(String, String)] =
      KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder, (String, String)](sc,
        kafkaParams,
        offsetRanges,
        leaders: Map[TopicAndPartition, Broker],
        messageHandler1)

    ///////////////// TODO  createDirectStream

    val fromOffsets:Map[TopicAndPartition,Long] = Map()

    val directStream: InputDStream[String] = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder,String](
      ssc,kafkaParams,fromOffsets,messageHandler)

    val directStream1: InputDStream[(String,String)] =
      KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder,(String,String)](
        ssc,kafkaParams,fromOffsets,messageHandler1)

    val directStream2: InputDStream[(String, String)] =
      KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](
        ssc,kafkaParams,topicMap.keySet)




//    ssc.start()
//    ssc.awaitTermination()
  }


}


/*



object SparkStreamingKafka_010_Case{
  import org.apache.kafka.clients.consumer.ConsumerRecord
  import org.apache.kafka.common.serialization.StringDeserializer
  import org.apache.spark.SparkConf
  import org.apache.spark.streaming.dstream.InputDStream
  import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
  import org.apache.spark.streaming.kafka010._
  import org.apache.spark.streaming.{Seconds, StreamingContext}

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("test")
      .set("spark.serializer", classOf[org.apache.spark.serializer.KryoSerializer].getName)


    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("checkpoint")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092,anotherhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("topicA", "topicB")
    val stream :InputDStream[ConsumerRecord[String, String]] =
      KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.map(record => (record.key, record.value))
  }
}



*/

