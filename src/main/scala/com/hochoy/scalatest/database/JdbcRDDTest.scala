package com.hochoy.scalatest.database

import java.sql.{Connection, DriverManager}

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
  * Created by hochoy on 2018/8/22.
  */
object JdbcRDDTest {

  case class DBConf(driver: String, url: String, user: String,
                    passwd: String, dbPrefix: String = "razor_")

  def getDBConf(sparkConf: SparkConf) = DBConf(
    sparkConf.get("spark.counting.sql.jdbc.driver", "oracle.jdbc.OracleDriver"),
    sparkConf.get("spark.counting.sql.jdbc.url", "jdbc:oracle:thin:@192.168.1.207:1521:razor"),
    sparkConf.get("spark.counting.sql.jdbc.user", "yrazor"),
    sparkConf.get("spark.counting.sql.jdbc.passwd", "yrazor"),
    sparkConf.get("spark.counting.sql.jdbc.dbPrefix", "razor_")
  )


  def getConn(conf: DBConf): Connection = {
    Class.forName(conf.driver).newInstance()
    val conn = DriverManager.getConnection(conf.url, conf.user, conf.passwd)
    conn
  }

  case class User(id: String, name: String, sex: String)

  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setAppName("hochoy")
      .setMaster("local[2]"))
    val conn = getConn(getDBConf(sc.getConf))
    val sql = s"select * from ${getDBConf(sc.getConf).dbPrefix}_user where id = ?"
    val ul = getUserList(conn ,sql )

  }
  def getUserList(conn:Connection,sql:String ): ListBuffer[User] ={
    val stat = conn.prepareStatement(sql)
    stat.setString(1,"1001")
    val rs = stat.executeQuery()
    val l = ListBuffer.empty[User]
    while (rs.next()) l += User(
      id = rs.getString("id"),
      name = rs.getString("name"),
      sex = rs.getString("sex")
    )
    rs.close()
    stat.close()
    l
  }
}
