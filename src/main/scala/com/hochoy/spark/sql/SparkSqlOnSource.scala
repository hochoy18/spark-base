package com.hochoy.spark.sql

import java.io.File

import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkSqlOnSource {


  def main(args: Array[String]): Unit = {

    new SparkSqlOnSource().test

  }


  val actionSchema = new StructType()
    .add("action", DataTypes.StringType, false)
    .add("appkey", DataTypes.StringType, false)
    .add("productid", DataTypes.StringType, false)
    .add("deviceid", DataTypes.StringType, false)
    .add("userid", DataTypes.StringType, true)
    .add("sessionid", DataTypes.StringType, false)
    .add("latitude", DataTypes.FloatType, true)
    .add("longitude", DataTypes.FloatType, true)
    .add("clienttime", DataTypes.TimestampType, false)
    .add("servertime", DataTypes.TimestampType, true)
    .add("uuid", DataTypes.StringType, true)
    .add("ip", DataTypes.StringType, true)
    .add("country", DataTypes.StringType, true)
    .add("region", DataTypes.StringType, true)
    .add("city", DataTypes.StringType, true)
    .add("useragent", DataTypes.StringType, true)
    .add("request", DataTypes.StringType, true)
    .add("exceptiontype", DataTypes.StringType, true)
    .add("action_content", DataTypes.StringType, true)
    .add("lib_version", DataTypes.StringType, true)
    .add("global_user_id", DataTypes.StringType, true) //根据user_id映射的整数
    .add("is_new_user", DataTypes.BooleanType, true) //根据user_id判断是否是新的user_id,sparksql有个bug，针对booleanType，设置schema含有该is_user_id,如果某个parquet文件实际上没有这个字段，查的时候会报错，如果booleanType字段改成StringType就没有问题
    .add("is_new_device", DataTypes.BooleanType, true)
    //properties
    .add("channelid", DataTypes.StringType, true)
    .add("duration", DataTypes.LongType, true)
    .add("platform", DataTypes.StringType, true)
    .add("version", DataTypes.StringType, true)
    .add("osversion", DataTypes.StringType, true)
    .add("network", DataTypes.StringType, true)
    .add("page", DataTypes.StringType, true)
    .add("pagetitle", DataTypes.StringType, true)
    .add("element_id", DataTypes.StringType, true)
    .add("refer", DataTypes.StringType, true)
    .add("language", DataTypes.StringType, true)
    .add("manufacturer", DataTypes.StringType, true)
    .add("model", DataTypes.StringType, true)
    .add("screen_width", DataTypes.ShortType, true)
    .add("screen_height", DataTypes.ShortType, true)
    .add("mccmnc", DataTypes.StringType, true)
    .add("is_update", DataTypes.BooleanType, true)
    .add("utm_source", DataTypes.StringType, true)
    .add("utm_medium", DataTypes.StringType, true)
    .add("utm_campaign", DataTypes.StringType, true)
    .add("utm_content", DataTypes.StringType, true)
    .add("day", DataTypes.StringType, false)
    .add("hour", DataTypes.StringType, false)
    .add("category", DataTypes.StringType, false)
    .add("actionattach", DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType, true), true)
    .add("by_day", DataTypes.IntegerType, true)


}

class SparkSqlOnSource {
  val user = "hdfs"
  System.setProperty("HADOOP_USER_NAME", user)
  val spark = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .config("spark.debug.maxToStringFields", 100)
    .config("spark.sql.warehouse.dir", "target/spark-warehouse")
    .master("local")
    .getOrCreate()


  //  import com.hochoy.spark.sql.sql._
  //  val path = dir2Path( File.separator ,"")
  import com.hochoy.spark.implicits._

  val path: String = buildPath("file:///D:", "user",  "cobub3", "parquet")

  val tableName = "parquetTmpTable"

  spark
    .read
    .schema(SparkSqlOnSource.actionSchema)
    .format("parquet")
    .load(path)
    .createOrReplaceTempView(tableName)

  def test {
    println("x=========================================")

    val df1: DataFrame = spark.sql(s"select userid,sessionid,action,day, count(1) from $tableName where day = '20190419' group by userid,sessionid,action,day order by action limit 10 ")
    val schema = df1.schema
    println("printTreeString=========================================")
    schema.printTreeString()
    println("isEmpty=========================================")

    print(schema.isEmpty)
    println("explain=========================================")
    df1.explain(true)
    df1.show()
    Thread.sleep(1000 * 60)
  }
}
