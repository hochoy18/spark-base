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
        readParquet
//    streamingParquet

  }

  def readParquet(): Unit = {
    val spark = createSparkSession("ParquetTest")(2)
    val path = USER_SPARK_PATH + FILE_PATH + "emptyDir"
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
    val ssc = createSparkStreamingContext("SocketTextStreamingTest", 1, 2)

    val lines = ssc.socketTextStream("localhost", 9999)

    val hochoy = cleanupData(lines)
    println(s"hochoy.count()  .....       ${hochoy.count()}")
    actionfun(hochoy)
    ssc.start()
    ssc.awaitTermination()


  }

  //{"data":[{"modulename":"G10","havebt":"true","phonenumber":"18958241528","devicename":"HTC A810e","network":"EDGE","platform":"Android","havegps":"true","os_version":"2.3.3","version":"2.1","isjailbreak":"1","latitude":"28.8464092000","event_identifier":"bottom_menu_blogrefresh","imsi":"525052342342345","havegravity":"true","useridentifier":"18958241528","lac":"39402","appkey":"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~","language":"zh_CN","wifimac":"74:ea:3a:41:a1:4c","havewifi":"true","longitude":"121.1300102000","mccmnc":"46001","ismobiledevice":"true","deviceid":"359990000000467","time":"2018-12-03 14:43:34","resolution":"1536*864","cellid":"4707"}]}#ip#197.3.156.14
  def cleanUp(cdlines: DStream[String]): DStream[Map[String, _]] = {
    cdlines.flatMap(line ⇒ {
      val data = if (line.contains("#ip#")) {
        val cd = line.split("#ip#", 2)(0)
        val ip = if (line.split("#ip#", 2)(1).indexOf(',') > 0) {
          line.split("#ip#", 2)(1)
        } else {
          line.split("#ip#", 2)(1).split(",", 2)(0)
        }
        val cdmap = JSON.toListMap(cd)
        val area = Map("country"→"china","province"→"jiangsu","county"→"nanjing")
        cdmap ++ area

      } else {
        (line, null)
      }
      JSON.toListMap(line)
    })
  }


  def actionfun(v: DStream[(String, Map[String, _])]): Unit = {
    v.foreachRDD((rdd: RDD[(String, Map[String, _])], time: Time) ⇒ {
      println(s"time is $time")
      rdd.foreachPartition(it ⇒ {
        it.map { case (userid, json) ⇒ {
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


object o {
  def singleSum (data :Array[Range.Inclusive] ):Int ={
    // 在例子中，假设传递的参数是 Array((1 to 5), (2 to 6), (3 to 7))
    // 我们假设看成三个分区 三个分区的数据分别是  ptn0： 1 to 5, ptn1: 2 to 6, ptn2: 3 to 7
    var sum = 0;
    data.foreach(partition___ ⇒ {     // partition___ 相当于我们的三个ptn
      var sum_partition = 0;          // 相当于 ：aggregate[U: ClassTag](zeroValue: U)(seqOp: (U, T) => U, combOp: (U, U) => U)  zeroValue = 0
      partition___.foreach(element_in_partition ⇒
        //每个partition中求和
        sum_partition = sum_partition + element_in_partition
      )
      println(s"sum_partition   $sum_partition")
      sum += sum_partition
    })
    println(s"sum。。。。。。。   $sum")
    sum
  }

  def main(args: Array[String]): Unit = {
    val data = Array((1 to 5), (2 to 6), (3 to 7))
    singleSum(data)
  }
}
























