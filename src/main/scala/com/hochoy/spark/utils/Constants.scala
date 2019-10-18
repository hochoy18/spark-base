package com.hochoy.spark.utils
import com.hochoy.spark.utils.SparkUtils._
/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 9:21
  * @version :  V1.0
  */
object Constants {

  val Unknown :String = "UNKNOWN"
  val FORMAT_CSV: String = "csv"
  val FORMAT_PARQUET: String = "parquet"
  val FORMAT_JSON: String = "json"
  val FORMAT_JDBC = "jdbc"
  val FORMAT_ORC: String = "orc"
  val FORMAT_TEXT: String = "text"
  val FORMAT_LIBSVM: String = "libsvm"
  val SPARK_SQL_WAREHOUSE_DIR: String = "spark.sql.warehouse.dir"


  val USER_DIR = {
    hadoopHomeSet
    "file:///"+System.getProperty("user.dir")
  }

  val TARGET_DIR = USER_DIR + "\\target"

  val USER_SPARK_PATH = USER_DIR + "\\src\\main\\scala\\com\\hochoy\\spark\\"

  val FILE_PATH = "sql\\data\\"
  val RETENTION_SEPARATOR  : String = "@@@@@@@"
}