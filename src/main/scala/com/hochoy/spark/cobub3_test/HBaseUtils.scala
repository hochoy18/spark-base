package com.hochoy.spark.cobub3_test

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util

import com.hochoy.cobub3_test.{Constants, Formatter}
import com.hochoy.spark.hbase.GlobalHConnection
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.spark.sql.Row
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer


/**
  * Created by IntelliJ IDEA.
  * Time:   6/10/15 6:41 AM
  *
  * @author jianghe.cao
  */
object HBaseUtils {
  val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Judge specific key or column exists.
    */
  def exists(table: Table, row: String): Boolean = {
    table.exists(new Get(row.getBytes()))
  }

  def exists(table: Table, row: String, family: String, qualifier: String): Boolean = {
    table.exists(new Get(row.getBytes()).addColumn(family.getBytes(), qualifier.getBytes()))
  }

  /**
    * Get result from HBase table by specific Info.
    */
  def getResult(table: Table, row: String, family: String, qualifier: String): Result = {
    table.get(new Get(Bytes.toBytes(s"$row")).addColumn(family.getBytes, qualifier.getBytes))
  }

  /**
    * Get result from HBase table by specific Info.
    */
  def getResults(table: Table, ks: List[(String, String, String)]): Array[Result] = {
    val gets = new util.LinkedList[Get]()
    ks.foreach(x => {
      gets.add(new Get(Bytes.toBytes(x._1)).addColumn(x._2.getBytes, x._3.getBytes))
    })
    table.get(gets)
  }

  /**
    * Put specific key and String value into HBase table.
    */
  def putKV(table: Table, row: String, family: String, qualifier: String, value: String): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  /**
    * Put specific key and Long value into HBase table.
    */
  def putKV(table: Table, row: String, family: String, qualifier: String, value: Long): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  /**
    * Put specific key and Float value into HBase table.
    */
  def putKV(table: Table, row: String, family: String, qualifier: String, value: Float): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  /**
    * Put specific key and Boolean value into HBase table.
    */
  def putKV(table: Table, row: String, family: String, qualifier: String, value: Boolean): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  /**
    * Put specific key and Boolean value into HBase table.
    */
  def putKVs(table: Table, kvs: List[(String, String, String, _)]): Unit = {
    val putList = new java.util.LinkedList[Put]()
    kvs.foreach(x=>{
      val put = new Put(Bytes.toBytes(x._1))
      put.addColumn(Bytes.toBytes(x._2),Bytes.toBytes(x._3),toBytes(x._4))
      putList.add(put)
    })
    table.put(putList)
  }

  def toBytes(value: Any): Array[Byte] ={
    value match {
      case a: String => Bytes.toBytes(a.asInstanceOf[String])
      case a: Long => Bytes.toBytes(a.asInstanceOf[Long])
      case a: Float => Bytes.toBytes(a.asInstanceOf[Float])
      case a: Boolean => Bytes.toBytes(a.asInstanceOf[Boolean])
      case a: Double => Bytes.toBytes(a.asInstanceOf[Double])
      case a: Int => Bytes.toBytes(a.asInstanceOf[Int])
      case a: Short => Bytes.toBytes(a.asInstanceOf[Short])
      case a: ByteBuffer => Bytes.toBytes(a.asInstanceOf[ByteBuffer])
      case a: java.math.BigDecimal => Bytes.toBytes(a.asInstanceOf[java.math.BigDecimal])
      case _ => Array[Byte](0)
    }
  }



  def putToHTable(rows: Array[Row], day: String = "20181012", tableName: String, hbaseZKConfig: Map[String, String]): Unit = {
    // Connect to HTable
    GlobalHConnection.setConf(hbaseZKConfig)
    val resultTable = GlobalHConnection.getConn().getTable(TableName.valueOf(tableName))
    val rowkeyPrefix = genRowkeyPrefix(day)

    if (!rows.isEmpty) {
      logger.info("Deleting old list report rows ...")
      delOldListRpt(tableName, rowkeyPrefix, hbaseZKConfig)
      val Nums100 = rows.take(1000)
      val fieldLength = Nums100(0).length
      val resultList = new util.ArrayList[Put]


      for (i <- 0 until Nums100.length) {
        val rowkey = rowkeyPrefix + "-" + i

        val put = new Put(Bytes.toBytes(rowkey))

        for (j <- 1 to fieldLength) {
          put.addColumn(Bytes.toBytes("f"), Bytes.toBytes((j - 1).toString), Bytes.toBytes(Nums100(i).get(j - 1).toString))
        }
        resultList.add(put)

        if (i % 100 == 0) {
          resultTable.put(resultList)
          resultList.clear()
        }
      }
      if (!resultList.isEmpty) {
        resultTable.put(resultList)
      }

      resultTable.close()
    }

  }




  /**
    * clear a table ,through delete it first and then re-create it
    */
  def clearHTable(hTable: String, hbaseZKConfig: Map[String, String]): Unit = {

    GlobalHConnection.setConf(hbaseZKConfig)
    val admin = new HBaseAdmin(GlobalHConnection.getConf())
    if (admin.tableExists(TableName.valueOf(hTable))) {
      if (admin.isTableEnabled(TableName.valueOf(hTable))) admin.disableTable(TableName.valueOf(hTable))
      admin.deleteTable(TableName.valueOf(hTable))
    }

    val htd = new HTableDescriptor(TableName.valueOf(hTable))
    val hcd = new HColumnDescriptor("f")
    //add  a column family to table
    htd.addFamily(hcd)
    admin.createTable(htd)

  }

  //扫描记录
  def scanRecord(tablename: String, family: String, column: String, productId: String, hbaseZKConfig: Map[String, String]): String = {
    var scanner: ResultScanner = null
    val userTable = TableName.valueOf(tablename)
    GlobalHConnection.setConf(hbaseZKConfig)
    val table = GlobalHConnection.getConn().getTable(userTable)
    try {

      val s = new Scan()
      s.addColumn(family.getBytes(), column.getBytes())
      scanner = table.getScanner(s)
      println("scan...for...")
      var result: Result = scanner.next()
      var totalLast: String = null;
      while (result != null) {
        if (Bytes.toString(result.getRow()).startsWith(productId)) {
          totalLast = Bytes.toString(result.getValue(family.getBytes(), column.getBytes()))
          println("Found value: " + totalLast)
        }
        result = scanner.next()
      }
      totalLast
    } finally {
      if (table != null)
        table.close()
      scanner.close()
    }
  }




  def isExist(tableName: String, hbaseZKConfig: Map[String, String]): Boolean = {
    GlobalHConnection.setConf(hbaseZKConfig)
    val hAdmin = GlobalHConnection.getConn().getAdmin
    val bool = hAdmin.tableExists(TableName.valueOf(tableName))

    hAdmin.close()
    bool
  }

  /**
    * Generate HBase row key according to taskType & calender.
    *
    * @param cal Calender to be used to generate row key.
    * @return The generated HBase row key.
    */
  private def genRowkeyPrefix(day: String) = {
    var rowkey = "" + day


    rowkey
  }


  /**
    * Delete HBase rows with the specific row key prefix.
    *
    * @param hTable       The HTable to be deleted rows.
    * @param rowKeyPrefix Rows those have this param as prefix will be deleted.
    *
    */
  private def delOldListRpt(tableName: String, rowKeyPrefix: String, hbaseZKConfig: Map[String, String]): Unit = {
    val start = Bytes.toBytes(rowKeyPrefix + "-0")
    val stop = Bytes.toBytes(rowKeyPrefix + "-:")
    val scan = new Scan(start, stop)
    var table: Table = null
    var scanner: ResultScanner = null
    try {
      GlobalHConnection.setConf(hbaseZKConfig)
      table = GlobalHConnection.getConn().getTable(TableName.valueOf(tableName))
      scanner = table.getScanner(scan)


      val resultList = new util.ArrayList[Delete]

      var lineNum = 0
      var result = scanner.next()
      while (result != null) {
        result.getRow
        val row = result.getRow
        resultList.add(new Delete(row))
        result = scanner.next()
        lineNum += 1
        if (lineNum % 100 == 0) {
          table.delete(resultList)
          resultList.clear()
          logger.info("Deleted rows: " + lineNum)
        }
      }
      if (!resultList.isEmpty) {
        table.delete(resultList)
        logger.info("Deleted rows: " + lineNum)
      }

    } catch {
      case e => logger.error(e.toString)

    } finally {
      if (table != null)
        table.close()
      scanner.close()
    }
  }

  /**
    * Generate MD5 prefix rowkey by productID and deviceID or userID .
    */
   def genMD5Rk(productId: String, other: String): String = {
    val Body = s"$productId${Constants.AL_SPLIT}$other"
    Formatter.genPrefix(MessageDigest.getInstance("MD5"), Body, 4).substring(0, 3) + Constants.AL_SPLIT + Body
  }


  def getSeqResults(table: Table, rks: List[(String, String, String)]): Map[String, Int] = {
    var res = Map[String, Int]()
    HBaseUtils.getResults(table, rks.distinct).foreach(x => {
      val rk = x.getRow
      val vl = x.value()
      if (rk != null && vl != null){
        res += ( Bytes.toString(rk) -> Bytes.toInt(vl))
      }
    })
    res
  }

  def getGIDResults(table: Table, rks: List[(String, String, String)]): Map[String, String] = {
    var res = Map[String, String]()
    HBaseUtils.getResults(table, rks.distinct).foreach(x => {
      val rk = x.getRow
      val vl = x.value()
      if (rk != null && vl != null){
        res += ( Bytes.toString(rk) -> Bytes.toString(vl))
      }
    })
    res
  }

  def getBindResults(table: Table, rks: List[(String, String, String)]): Map[String, Boolean] = {
    var res = Map[String, Boolean]()
    HBaseUtils.getResults(table, rks.distinct).foreach(x => {
      val rk = x.getRow
      val vl = x.value()
      if (rk != null && vl != null){
        res += ( Bytes.toString(rk) -> Bytes.toBoolean(vl))
      }
    })
    res
  }

  def putSeqResults(table: Table, family: String, res: Map[String, Int]): Unit ={
    val out = ListBuffer[(String, String, String, _)]()
    res.foreach(x => {
      out.append((x._1, family, "sequence", x._2))
    })
    HBaseUtils.putKVs(table, out.toList)
  }

  def putGIDResults(table: Table, family: String, res: Map[String, String]): Unit ={
    val out = ListBuffer[(String, String, String, _)]()
    res.foreach(x => {
      out.append((x._1, family, "globalUserId", x._2))
    })
    HBaseUtils.putKVs(table, out.toList)
  }

  def putBindResults(table: Table, family: String, res: Map[String, Boolean]): Unit ={
    val out = ListBuffer[(String, String, String, _)]()
    res.foreach(x => {
      out.append((x._1, family, "deviceBindFlag", x._2))
    })
    HBaseUtils.putKVs(table, out.toList)
  }

  def putUserResults(table: Table, family: String, res: Map[String, String]): Unit ={
    val out = ListBuffer[(String, String, String, _)]()
    res.foreach(x => {
      out.append((x._1, family, "userId", x._2))
    })
    HBaseUtils.putKVs(table, out.toList)
  }
}
