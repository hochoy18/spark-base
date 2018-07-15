package com.hochoy.sparktest.spark.job.hbase

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.{HConstants, HBaseConfiguration}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.hadoop.hbase.util.Bytes

/**
  * Created by Cobub on 2018/7/15.
  */
object rddHbase {

  val sparkConf = new SparkConf().setAppName("Spark-Hbase").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val hConf = HBaseConfiguration.create()
  val zk = "192.168.1.211:2181"

  hConf.set(HConstants.ZOOKEEPER_QUORUM, zk)
  hConf.set(TableInputFormat.INPUT_TABLE, "gd:clientdata")
  hConf.set(TableInputFormat.SCAN_ROW_START, "")
  hConf.set(TableInputFormat.SCAN_ROW_STOP, "")
  hConf.set(TableInputFormat.SCAN_COLUMNS, "f:localtime")

  //hRDD
  //用sc.newAPIHadoopRDD根据conf中配置好的scan来从Hbase的数据列族中读取包含(ImmutableBytesWritable, Result)的RDD,
  val hRDD:RDD[scala.Tuple2[ImmutableBytesWritable, Result]] = sc.newAPIHadoopRDD(hConf, classOf[TableInputFormat]
    , classOf[ImmutableBytesWritable], classOf[Result])

  hRDD.count()

  val rowkeyRdd = hRDD.map(tuple => tuple._1).map(
    item => Bytes.toString(item.get())
  )
  val resultRdd = hRDD.map(tuple =>tuple._2)

  val keyValueRdd = resultRdd.map(result=>Bytes.toString(result.getRow).split(" ")(0))
  keyValueRdd.take(3).foreach(kv=>println(kv))

//  val keyStatsRdd = keyValueRdd.groupB

  def main(args: Array[String]) {

  }
}
