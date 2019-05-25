package com.hochoy.spark.sql

import com.hochoy.spark.utils.SparkUtils._
import org.apache.avro.generic.GenericData.StringType
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, StructField, StructType}


/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 9:40
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object SparkSQLUdfAndFunction {

  val spark = createSparkSession("SparkSQLUdfAndFunction")

  def main(args: Array[String]): Unit = {
    udf_test1

    spark.stop()
  }

  def udf_test1:Unit = {

    val sales = Array("2018-01-01,50,Teacher",
      "2018-01-01,60,Hello",
      "2018-01-01,70,world",
      "2018-01-01",
      "2018-01-02,150,good",
      "2018-01-02,250,Morning")

    import spark.implicits._

    val saleDf = spark.sparkContext.parallelize(sales)
      .filter(x ⇒x.split(",").length == 3)
      .map(x⇒ x.split(","))
      .map(x⇒Sale(x(0),x(1).toInt,x(2)))
      .toDF

    import org.apache.spark.sql.functions._
    saleDf.groupBy("date")
      .agg(sum("price").as("money")).show

    spark.udf.register("strLen",(s:String)⇒ s.length+100)

    saleDf.createOrReplaceTempView("hochoy_sales")

    val frame = spark.sql(
      "select date,strLen(date) as date_len,price,strLen(price) as price_len ,name from hochoy_sales")
    frame.show()

    val strUpper = udf( (str:String  ) ⇒  { str.toUpperCase() } )

    frame.withColumn("name_upper",strUpper($"name")).show()

  }
  case class Sale(date:String,price:Int,name:String)


//  def udfa_test1:Unit = {
//    object CustomerCount extends UserDefinedAggregateFunction{
//
//      override def inputSchema: StructType = {
//        StructType(StructField("inputColumn",StringType)::Nil)
//      }
//
//      override def bufferSchema: StructType = ???
//
//      override def dataType: DataType = ???
//
//      override def deterministic: Boolean = ???
//
//      override def initialize(buffer: MutableAggregationBuffer): Unit = ???
//
//      override def update(buffer: MutableAggregationBuffer, input: Row): Unit = ???
//
//      override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = ???
//
//      override def evaluate(buffer: Row): Any = ???
//    }
//
//  }



}
