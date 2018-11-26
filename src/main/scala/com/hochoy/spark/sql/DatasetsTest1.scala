package com.hochoy.spark.sql

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructType, StringType, StructField}

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

//    testDS1
//    println("...............................................................")
//    rdd2DF_Test
    println("==============================================================")
    rdd2DF_Schema

  }

  def testDS1 = {
    val caseDS = Seq(Student(123, "hochoy", 23)).toDS()
    caseDS.show()

    val stus = spark.read.json(USER_SPARK_PATH + "sql\\students.sql").as[Student]
    stus.show()
  }

  def rdd2DF_Test = {
    /**
      * RDD to DF using reflection
      * http://spark.apache.org/docs/latest/sql-getting-started.html#inferring-the-schema-using-reflection
      */
    val stuDF = spark.sparkContext
      .textFile(USER_SPARK_PATH + "sql\\students1.txt")
      .map(_.split(","))
      .map(attributes => Student(attributes(0).trim.toLong, attributes(1), attributes(2).trim.toLong))
      .toDF
    stuDF.createOrReplaceTempView("stu_1")

    val sql = spark.sql("select * from stu_1 where age between 18 and 19 and id >8")
    sql.show()
    sql.map(t => "name  :  " + t(1)).show()
    sql.map(t => s"id as Int   :   ${t.getAs[Int]("id")}").show()
    sql.map(t => s"age   :  ${t(2)}").show

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]
    val v = sql.map(stu => stu.getValuesMap[Any](List("id", "name", "age"))).collect()
    v.map(m => {
      val keys = m.keys
      println(keys)
      keys.foreach(k => {
        println(s"key is  $k    values is   ${m.get(k).toString}")
      })

    })
  }

  def rdd2DF_Schema = {
    /**
      * RDD to DF Programmatically Specifying the Schema
      * http://spark.apache.org/docs/latest/sql-getting-started.html#programmatically-specifying-the-schema
      *
      * a DataFrame can be created programmatically with three steps.
      *  1. Create an RDD of Rows from the original RDD;
      *  2. Create the schema represented by a StructType matching the structure of Rows in the RDD created in Step 1.
      *  3. Apply the schema to the RDD of Rows via `createDataFrame` method provided by SparkSession.
      */
    import spark.implicits._
    val stuRDD = spark.sparkContext.textFile(USER_SPARK_PATH +"sql\\students1.txt")
    val schemaStr = "id,name,age"

    val fields = schemaStr.split(",")
      .map(fn =>{
        println("fn....."+fn)
        StructField(fn,StringType,nullable = true)
      })

    /**
      * 2. Create the schema represented by a StructType matching the structure of Rows in the RDD created in Step 1.
      */
    val schema = StructType(fields)

    /**
      *  1. Create an RDD of Rows from the original RDD;
      */
    val rowRDD = stuRDD.map(_.split(",")).map(x=>Row(x(0),x(1).trim,x(2)))

    /**
      * 3. Apply the schema to the RDD of Rows via `createDataFrame` method provided by SparkSession.
      */
    val stuDF = spark.createDataFrame(rowRDD,schema)
    println( "-------stuDF.show()-------------")
    stuDF.show()

    stuDF.createOrReplaceTempView("stu_1")
    val result = spark.sql("select name from stu_1")
    println("result........................")
    result.map(attr=>s"Name:  ${attr(0)}").show()

  }
}