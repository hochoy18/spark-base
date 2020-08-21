package com.hochoy.spark.streaming


import java.io.{BufferedReader, InputStreamReader}

import com.hochoy.utils.{DBUtils, HochoyUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, LocationStrategies}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object SparkStreaming01_WC {

  def main(args: Array[String]): Unit = {

    testForEachRddForConnection()
    //    testReceiver
  }

  /**
    * 在 foreachPartition / mapPartitions 中 获取外部连接，进行操作，
    * 诸如： DB ，HBase 等。
    */
  def testForEachRddForConnection(): Unit = {
    import org.apache.spark.streaming.kafka010.KafkaUtils


    val conf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(SparkStreaming01_WC.getClass.getName)
      //      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.serializer", classOf[org.apache.spark.serializer.KryoSerializer].getName)
    // https://blog.csdn.net/zj__zfh/article/details/86658304
    // java.io.NotSerializableException: org.apache.kafka.clients.consumer.ConsumerRecord

    val ssc = new StreamingContext(conf, Seconds(5))

    val props = HochoyUtils.getProperties("consumer.properties")


    //    val kafkaParams : Map[String, AnyRef] = Map(
    //      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> props.get("bootstrap.servers"),
    //      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> props.get("auto.offset.reset"),
    //      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    //      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    //      ConsumerConfig.GROUP_ID_CONFIG -> "xxxxxxxx")
    import scala.collection.JavaConverters._
    val kafkaParams: mutable.Map[String, AnyRef] = props.asScala.map(kv => (kv._1.toString, kv._2))

    val inputDStream: DStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set("partitions3-topic"), kafkaParams))
      .cache()


    inputDStream.foreachRDD(rdd => {
      rdd.foreachPartition(p => {
        p.foreach(e => {
          printf("key: %s, value : %s, offset : %s, partition: %s, timestamp : %s, timestampType : %s%n",
            e.key(), e.value(), e.offset(), e.partition(), e.timestamp(), e.timestampType().toString)
        })
        println("========================")
      })
    })
    println("==============$$$$$$$$$$$$$$$$$$$$$$$$==========")
    inputDStream.foreachRDD(rdd => {
      rdd.foreachPartition(p => {
        //Connection connection = createNewConnection();

        /**
          * ***************************** DB Operate start **********************************************
          */
        val conn = DBUtils.getConnection
        val res: mutable.Buffer[java.util.Map[String, AnyRef]] = HochoyUtils.query(conn, "select * from mysql_offset").asScala

        res.foreach(e => {
          val it: java.util.Iterator[java.util.Map.Entry[String, AnyRef]] = e.entrySet().iterator()
          while (it.hasNext) {
            val entry: java.util.Map.Entry[String, AnyRef] = it.next()
            println(s"======================key: ${entry.getKey} , value : ${entry.getValue}")
          }
        })

        /**
          * ***************************** DB Operate end  **********************************************
          */
        /**
          * ***************************** business start   **********************************************
          */
        p.foreach { e =>
          printf(">>>>>>>>>>>>>>>>>>key : %s ,value : %s%n ", e.key(), e.value())
        }

        /**
          * ***************************** business end   **********************************************
          */
        DBUtils.returnConnection(conn)
      })

    })

    ssc.start()
    ssc.awaitTermination()
  }


  def testReceiver(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(SparkStreaming01_WC.getClass.getName)

    val ssc = new StreamingContext(conf, Seconds(5))

    val ds: ReceiverInputDStream[String] = ssc.receiverStream(new TestReceiver[String]("localhost", 9999))

    val res: DStream[(String, Int)] = ds
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    res.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def socketTextStream(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(SparkStreaming01_WC.getClass.getName)

    val ssc = new StreamingContext(conf, Seconds(5))

    val ds: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)

    val res: DStream[(String, Int)] = ds
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    res.print()

    ssc.start()
    ssc.awaitTermination()
  }


  def textFileStream(): Unit = {
    val conf = new SparkConf()
      .setAppName(this.getClass.getName)
      .setMaster("local[*]")
      .set("spark.ui.port", "4050")

    val ssc = new StreamingContext(conf, Seconds(5))

    val ds: DStream[String] = ssc.textFileStream("target/generated-sources")

    ds.foreachRDD(rdd => {
      rdd.flatMap(line => line.split(" "))
        .map((_, 1))
        .reduceByKey(_ + _)
        .foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()
  }

}

class TestReceiver[T](host: String,
                      port: Int,
                      storageLevel: StorageLevel = StorageLevel.MEMORY_ONLY
                     ) extends Receiver[String](storageLevel) {

  import java.net.Socket

  private var socket: Socket = _


  def onStart(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = receive()
    }).start()
  }

  def receive(): Unit = {
    socket = new Socket(host, port)
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))

    var line: String = null
    while ((line = reader.readLine()) != null) {
      if ("END".equals(line)) return
      this.store(line)
    }
  }


  def onStop(): Unit = {
    if (socket != null)
      socket.close()
  }
}
