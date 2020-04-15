package com.hochoy.spark.rdd

import com.hochoy.spark.utils.SparkUtils
import org.apache.spark.storage.StorageLevel

object CacheAndCheckpoint {
  System.setProperty("HADOOP_USER_NAME", "hdfs")
  val sc = SparkUtils.createSparkContext(this.getClass.getName)


  sc.setLogLevel("WARN")
  def main(args: Array[String]): Unit = {
    testCheckpoint
  }
  def testCheckpoint:Unit ={

    //  sc.getConf.set("spark.io.compression.codec", "snappy")
//    sc.setCheckpointDir("hdfs://nameservice1:8020/tmp/hc")
    sc.setCheckpointDir("hdfs://tdhtest01:8020/tmp/hc")

    val score = List(
      ("math", 100), ("Chinese", 100),
      ("math", 90), ("Chinese", 98),
      ("hbase", 100), ("hadoop", 56),
      ("math", 100), ("hadoop", 34),
      ("hbase", 200))
    val rdd1 = sc.makeRDD(score,2).groupByKey()
    rdd1.checkpoint()
    rdd1.foreach(println)
    println("------------------------")

    rdd1.persist(StorageLevel.MEMORY_ONLY_2)
    val avg = rdd1.map(e=>(e._1,e._2.sum / e._2.size))
    val total = rdd1.map(e=>(e._1,e._2.sum))

    avg.foreach(println)
    total.foreach(println)

    println("------------------------")

    val file: Option[String] = rdd1.getCheckpointFile
    println(s"file:${file}")

    val cpData = sc.textFile(file.get + "/part-*")// TODO
    cpData.foreach(println)

    Thread.sleep(1000 * 50)

  }
}
