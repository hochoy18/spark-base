package com.hochoy.spark.hbase

import java.util.concurrent.TimeUnit

import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.SparkSession

object MemberGenerator {
  val spark = SparkSession.builder()
    .appName("heihei")
    .master("local[*]")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    phoenix
  }

  def phoenix(): Unit = {
    phoenix_1()
  }

  def phoenix_1(): Unit = {
    // 第一种读取方法
    val df2 = spark
      .read
      .format("phoenix")
      .option("table", "NS_SNBUBAS:cobub_users")
      .option("zkUrl", "192.168.1.11:2181,192.168.1.12:2181,192.168.1.15:2181")
      .load()
    df2.printSchema()
    println("===============================================")
    df2.show()

    TimeUnit.MINUTES.sleep(2)
    //    val df3 =    df2.filter("name not like 'hig%'")
    //      .filter("password like '%0%'")
    //    df3.show()

  }

  def phoenix_2(): Unit = {
    val configuration = new Configuration()
    configuration.set("hbase.zookeeper.quorum", "192.168.56.11:2181")
    // 第二种读取方法
    import org.apache.phoenix.spark._
    val dataFrame =
      spark.sqlContext.phoenixTableAsDataFrame("test1", Array("ID", "INFO.NAME", "INFO.PASSWORD"), conf = configuration)
    dataFrame.show()
  }

}

