package com.hochoy.spark.sql.datasources

import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.SparkUtils._
import org.apache.spark.sql.SaveMode

/**
  *
  *
  * http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html
  *
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月26日 19:34
  * @version :  V1.0
  */
object SQLDataSourceTest1 {

  val spark = createSparkSession("SQL Data Source ")

  val warehouse_dir = spark.conf.get(SPARK_SQL_WAREHOUSE_DIR)
  def main(args: Array[String]) {

    println(s"warehouse_dir :   $warehouse_dir")
    //  Generic Load/Save Functions
    loadAndSave

    //Run SQL on files directly
    runSQLAndSave

    //Loading Data Programmatically
    runBasicParquetExample

    //Schema Merging
    runParquetSchemaMergingExample
  }

  def loadAndSave() = {
    //  Generic Load/Save Functions
    //  Manually Specifying Options
    val userDF = spark.read.load(s"${USER_SPARK_PATH}${FILE_PATH}users.parquet")
    val seleUserDF = userDF.select("name", "favorite_color")

    val v = spark.read.parquet(s"${USER_SPARK_PATH}${FILE_PATH}users.parquet")
    v.printSchema()
    val vs = v.select("name", "favorite_numbers", "favorite_color")
    vs.show()

    seleUserDF.write.parquet(s"${warehouse_dir}\\nac.parquet")

    val pDF = spark.read.format("json").load(s"${USER_SPARK_PATH}${FILE_PATH}sql_datasource.json")
    val seleDF = pDF.select("name", "age")
    seleDF.write.format("parquet").save(s"${warehouse_dir}\\r.parquet")

    println("===================================load data from manually-save file and vie regex-path =====================================")
    val getDF = spark.read.format("parquet").parquet(s"${warehouse_dir}\\r.parquet\\part-*.parquet")
    getDF.printSchema()
    getDF.select("name", "age").show()


    println("===================================Manually Specifying Options   -- csv =====================================")
    //  Manually Specifying Options
    //http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html#manually-specifying-options
    val peopleDF = spark.read.format(FORMAT_CSV)
      .option("sep", ";")
      .option("inferSchema", "true")
      .option("header", "true").load(s"${USER_SPARK_PATH}${FILE_PATH}people.csv")

    peopleDF.printSchema()
    val p = peopleDF.select("name", "age", "job")
    p.show()

    //    println("===================================Manually Specifying Options   -- orc  =====================================")
    //
    //    vs.show()
    //    vs.write.format(FORMAT_ORC)
    //      .option("orc.bloom.filter.columns", "favorite_color")
    //      .option("orc.dictionary.key.threshold", "1.0")
    //      .save(s"${USER_DIR}\\target\\vs.orc")

  }

  def runSQLAndSave() = {
    //Run SQL on files directly
    val sqlDF = spark.sql(s"select * from parquet.`${USER_SPARK_PATH}${FILE_PATH}users.parquet`")
    sqlDF.printSchema()
    sqlDF.show()

    //Save Modes
    //http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html#save-modes
    /**
      * Optional save mode
      *
      * SaveMode.ErrorIfExists
      * SaveMode.Append
      * SaveMode.Overwrite
      * SaveMode.Ignore
      */
    sqlDF.write.mode(SaveMode.Append).format(FORMAT_JSON).save(s"${warehouse_dir}\\saveMode.json")

    val resDF = spark.read.json(s"${warehouse_dir}\\saveMode.json\\part-*.json")
    resDF.printSchema()
    resDF.select("favorite_color", "favorite_numbers", "name").show()

  }

  def runBasicParquetExample = {

    //Loading Data Programmatically
    /**
      * http://spark.apache.org/docs/latest/sql-data-sources-parquet.html#loading-data-programmatically
      *
      */
    val peopleDF = spark.read.json(s"${USER_SPARK_PATH}${FILE_PATH}people.json")
    peopleDF.write.mode(SaveMode.Append).parquet(s"${warehouse_dir}\\peo.parquet")
    val resDF = spark.read.parquet(s"${warehouse_dir}\\peo.parquet")
    resDF.createOrReplaceTempView("peo_p")
    val nameDF = spark.sql("select name from peo_p ")
    nameDF.printSchema()
    nameDF.show()
  }

  def runParquetSchemaMergingExample = {

    //Schema Merging
    /**
      * http://spark.apache.org/docs/latest/sql-data-sources-parquet.html#schema-merging
      */
    import spark.implicits._
    val path = {
      s"${warehouse_dir}\\schema_merging.parquet"
    }
    val squaresDF = spark.sparkContext.makeRDD(1 to 10).map(i => (i, Math.pow(i, 2))).toDF("value", "square")
    squaresDF.write.mode(SaveMode.Overwrite) parquet (s"${path}\\key=1")

    val cobesDF = spark.sparkContext.makeRDD(1 to 10).map(i => (i, Math.pow(i, 3))).toDF("value", "cube")
    cobesDF.write.mode(SaveMode.Overwrite).parquet(s"${path}\\key=2")

    //before merge
    println("===================================before merge =====================================")
    val v = spark.read.parquet(path)
    v.printSchema()
    v.select("value", "key", "square").show()

    println("===================================after merge =====================================")
    val mergedDF = spark.read.option("mergeSchema", "true").parquet(path)
    mergedDF.select("value", "key", "square", "cube").show()
    mergedDF.printSchema()


  }
}