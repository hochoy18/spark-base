package com.hochoy.spark.rdd

import com.hochoy.spark.streaming.meta.DataTables
import com.hochoy.spark.utils.SparkUtils.createSparkSession
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object HBaseReadOrWrite {


  val spark: SparkSession = createSparkSession("SQL Data Source ")
  val AL_SPLIT = "\001"

  def main(args: Array[String]): Unit = {
    val sc: SparkContext = spark.sparkContext

    val rdd: RDD[(ImmutableBytesWritable, Result)] = readHBase("action","15",sc)
    println(rdd.count())

  }


  def readHBase(tableType: String, reportId: String, sc: SparkContext): RDD[(ImmutableBytesWritable, Result)] = {
    val ns: String = "NS_SNBUBAS"
    val hbaseConf = HBaseConfiguration.create
    val zkConfig: Map[String, String] = hbaseZKConfig
    val tableName = if (tableType == "action") new DataTables(ns).CobubActionReport.name else new DataTables(ns).CobubRetentionReport.name
    hbaseConf.clear()
    zkConfig.foreach(kv => hbaseConf.set(kv._1, kv._2))
    hbaseConf.set(TableInputFormat.INPUT_TABLE, tableName)
    hbaseConf.set(TableInputFormat.SCAN_ROW_START, s"${reportId}${AL_SPLIT}")
    hbaseConf.set(TableInputFormat.SCAN_ROW_STOP, s"s${reportId}${AL_SPLIT}a")
    val rdd: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(hbaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    rdd
  }

  def hbaseZKConfig: Map[String, String] = {
    val map = collection.mutable.Map[String, String]()
    map += (
      "hbase.zookeeper.quorum" -> "192.168.1.11,192.168.1.12,192.168.1.15",
      "hbase.zookeeper.property.clientPort" -> "2181"
    )
    map.toMap
  }

}
