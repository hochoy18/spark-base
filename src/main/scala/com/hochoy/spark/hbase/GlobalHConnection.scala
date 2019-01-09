package com.hochoy.spark.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}

object GlobalHConnection extends Serializable {
  sys.addShutdownHook(clone())

  private lazy val hConn = ConnectionFactory.createConnection(conf)

  private val conf = HBaseConfiguration.create

  def setConf(conf: Configuration): Unit = {
    this.conf.clear
    this.conf.addResource(conf)
  }

  def getConf: Configuration = this.conf

  def apply: Connection = hConn

  def close(): Unit = if (!hConn.isClosed) {
    hConn.close()
  }

}
