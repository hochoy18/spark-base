package com.hochoy.spark.sql

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.hochoy.spark.utils.Constants
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import org.apache.spark.sql._

object SparkSqlOnSource {


  def main(args: Array[String]): Unit = {

//    new SparkSqlOnSource().test

    new SparkSqlOnSourceCSV().csvTest

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
    Thread.sleep(1000 * 600)
  }
}


class SparkSqlOnSourceCSV {
  Logger.getLogger("org.apache.spark").setLevel(Level.OFF)

  val user = "hdfs"
  System.setProperty("HADOOP_USER_NAME", user)
  val transactionTable:String = "transaction_Table"
  import com.hochoy.spark.implicits._

  val spark = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .config("spark.debug.maxToStringFields", 100)
    .config("spark.sql.warehouse.dir", buildPath("file:///" + System.getProperty("user.dir"), "target", "spark-warehouse"))
    .master("local")
    .getOrCreate()


  val path: String = buildPath("file:///" + System.getProperty("user.dir"), "src", "main", "scala", "com", "hochoy", "spark", "sql", "data")
  def csvTest: Unit = {
    val df = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")// 自动推断数据类型
      .option("header", "true") // 第一行不作为数据，只作为列名
      .load(buildPath(path, "csv", "trans*.csv"))

    df.createOrReplaceTempView(transactionTable)

    spark.sql(s"desc $transactionTable").show()
    println("======================================================================")

    val df1 = spark.sql(s"select * from $transactionTable")
    df1.printSchema()
    println("======================================================================")
    val df2 = spark.sql(s"select count(1) from $transactionTable")
    df2.show()
    println("======================================================================")
    df1.show()

    TimeUnit.SECONDS.sleep(60)
  }

  def csvSaveTest(): Unit = {
    val df = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true") // 自动推断数据类型
      .option("header", "true") // 第一行不作为数据，只作为列名
      .load(buildPath(path, "csv", "trans*.csv"))

    df.createOrReplaceTempView(transactionTable)


    val df2 = spark.sql(s"select * from $transactionTable")
    df2.printSchema()
    df2.show(100)
    println("--------------------------------------------\n\n")


    val warehouse_dir = spark.conf.get(Constants.SPARK_SQL_WAREHOUSE_DIR)
    //    df2.write.csv(buildPath(warehouse_dir,"csv"))

    var i = 0
    while (i < 3) {
      df2
        .write
        .format("csv")
        .partitionBy("trans_channel","day")
//        .bucketBy(3, "trans_channel")
//        .sortBy("trans_time")
        .option("path", buildPath(warehouse_dir, "csv"))
        .mode(SaveMode.Overwrite)
        .saveAsTable("trans_csv_tab")
      i += 1
      println("-----------------------", i)
    }
    println("--------------------------------------------\n\n")
    TimeUnit.SECONDS.sleep(3)
    spark.sql("select trans_channel ,count(1) from trans_csv_tab group by trans_channel").show(50)
    spark.sql("select * from trans_csv_tab where trans_channel = 'pc'").show(50)
    spark.sql("select * from trans_csv_tab where trans_channel = 'mobile'").show(50)
    TimeUnit.SECONDS.sleep(30)

  }


}
object SparkSqlOnSourceCSV{
  def main(args: Array[String]): Unit = {

    val csv = new SparkSqlOnSourceCSV()
    csv.csvSaveTest()
//    csv.csvTest
  }
}

case class People(name:String,age:Int)

class SparkSqlOnSourceJson{

  val user = "hdfs"
  System.setProperty("HADOOP_USER_NAME", user)
  val transactionTable:String = "transaction_Table"
  import com.hochoy.spark.implicits._


  val spark = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .config("spark.debug.maxToStringFields", 100)
    .master("local[*]")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

  def createDF :Unit = {
    val df = spark.read.json(buildPath(DATA_PATH,"people.json")).cache()
    df.show()
    df.printSchema()
    df.select($"name").show()
    df.select($"name",$"age" + 1).show()
    df.filter($"age" >21).show()
    df.groupBy($"age").count().show



    TimeUnit.SECONDS.sleep(60)

  }


  def inferringSchemaByReflection  :Unit = {
    val frame = spark.sparkContext
      .textFile(buildPath(DATA_PATH, "people.txt"))
      .map(_.split(","))
      .map(att => People(att(0), att(1).toInt))
      .toDF()

    frame.createOrReplaceTempView("people")

    val df1:DataFrame = spark.sql("select * from people where age between 19 and 29")
    df1.show()
    val value: Dataset[String] = df1.map(tee => "Name : " + tee(0) + ",age : " + tee(1))
    value.show()

    df1.map(tee=>"Name: " + tee.getAs[String]("name")).show()
    df1.map(tee=>"age: " + tee.getAs[Int]("age")).show()

    df1
      .map(teenager => teenager.getValuesMap[Any](List("name", "age"))).collect()
      .foreach(println)




  }


  def udaf:Unit = {
    spark.udf.register("myUdaf",MyUDAF)
    val df: DataFrame = spark.read.json(buildPath(DATA_PATH,"employees.json")).repartition(4)
    df.createOrReplaceTempView("employees")
    df.show()
    val result = spark.sql("select myUdaf (salary,age ) as avg  from employees ")
    result.show()
    TimeUnit.SECONDS.sleep(30)
  }

}
object SparkSqlOnSourceJson extends App{
  private val sourceJson = new SparkSqlOnSourceJson()
//  sourceJson.createDF
//  sourceJson.inferringSchemaByReflection
  sourceJson.udaf

}

class SparkSqlOnSourceJdbc {
  val spark = SparkSession
    .builder()
    .appName(this.getClass.getName)
    .config("spark.some.config.option", "some-value")
    .config("spark.debug.maxToStringFields", 100)
    .master("local[*]")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

  def jdbcTest:Unit ={
    val url = "jdbc:mysql://localhost:3306/test"

    val props: Properties = new Properties()
    props.put("user","root")
    props.put("password","123456")


    val jdbcDf = spark.read.jdbc(url,"employee",props)
      jdbcDf.createOrReplaceTempView("tmp_employee")

    jdbcDf.printSchema()

    val df = spark.sql("select name, age  from tmp_employee where age = 18 ")
    df.show()

    df.write.mode(SaveMode.Ignore).jdbc(url,"employee",props)
    val jdbcDf1 = spark.read.jdbc(url,"employee",props)
    jdbcDf1.show()
    TimeUnit.SECONDS.sleep(30)

  }

}

object SparkSqlOnSourceJdbc extends App{
  private val sourceJdbc = new SparkSqlOnSourceJdbc()
  //  sourceJson.createDF
  //  sourceJson.inferringSchemaByReflection
  sourceJdbc.jdbcTest
}


















object MyUDAF extends UserDefinedAggregateFunction{

  override def inputSchema: StructType =
    new StructType()
      .add("c_0",LongType)
      .add("c_2",LongType)
//      new StructType().add("inputColumn",LongType)
//   StructType(StructField("inputColumn",LongType)::Nil)

  override def bufferSchema: StructType = {
//    StructType(StructField("sum", LongType) :: StructField("count", LongType) :: Nil)

    new StructType()
      .add("c0_s",LongType)
      .add("c0_c",LongType)
//      .add("c1_s",LongType)
//      .add("c1_c",LongType)
  }

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0L
//    buffer(2) = 0L
//    buffer(3) = 0L
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (!input.isNullAt(0)) {

      buffer(0) = buffer.getLong(0) + input.getLong(0)
      buffer(1) = buffer.getLong(1) + input.getLong(1)
//      buffer(1) = buffer.getLong(1) + 1
//      buffer(2) = buffer.getLong(2) + input.getLong(1)
//      buffer(3) = buffer.getLong(3) + 1
    }
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
//    buffer1(2) = buffer1.getLong(2) + buffer2.getLong(3)
//    buffer1(3) = buffer1.getLong(2) + buffer2.getLong(3)
  }

  override def evaluate(buffer: Row): Double = {
    buffer.getLong(0) / buffer.getLong(1)
//    (buffer.getLong(0).toDouble / buffer.getLong(1))/(buffer.getLong(2).toDouble / buffer.getLong(3))
  }

}