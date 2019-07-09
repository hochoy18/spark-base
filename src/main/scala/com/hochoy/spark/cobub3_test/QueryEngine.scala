package com.hochoy.spark.cobub3_test

import java.util.StringJoiner

import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 16:14
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object QueryEngine {


  def main(args: Array[String]): Unit = {
    val config = Map(
      "namespace" → "cobub3",
      "prop" → """{"11128_groupsixbak":"string","11128_latitudeb":"int","11128_latitudeb":"string"}""",
      "groupid" → """{11128_groupfivebak,11128_groupsixbak}"""
    )
    createViewOfUsersTable(null, config)
  }


  def createViewOfUsersTable(spark: SparkSession, config: Map[String, String]): Unit = {
    val userIdBase = "userIdBase"
    val select = s" select %s  from  ${userIdBase} ub  "
    val join = " left join %s  on  ub.pk = %s.pk "

    val joinTabList = new StringJoiner(" ")
    val selectFieldList = new StringJoiner(", ")

    val ns = config("namespace")
    val mapStr = config("prop")
    val groupid = config("groupid")
    val prop = JSON.toKVMap(mapStr, "string")
    val joinList = ListBuffer[String]()
    val fieldSet = mutable.Set[String]()
    var i = 0
    if ("{}" != groupid.trim) {
      val groupids = groupid.substring(1, groupid.length - 1).split(",")
      groupids.foreach(field ⇒ {
        joinList += String.format(join, s"${field}Base ug${i}", s"ug${i}")
        fieldSet.add(field)
        i += 1
      })
    }

    if (!prop.isEmpty) {
      prop.toMap.foreach(field ⇒ {
        joinList += String.format(join, s"${field._1}Base up${i}", s"up${i}")
        fieldSet.add(field._1)
        i += 1
      })
    }

    if (!joinList.isEmpty) {
      joinList.toList.foreach(joinTabList.add(_))
    }

    if (!fieldSet.isEmpty) {
      fieldSet.toStream.foreach(selectFieldList.add(_))
    }
    val selectFields = String.format(select, if (!selectFieldList.toString.isEmpty) s"ub.pk as pk, ub.userid as userId, ${selectFieldList.toString}" else "ub.pk as pk, ub.userid as userId")
    val joinSQL = selectFields + joinTabList.toString
    println(joinSQL)

    val view = "CREATE OR REPLACE TEMPORARY VIEW usersTable  AS ( %s )"

    val sql = String.format(view ,joinSQL)
    println(sql)
        spark.sql(sql)
  }

  def genSQL(list: List[(String, String)]): String = {
    val view = "CREATE OR REPLACE TEMPORARY VIEW usersTable  AS ( %s )"
    val relation = " FULL JOIN "
    val joiner = new StringJoiner(relation)
    val sb = new StringBuilder()
    sb.append("")

    ""
  }


}
