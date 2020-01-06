package com.hochoy.spark.utils

import java.sql.{Connection, DriverManager, ResultSet, ResultSetMetaData}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object JdbcUtil {

  val driverClass = "com.mysql.jdbc.Driver"

  val url = "jdbc:mysql://192.168.1.207:3306/snbubas"
  val userName = "snbubas"
  val password = "snbubas"

  def main(args: Array[String]): Unit = {
    val conn = getConnection
    val actionSQL = "select REPORT_ID,TASK_RESULT_TABLE  from COBUB_ACTION_REPORT where TASK_ENABLE = 1 and REPORT_ID in (?)"
    val actionList: List[Map[String, String]] = getResultBySQL(conn, actionSQL,args.mkString(",") )
    actionList.foreach(v =>{println("action ::::::--------------------------------------"); v.foreach(x=>println(s"${x._1}===>>>>>>${x._2}"))})

  }

  def getConnection():Connection={
    Class.forName(driverClass).newInstance()
    DriverManager.getConnection(url,userName,password)
  }



  @throws(classOf[Exception])
  def getResultBySQL(conn: Connection, preSQL : String ,args :String* ):List[Map[String,String]] = {

    val resList : ListBuffer[Map[String,String]] = ListBuffer[Map[String,String]]()

    val pstmt = conn.prepareStatement(preSQL)

    for (idx <- (1 to args.length)) {
      pstmt.setString(idx,args(idx-1))
    }

    val rs: ResultSet = pstmt.executeQuery()

    while (rs.next()){
      val map: mutable.HashMap[String, String] = mutable.HashMap[String,String]()
      val rsmd: ResultSetMetaData = rs.getMetaData


      for (i <- (1 to rsmd.getColumnCount)){
        val column: String = rsmd.getColumnName(i).toLowerCase
        val value :String = rs.getString(i)
        map += (column -> value)
      }
      resList += map.toMap

    }
    resList.toList
  }
}
