package com.hochoy.spark.streaming.structured
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object StructuredStreaming {
  val spark = SparkSession
    .builder()
    .appName("StructuredNetworkWordCount")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    if (args.length < 3){
      System.exit(-1)
    }

    args(0) match {
      case "kafka" => kafka(args)
      case _ => socket(args)
    }



  }
  def kafka(args: Array[String]):Unit={
    val df: DataFrame = spark.readStream
      .format("kafka") // 设置 kafka 数据源
      .option("kafka.bootstrap.servers", "DESKTOP-HQQ4VIP:9092")
      .option("subscribe", "partitions3-topic") // 也可以订阅多个主题:   "topic1,topic2"
      .load


    df.writeStream
      .outputMode("update")
      .format("console")
      .trigger(Trigger.Continuous(1000))
      // timestamp显示全
      .option("truncate",false)
      .start
      .awaitTermination()



//
//    val df: DataFrame = spark.readStream
//      .format("kafka") // 设置 kafka 数据源
//      .option("kafka.bootstrap.servers", "localhost:9092")
//      .option("subscribe", "partitions3-topic") // 也可以订阅多个主题:   "topic1,topic2"
//      .load
//
//    df.writeStream
//      .outputMode("update")
//      .format("console")
//      .trigger(Trigger.Continuous(1000))
//      // timestamp显示全
//      .option("truncate",false)
//      .start
//      .awaitTermination()


  }


  def testSocket(args:Array[String]):Unit ={
    val socketDF = spark
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    socketDF.isStreaming    // Returns True for DataFrames that have streaming sources

    socketDF.printSchema

    val userSchema = new StructType().add("name", "string").add("age", "integer")
    val csvDF = spark
      .readStream
      .option("sep", ";")
      .schema(userSchema)      // Specify schema of the csv files
      .csv("/path/to/directory")


  }

  def socket(args: Array[String]):Unit={
    val host = args(1)
    val port = args(2)



    val lines = spark.readStream
    .format("socket")
    .option("host", host)
    .option("port", port)
    .load()
    // Split the lines into words
    val words: Dataset[String] = lines.as[String].flatMap(_.split(" "))
//    println("====================words==================")
//    words.show(100)
//    words.writeStream
//      .outputMode("complete")
//      .format("console")
//      .start()

    // Generate running word count
    val wordCounts: DataFrame = words.groupBy("value").count()
//    println("====================wordCount==================")
//    wordCounts.show(100)


    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()

  }

}
