package com.hochoy.spark.sql
import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月26日 19:34
  * @version :  V1.0
  */
object SQLDataSourceTest1 {

  val spark = createSparkSession("SQL Data Source ")
  import spark.implicits._
  def main(args: Array[String]) {
    val userDF = spark.read.load(s"${USER_SPARK_PATH}${FILE_PATH}users.parquet")
    userDF.select("name","favorite_color").show()

    val pDF = spark.read.format("json").load(s"${USER_SPARK_PATH}${FILE_PATH}sql_datasource.json")
    pDF.select("name","age").show()
    pDF.select("name","age").write.format("parquet").save(s"r.parquet")

    val rDF = spark.read.load("r.parquet")
    rDF.select().show()
  }
}