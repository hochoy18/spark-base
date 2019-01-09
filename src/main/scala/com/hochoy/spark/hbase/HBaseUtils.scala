package com.hochoy.spark.hbase

import org.apache.hadoop.hbase.client.{Get, Put, Result, Table}
import org.apache.hadoop.hbase.util.Bytes
import com.hochoy.spark.utils.EnhancedMethods._

object HBaseUtils {
  implicit val family :String = "f"

  def putKV(table: Table, row: String,  qualifier: String, value: String)(implicit family: String): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(family.toByteArray, qualifier.toByteArray, value.toByteArray)
    table.put(put)
  }

  def putKV(table: Table, row: String,  qualifier: String, value: Long)(implicit family: String): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  def putKV(table: Table, row: String, qualifier: String, value: Float)(implicit  family: String): Unit = {
    val put = new Put(Bytes.toBytes(row))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
  }

  def batchPut(table: Table, row: String,   fqvs: List[FQV])(implicit  family: String): Unit = {
    val put = new Put(Bytes.toBytes(row))
    fqvs.foreach(fqv ⇒ put.addColumn(family.toByteArray, fqv._1.toByteArray, fqv._2.toByteArray))
    table.put(put)
  }


  def getResultByRow(table: Table, row: String): Result = {
    val g = new Get(Bytes.toBytes(row))
    val r = table.get(g)
    r
  }

  def test(q:String)(implicit f:String): Unit ={
    println(s"$q    $f")
  }

  def main(args: Array[String]): Unit = {
    test("12")
  }

  /**
    * (qualifier,value)
    */
  type FQV = (String, String)

}
