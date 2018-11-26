package com.hochoy.spark.sql

import org.apache.spark.SparkConf
import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._

/**
  * spark sql & DataFrames & temp_table
  * http://spark.apache.org/docs/latest/sql-getting-started.html
  *
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月26日 14:18
  * @version :  V1.0
  */
object SQLTest1 {
  val conf = new SparkConf().set("","")
  val spark = createSparkSession("SQL Test 1 ",conf)

  import spark.implicits._

  def main(args: Array[String]) {

    val df = spark.read.json(USER_SPARK_PATH + "sql\\students.sql")
    df.show()
    df.printSchema()
    df.select($"name", $"age"+3).show()
    df.groupBy("age").count().show()


    println("create temp table ...........................")
    df.createOrReplaceTempView("stu")
    val sqlDF = spark.sql("select id , name , age + 1000 as AGE_ from stu order by AGE_");
    sqlDF.show()


  }
}