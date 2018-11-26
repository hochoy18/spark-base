package com.hochoy.spark.sql

import com.hochoy.spark.utils.SparkUtils._
import com.hochoy.spark.utils.Constants._
import org.apache.spark.sql.types.{StringType, IntegerType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession, SQLContext}

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月21日 15:13
  * @version :  V1.0
  */
object DataFrameTest {

  val sc = createSparkContext("Data frame ")

  //val hiveCon = new

  def main(args: Array[String]) {
    val sqlContext = new SQLContext(sc)
    val fileRDD = sc.textFile(USER_SPARK_PATH + "sql\\students.sql")
    fileRDD.saveAsTextFile(USER_SPARK_PATH + "sql\\result.txt")
    val lineRDD = fileRDD.map(line => line.split(","))
    val studentsRDD = lineRDD.map(x => Student(x(0).toLong, x(1), x(2).toLong))
    import sqlContext.implicits._
    val studentDf = studentsRDD.toDF();
    studentDf.registerTempTable("t_students")
    val df = sqlContext.sql("select * from t_students")
    df.rdd.foreach(row => println(s"${row(0)} ,  ${row(1)}  ,  ${row(2)}"))
    df.rdd.saveAsTextFile(USER_SPARK_PATH + "sql\\result")


  }
  case class Student(int: Long, name: String, age: Long)
}

object StructTypeTest{
  def main(args: Array[String]) {
    val sc = createSparkContext("Struct TypeTest frame ")
    val sqlContext = new SQLContext(sc)
    val fileRDD = sc.textFile(USER_SPARK_PATH + "sql\\students.sql").map(_.split(","))

    val spark = SparkSession.builder().appName("spark session").config("","")

    val row =fileRDD.map(x=> Row(x(0),x(1),x(2)))
    val schema = StructType(
      List(
        StructField("id",StringType,true),
        StructField("name",StringType,true),
        StructField("age",StringType,true)
      )
    )
    val df = sqlContext.createDataFrame(row,schema)
    df.registerTempTable("t_student")
    val r = sqlContext.sql("select * from t_student order by age")
    r.rdd.collect().foreach(x=>println(s"${x}"))
  }
}

object testDF{
  def main(args: Array[String]) {
    val sc = createSparkContext("Struct TypeTest frame ")
    val sql = new SQLContext(sc)
    val df = sql.read.json(USER_SPARK_PATH + "sql\\students.sql")
    df.show()
    val descDF = df.describe()
    descDF.show()
    df.foreach(r=>println(r.mkString(",")))
  }
}