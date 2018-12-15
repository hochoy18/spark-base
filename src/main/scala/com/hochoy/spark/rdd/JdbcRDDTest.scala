package com.hochoy.spark.rdd

import java.sql.{ResultSet}

import org.apache.spark.rdd.JdbcRDD
import com.hochoy.spark.utils.SparkUtils._
import com.hochoy.spark.utils.JdbcUtil._

object JdbcRDDTest {

  val sc = createSparkContext("jdbc rdd")

  def extractValues(r: ResultSet): (String, String) = {
    (r.getString(1), r.getString(2))
  }

  val data = new JdbcRDD(
    sc,
    getConnection,
    "select user_id,user_name,password from t_user where user_id = 1",
    lowerBound = 1, upperBound = 4, numPartitions = 2, mapRow = extractValues
  )

  def main(args: Array[String]): Unit = {
    println(data.collect().toList)
  }

}
