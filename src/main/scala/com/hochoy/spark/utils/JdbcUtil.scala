package com.hochoy.spark.utils

import java.sql.{Connection, DriverManager}

object JdbcUtil {

  val driverClass = "com.mysql.jdbc.Driver"

  val url = "jdbc:mysql://localhost:3306/authority-control?serverTimezone=UTC"
  val userName = "root"
  val password = "1234"
  def getConnection():Connection={
    Class.forName(driverClass).newInstance()
    DriverManager.getConnection(url,userName,password)
  }
}
