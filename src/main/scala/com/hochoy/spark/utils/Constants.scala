package com.hochoy.spark.utils
import com.hochoy.spark.utils.SparkUtils._
/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月15日 9:21
  * @version :  V1.0
  */
object Constants {

  val USER_SPARK_PATH ={
    hadoopHomeSet
    System.getProperty("user.dir")+"\\src\\main\\scala\\com\\hochoy\\spark\\"
  }
  val FILE_PATH={"sql\\data\\"}
}