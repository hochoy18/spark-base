package com.hochoy.spark.sql.datasources.parquet

import com.hochoy.spark.utils.SparkUtils._
import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.EnhancedMethods._
import com.hochoy.spark.utils.JSON
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.dstream.DStream

object ParquetTest {



  def main(args: Array[String]): Unit = {
//    println(spark.conf.get("spark.master"))
//    readParquet
    streamingParquet

  }

  def readParquet(): Unit = {
    val spark = createSparkSession("ParquetTest")(2)
    val path = USER_SPARK_PATH + FILE_PATH + "users.parquet"
    val user = spark.read.parquet(path)
    user.show()
    user.createOrReplaceTempView("tmp_user")
    val users = spark.sql("select name ,favorite_numbers as numbers from tmp_user")
    users.show()
  }


  def streamingParquet(): Unit = {
//    val ssc = createSparkStreamingContext("StreamingParquet", 1L, 2)
//    val line = ssc.socketTextStream("localhost", 9999)
//    val words = line.flatMap(_.split(" "))
//
//    val pairs = words.map(w=>(w,1))
//    val wcs = pairs.reduceByKey(_+_)
//    wcs.print
//
//
    val ssc = createSparkStreamingContext("SocketTextStreamingTest",1,2)

    val lines = ssc.socketTextStream("localhost",9999)

    val words = lines.flatMap(_.split(" "))

    val pairs = words.map(w=>(w,1))
    val wcs = pairs.reduceByKey(_+_)
    wcs.print()





    val hochoy = cleanupData(lines)
    println(s"hochoy.count()  .....       ${hochoy.count()}")
    actionfun(hochoy)
    ssc.start()
    ssc.awaitTermination()



  }
  def actionfun(v:DStream[(String, Map[String, _])]): Unit ={
    v.foreachRDD((rdd:RDD[(String,Map[String,_])],time:Time) ⇒{
      println(s"time is $time")
      rdd.foreachPartition(it⇒{
        it.map{case (userid,json)⇒{
          val action = json("action").toString
          println(action)

        }

        }

      })
    })
  }
  def cleanupData(action: DStream[String]): DStream[(String, Map[String, _])] = {
    val keySets = Set("action", "appkey", "productid", "deviceid", "sessionid", "clienttime", "platform", "channelid", "version")
    action.flatMap(JSON.toListMap).filter { j ⇒
      j.containsKeysAndNotEmpty(keySets)
    }.map(json ⇒ {
      val userId = if (json.containsKeysAndNotEmpty("userid")) json("userid").toString else "null_userid"

      println(userId + "       " + json)

      (userId, json)
    })
  }

}
