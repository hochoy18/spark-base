package com.hochoy.spark.sql

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._

/**
  * datasets
  * http://spark.apache.org/docs/latest/sql-getting-started.html#creating-datasets
  *
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月26日 15:15
  * @version :  V1.0
  */
object DatasetsTest1 {

  case class Student(id: Long, name: String, age: Long)

  val spark = createSparkSession("Datasets 1")

  import spark.implicits._

  def main(args: Array[String]) {

    rdd2DF_Test

  }

  def testDS1 = {
    val caseDS = Seq(Student(123, "hochoy", 23)).toDS()
    caseDS.show()

    val stus = spark.read.json(USER_SPARK_PATH + "sql\\students.sql").as[Student]
    stus.show()
  }

  def rdd2DF_Test = {
    /**
      * RDD to DF
      */
    val stuDF = spark.sparkContext
      .textFile(USER_SPARK_PATH + "sql\\students1.txt")
      .map(_.split(","))
      .map(attributes => Student(attributes(0).trim.toLong, attributes(1), attributes(2).trim.toLong))
      .toDF
    stuDF.createOrReplaceTempView("stu_1")

    val sql = spark.sql("select * from stu_1 where age between 18 and 19 and id >8")
    sql.show()
    sql.map(t =>"name  :  "+ t(1)).show()
    sql.map(t=>s"id as Int   :   ${t.getAs[Int]("id")}").show()
    sql.map(t=>s"age   :  ${t(2)}").show

  }
}