package com.hochoy.spark.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.slf4j.{LoggerFactory, Logger}


object GlobalHConnection {

  val logger:Logger=LoggerFactory.getLogger(this.getClass)

  private val conf = {
    val conf = HBaseConfiguration.create
    conf.addResource(ClassLoader.getSystemResourceAsStream("razor-kerberos.xml"))
    conf
  }

//  private  lazy val hConn = HB
}