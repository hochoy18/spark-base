package com.hochoy.spark.utils

import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import org.apache.log4j.Logger
import javax.sql.DataSource

/**
  * @author yangxin_ryan
  * Mysql数据库连接池
  */
object DruidMysqlPoolUtils {
 
  private val LOG = Logger.getLogger(DruidMysqlPoolUtils.getClass.getName)
 
  val dataSource: Option[DataSource] = {
    try {
        val druidProps = new Properties()
        // 获取Druid连接池的配置文件
        val druidConfig = getClass.getClassLoader.getResourceAsStream("druid.properties")
        // 倒入配置文件
        druidProps.load(druidConfig)
        Some(DruidDataSourceFactory.createDataSource(druidProps))
    } catch {
      case error: Exception =>
        LOG.error("Error Create Mysql Connection", error)
        None
    }
  }
 
  // 连接方式
  def getConnection: Option[Connection] = {
    dataSource match {
      case Some(ds) => Some(ds.getConnection())
      case None => None
    }
  }

  def close(conn: Connection): Unit = if (conn !=null) conn.close()

  def main(args: Array[String]): Unit = {
    val conn = DruidMysqlPoolUtils.getConnection.get
    val statement: PreparedStatement = conn.prepareStatement("select * from mysql_offset")
    val rs: ResultSet = statement.executeQuery()
    while (rs.next()){
      val metaData: ResultSetMetaData= rs.getMetaData
      val columnCount:Int = metaData.getColumnCount
      for (i <- 1 to columnCount){
        val value = rs.getString(i)
        val columnName: String = metaData.getColumnName(i)
        printf(" %s  : %s \t ",columnName,value)
      }
      println
    }
    close(conn)
  }
}