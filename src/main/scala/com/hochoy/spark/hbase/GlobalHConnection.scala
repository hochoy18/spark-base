package com.hochoy.spark.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * A global HBase connection shared by multiple threads.
 * Time:   11/10/15 10:28 AM
 *
 * @author jianghe.cao
 */
object GlobalHConnection extends Serializable {
  sys.addShutdownHook(close())
  val logger = LoggerFactory.getLogger(this.getClass)

  /**
   * The HBase connection
   */
  private lazy val hConn = ConnectionFactory.createConnection(conf)

  private val conf = HBaseConfiguration.create

  def setConf(conf: Configuration): Unit = {
    this.conf.clear()
    this.conf.addResource(conf)
  }

  def setConf(hbaseZKConfig: Map[String, String]): Unit = {
    if (!hbaseZKConfig.isEmpty){
      this.conf.clear()
      hbaseZKConfig.toStream.foreach(x⇒{ this.conf.set(x._1,x._2)})
    }
  }

  def getConf(): Configuration = {
    this.conf
  }

  def getConn(): Connection = {
    this.hConn
  }


  def apply(): Connection = hConn

  /**
   * Close HBase connection
   */
  def close(): Unit = if (!hConn.isClosed) {
    logger.info("Closing the global HBase connection")
    hConn.close()
    logger.info("The global HBase connection closed")
  }

}
