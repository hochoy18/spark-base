package com.hochoy.spark.sql.datasources

import java.nio.charset.Charset
import java.util.Base64

import scala.collection.mutable.ListBuffer
import com.alibaba.fastjson.{JSON, JSONObject}
import com.hochoy.cobub3_test.Constants
import com.hochoy.spark.hbase.GlobalHConnection
import com.hochoy.spark.utils.CaseClasses.Event
import com.hochoy.spark.utils.Constants._
import com.hochoy.spark.utils.{DateUtils, Util}
import com.hochoy.spark.utils.SparkUtils._
import com.hochoy.utils.BitmapUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hive.ql.index.bitmap.BitmapObjectOutput
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode}
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.storage.StorageLevel
import org.roaringbitmap.RoaringBitmap
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.parallel.immutable

/**
  *
  *
  * http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html
  *
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月26日 19:34
  * @version :  V1.0
  */
object SQLDataSourceTest1 {
  System.setProperty("HADOOP_USER_NAME", "hdfs")
  val spark = createSparkSession("SQL Data Source ")
  spark.sparkContext.setLogLevel("WARN")
  val warehouse_dir = spark.conf.get(SPARK_SQL_WAREHOUSE_DIR)

  def main(args: Array[String]) {
    funnel0
    //    retention
    //    score
  }

  def loadAndSave() = {
    //  Generic Load/Save Functions
    //  Manually Specifying Options
    val userDF = spark.read.load(s"${USER_SPARK_PATH}${FILE_PATH}users.parquet")
    val seleUserDF = userDF.select("name", "favorite_color")

    val v = spark.read.parquet(s"${USER_SPARK_PATH}${FILE_PATH}users.parquet")
    v.printSchema()
    val vs = v.select("name", "favorite_numbers", "favorite_color")
    vs.show()

    seleUserDF.write.parquet(s"${warehouse_dir}\\nac.parquet")

    val pDF = spark.read.format("json").load(s"${USER_SPARK_PATH}${FILE_PATH}sql_datasource.json")
    val seleDF = pDF.select("name", "age")
    seleDF.write.format("parquet").save(s"${warehouse_dir}\\r.parquet")

    println("===================================load data from manually-save file and vie regex-path =====================================")
    val getDF = spark.read.format("parquet").parquet(s"${warehouse_dir}\\r.parquet\\part-*.parquet")
    getDF.printSchema()
    getDF.select("name", "age").show()


    println("===================================Manually Specifying Options   -- csv =====================================")
    //  Manually Specifying Options
    //http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html#manually-specifying-options
    val peopleDF = spark.read.format(FORMAT_CSV)
      .option("sep", ";")
      .option("inferSchema", "true")
      .option("header", "true").load(s"${USER_SPARK_PATH}${FILE_PATH}people.csv")

    peopleDF.printSchema()
    val p = peopleDF.select("name", "age", "job")
    p.show()

    //    println("===================================Manually Specifying Options   -- orc  =====================================")
    //
    //    vs.show()
    //    vs.write.format(FORMAT_ORC)
    //      .option("orc.bloom.filter.columns", "favorite_color")
    //      .option("orc.dictionary.key.threshold", "1.0")
    //      .save(s"${USER_DIR}\\target\\vs.orc")

  }

  def runSQLAndSave() = {
    //Run SQL on files directly
    val sqlDF = spark.sql(s"select * from parquet.`${USER_SPARK_PATH}${FILE_PATH}users.parquet`")
    sqlDF.printSchema()
    sqlDF.show()

    //Save Modes
    //http://spark.apache.org/docs/latest/sql-data-sources-load-save-functions.html#save-modes
    /**
      * Optional save mode
      *
      * SaveMode.ErrorIfExists
      * SaveMode.Append
      * SaveMode.Overwrite
      * SaveMode.Ignore
      */
    sqlDF.write.mode(SaveMode.Append).format(FORMAT_JSON).save(s"${warehouse_dir}\\saveMode.json")

    val resDF = spark.read.json(s"${warehouse_dir}\\saveMode.json\\part-*.json")
    resDF.printSchema()
    resDF.select("favorite_color", "favorite_numbers", "name").show()

  }

  def runBasicParquetExample = {

    //Loading Data Programmatically
    /**
      * http://spark.apache.org/docs/latest/sql-data-sources-parquet.html#loading-data-programmatically
      *
      */
    val peopleDF = spark.read.json(s"${USER_SPARK_PATH}${FILE_PATH}people.json")
    peopleDF.write.mode(SaveMode.Append).parquet(s"${warehouse_dir}\\peo.parquet")
    val resDF = spark.read.parquet(s"${warehouse_dir}\\peo.parquet")
    resDF.createOrReplaceTempView("peo_p")

    resDF.printSchema()
    resDF.show()
    val nameDF = spark.sql("select name from peo_p ")

    val propMap = mutable.Map[String, String]()
    if (spark.sql("show tables like 'peo_p' ").count() > 0) {
      println(1111)
      val desc1 = spark.sql("desc  peo_p")
      desc1.take(50).foreach(row ⇒ {
        propMap += (row.getAs("col_name").toString → row.getAs("data_type").toString)
      })
    }

    propMap.foreach(println(_))

    val props = mutable.Map[String, String]("country" → "string", "age" → "int", "group1" → "boolean")
    props.toMap
    props.foreach(x ⇒ {
      propMap += (x._1 → x._2)
    })

    propMap.toList.foreach(println)

    nameDF.show()

    println("--------------------------------")


    import org.apache.spark.sql.functions.monotonically_increasing_id
    val addDF = nameDF.withColumn("group01", monotonically_increasing_id())
    addDF.printSchema()
    addDF.show()

    import org.apache.spark.sql.expressions.Window
    import org.apache.spark.sql.functions.row_number
    val w = Window.orderBy("name")
    val frame = nameDF.withColumn("group001", row_number().over(w))
    frame.printSchema()
    frame.show()

    println("==================================")
    val frame1 = frame.withColumn("group00001", row_number().over(w))
    frame1.printSchema()
    frame1.show()

    //

  }

  def runParquetSchemaMergingExample = {

    //Schema Merging
    /**
      * http://spark.apache.org/docs/latest/sql-data-sources-parquet.html#schema-merging
      */
    import spark.implicits._
    val path = {
      s"${warehouse_dir}\\schema_merging.parquet"
    }
    val squaresDF = spark.sparkContext.makeRDD(1 to 10).map(i => (i, Math.pow(i, 2))).toDF("value", "square")
    squaresDF.write.mode(SaveMode.Overwrite) parquet (s"${path}\\key=1")

    val cobesDF = spark.sparkContext.makeRDD(1 to 10).map(i => (i, Math.pow(i, 3))).toDF("value", "cube")
    cobesDF.write.mode(SaveMode.Overwrite).parquet(s"${path}\\key=2")

    //before merge
    println("===================================before merge =====================================")
    val v = spark.read.parquet(path)
    v.printSchema()
    v.select("value", "key", "square").show()

    println("===================================after merge =====================================")
    val mergedDF = spark.read.option("mergeSchema", "true").parquet(path)
    mergedDF.select("value", "key", "square", "cube").show()
    mergedDF.printSchema()


  }


  def clearAndCache = {

    //Loading Data Programmatically
    /**
      * http://spark.apache.org/docs/latest/sql-data-sources-parquet.html#loading-data-programmatically
      *
      */

    val peopleDF = spark.read.json(s"${USER_SPARK_PATH}${FILE_PATH}people.json")
    peopleDF.createOrReplaceTempView("people")
    spark.sql("cache table people")
    spark.sql("cache table p select * from people")
    spark.sql("select * from people").show(100)
    spark.sql("select * from p").show(100)
    spark.sql("show tables").show(100)
    spark.sql("clear cache")
    spark.sql("show tables").show(100)
    spark.sql("select * from people").show(100)
    spark.sql("select * from p").show(100)



    //

  }

  def retention: Unit = {
    retentionUDF()
    //    val sql = """SELECT tc.first_day, tc.by_day, collect_set(tc.global_user_id) AS usersets FROM (SELECT  TL.global_user_id, TL.date_group_by AS first_day, DATEDIFF(TR.servertime, TL.servertime) AS by_day FROM (select ta.global_user_id, ta.date_group_by, MIN(ta.servertime) AS servertime from  (select global_user_id, servertime, from_unixtime(unix_timestamp(servertime, 'yyyy-MM-dd'), 'yyyy-MM-dd') AS date_group_by from parquetTmpTable where  productid = '7' AND day >= 20190805 AND  day <= 20190815  AND  category = 'event' AND action = '$appClick' ) ta  GROUP BY ta.global_user_id, ta.date_group_by) TL LEFT JOIN (select ta.global_user_id, ta.date_group_by, MAX(ta.servertime) AS servertime from  (select global_user_id, servertime, from_unixtime(unix_timestamp(servertime, 'yyyy-MM-dd'), 'yyyy-MM-dd') AS date_group_by from parquetTmpTable where  productid = '7' AND day >= 20190805 AND  day <= 20190826  AND  category = 'cd' AND action = '$launch' ) ta  GROUP BY ta.global_user_id, ta.date_group_by) TR ON TL.global_user_id = TR.global_user_id AND TL.servertime < TR.servertime ) tc GROUP BY tc.first_day, tc.by_day  GROUPING SETS((tc.first_day,tc.by_day), (tc.first_day))"""
    val sql = "SELECT tc.first_day, tc.by_day, USERSET_UDF(collect_set(tc.global_user_id)) AS usersets,count(distinct(tc.global_user_id)) as userCount FROM (SELECT  TL.global_user_id, TL.date_group_by AS first_day, DATEDIFF(TR.servertime, TL.servertime) AS by_day FROM (select ta.global_user_id, ta.date_group_by, MIN(ta.servertime) AS servertime from  (select global_user_id, servertime, from_unixtime(unix_timestamp(servertime, 'yyyy-MM-dd'), 'yyyy-MM-dd') AS date_group_by from parquetTmpTable where  productid = '7' AND day >= 20190729 AND  day <= 20190827  AND  category = 'event' AND action = '$appClick' ) ta  GROUP BY ta.global_user_id, ta.date_group_by) TL LEFT JOIN (select ta.global_user_id, ta.date_group_by, MAX(ta.servertime) AS servertime from  (select global_user_id, servertime, from_unixtime(unix_timestamp(servertime, 'yyyy-MM-dd'), 'yyyy-MM-dd') AS date_group_by from parquetTmpTable where  productid = '7' AND day >= 20190729 AND  day <= 20190903  AND  category = 'event' AND action = 'e_sys_login' ) ta  GROUP BY ta.global_user_id, ta.date_group_by) TR ON TL.global_user_id = TR.global_user_id AND TL.servertime < TR.servertime ) tc GROUP BY tc.first_day, tc.by_day  GROUPING SETS((tc.first_day,tc.by_day), (tc.first_day))"
    val parquet = spark.read.parquet("/user/ubas/parquet")
    parquet.createOrReplaceTempView("parquetTmpTable")
    //    retentionTest

    //    val queryType = "count"

    retention(sql)

    //    val str1: Array[String] = retention(sql,"count")
    //    println(str1)
    //    val str2: Array[String] = retention(sql,"compress")
    //    println(str2)
  }

  case class Sale(date: String, userset: Set[Int])

  def retentionTest: Unit = {

    val l = List(
      ("20190808", Set(2, 4, 6, 8)),
      ("20190809", Set(1, 3, 5, 7, 9)),
      ("20190810", Set(1, 2, 9, 0)),
      ("20190808", Set(1, 2, 3, 4))
    )
    val rdd = spark.sparkContext.parallelize(l, 3).cache()

    val rdd1: RDD[(String, Set[Int])] = rdd

    val value: RDD[(String, Set[Int])] = rdd1.aggregateByKey(zeroValue = (Set[Int]()))(seqOp = (U, V) ⇒ U ++ V, combOp = (U1, U2) ⇒ U1 ++ U2)


    val tuples = value.collect()
    tuples.foreach(println(_))


  }

  val SEPARATOR1 = "\001";


  def retention(sql: String): Unit = {
    val userPathNode = "retention_tmp_job_xxxxxxxxxxxxxxxxxxxxxx"
    val baseSql = s"SELECT * FROM (${sql}) "

    spark.sql(baseSql).
      coalesce(spark.conf.get("spark.executor.instances", "1").toInt * spark.conf.get("spark.executor.cores", "2").toInt)
      .createOrReplaceTempView(userPathNode)
    spark.table(userPathNode).persist(StorageLevel.MEMORY_ONLY)

    val saveSql = s"select first_day, by_day, usersets , userCount FROM ${userPathNode} "
    spark.sql(saveSql).show(100)
    println("=======================")
    val rdd: RDD[Row] = spark.sql(saveSql).rdd.cache()

    val res0 = rdd.filter(row => row.getAs[Any]("by_day") != null).map(rowTransfer(_))
    res0.collect().foreach(println)

    val res100: RDD[(String, Array[Byte], Long)] = rdd.filter(row => row.getAs[Any]("by_day") == null).map(row => {
      val firstDay = row.getAs[String]("first_day").replaceAll("-", "")
      val byDay: Any = row.getAs[Any]("by_day")
      val userSetStr: String = row.getAs[String]("usersets")
      val userCount: Long = row.getAs[Long]("userCount")
      (s"${firstDay}${Constants.AL_SPLIT}${byDay}", userSetStr, userCount)
    }).groupBy(_._1).map(it => {
      it._2.toList.sortBy(_._3)(Ordering.Long.reverse).head
    }).map(v => {
      val rk: String = v._1
      val bytes: Array[Byte] = v._2.getBytes("iso8859-1")
      val value: Long = v._3
      (rk, bytes, value)
    })
    res100.foreach(println(_))

    //    val res0: RDD[(ImmutableBytesWritable, Put)] = resRdd0.map(rowTransfer(_))


  }

  def rowTransfer(row: Row) = {
    val firstDay = row.getAs[String]("first_day").replaceAll("-", "")
    val byDay: Any = row.getAs[Any]("by_day")
    val userSetStr: String = row.getAs[String]("usersets")
    val userCount: Any = row.getAs[Any]("userCount")
    val rk = s"${firstDay}${Constants.AL_SPLIT}${byDay}"
    //    val put = new Put(Bytes.toBytes(rk))
    //    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("count"), Bytes.toBytes(userCount.toLong))
    //    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("userset"), userSetStr.getBytes("iso8859-1"))
    //    (new ImmutableBytesWritable, put)
    (rk, userCount.toString)
  }


  def retention(sql: String, queryType: String): Array[String] = {

    val maxLine = 100
    val query = spark.sql(sql)

    if (queryType == "count") { // iscount = true
      val res1: RDD[(String, Set[String])] = query.rdd.map(row => {
        val firstDay = row.getAs[String]("first_day").replaceAll("-", "")
        val byDay: Any = row.getAs[Any]("by_day")
        val usersets: Set[String] = row.getAs[mutable.WrappedArray[String]]("usersets").toSet[String]
        (s"${firstDay}${com.hochoy.spark.utils.Constants.RETENTION_SEPARATOR}${byDay}", usersets)
      }).aggregateByKey(Set[String]())((U, V) ⇒ U ++ V, (U1, U2) ⇒ U1 ++ U2)
      res1.take(maxLine).map(v => s"${v._1}${com.hochoy.spark.utils.Constants.RETENTION_SEPARATOR}${v._2.size}")
    } else if (queryType == "compress") { // compress = true
      val res1: RDD[(String, Set[String])] = query.rdd.map(row => {
        val firstDay = row.getAs[String]("first_day").replaceAll("-", "")
        val byDay: Any = row.getAs[Any]("by_day")
        val usersets: Set[String] = row.getAs[mutable.WrappedArray[String]]("usersets").toSet[String]
        (s"${firstDay}${com.hochoy.spark.utils.Constants.RETENTION_SEPARATOR}${byDay}", usersets)
      }).aggregateByKey(Set[String]())((U, V) ⇒ U ++ V, (U1, U2) ⇒ U1 ++ U2)
      val res2 = res1.map(v => {
        val rb = RoaringBitmap.bitmapOf()
        v._2.foreach(v0 => rb.add(Integer.valueOf(v0.toString)))
        val bytes = BitmapUtils.serializeBitMapToByteArray(rb)
        (v._1, new String(bytes, Charset.forName("UTF-8")))
      })
      res2.take(maxLine).map(p => s"${p._1}${com.hochoy.spark.utils.Constants.RETENTION_SEPARATOR}${p._2}")
    } else {
      val res = query.take(maxLine)
      res.map(_.mkString(com.hochoy.spark.utils.Constants.RETENTION_SEPARATOR))
    }

  }


  private def userSetUdf(userSet: mutable.WrappedArray[String]): String = {
    val rb = RoaringBitmap.bitmapOf()
    userSet.foreach(v => rb.add(Integer.parseInt(v)))
    val bytes: Array[Byte] = BitmapUtils.serializeBitMapToByteArray(rb)
    new String(bytes, Charset.forName("UTF-8"))
  }


  def retention1(sql: String): Unit = {
    val parquet = spark.read.parquet("/user/ubas/parquet")
    parquet.createOrReplaceTempView("parquetTmpTable")
    val query = spark.sql(sql)

    val strings11: Array[String] = query.take(100).map(row => {
      val m = mutable.Map[String, Any]()
      row.schema.fieldNames.foreach(colName => {
        val value: Any = row.getAs[Any](colName)
        val putVal = if (value != null) {
          value
        } else "ALL"
        m += (colName -> putVal)
      })
      scala.util.parsing.json.JSONObject(m.toMap).toString()
    })
    val str = new scala.util.parsing.json.JSONObject(Map("result" -> new scala.util.parsing.json.JSONArray(strings11.toList))).toString()
    val jo = JSON.parseObject(str)
    val array = jo.getJSONArray("result")
    array.toArray().foreach(q => {
      val jo0 = JSON.parseObject(q.toString)
      val first_day = jo0.getString("first_day")
      val userset = jo0.getString("usersets")
      val bytes: Array[Byte] = userset.getBytes(Charset.forName("UTF-8"))
      val bitmap: RoaringBitmap = BitmapUtils.deSerializeByteArrayToBitMap(bytes)

      //      for (int i : rbm) {
      //        gidSet.add(i);
      //      }
      val set = bitmap.toArray.toSet
      println(set)
    })



    //    query.groupByKey(row =>{row})
    query.groupBy("first_day", "by_day", "usersets").count().show()
    query.show(100)
    //    query.foreach(row =>{
    //      val str = row.getAs[String](1)
    //      println(s"1..........$str")
    //      val str2 = row.getAs[String](0)
    //      println(s"0..........$str2")
    //      val str1 = row.getAs (2)
    //      println(s"2..........$str1")
    //
    //    })


    val res = query.take(100)

    val strings = res.map(_.mkString(SEPARATOR1))

    strings.foreach(v => {
      val splits = v.toString().split(SEPARATOR1)
      val first_day = splits(0)
      val by_day = splits(1)
      val userset = splits(2)
      val bytes: Array[Byte] = userset.getBytes(Charset.forName("UTF-8"))
      val bitmap: RoaringBitmap = BitmapUtils.deSerializeByteArrayToBitMap(bytes)

      val set = bitmap.toArray.toSet
      println(set)
    })

    println(strings)
    //
    //    val unit = query.rdd.map(row => {
    //      val first_day = row.getAs[String]("first_day").replaceAll("-", "")
    //      val by_day: Any = row.getAs[Any]("by_day")
    //      val usersets: Set[String] = row.getAs[mutable.WrappedArray[String]]("usersets").toSet[String]
    //      (s"${first_day}${Constants.AL_SPLIT}${by_day}", usersets)
    //    }).aggregateByKey(Set[String]())((U, V) ⇒ U ++ V, (U1, U2) ⇒ U1 ++ U2)
    //    unit


    //    val value = query.rdd.map(row => {
    //      val first_day = row.getAs[String]("first_day").replaceAll("-", "")
    //      val by_day: Any = row.getAs[Any]("by_day")
    //      val usersets: Set[String] = row.getAs[mutable.WrappedArray[String]]("usersets").toSet[String]
    //      (s"${first_day}${Constants.AL_SPLIT}${by_day}", usersets)
    //    }).aggregateByKey(Set[String]())((U, V) ⇒ U ++ V, (U1, U2) ⇒ U1 ++ U2)
    //      .repartition(spark.conf.get("spark.executor.instances").toInt * spark.conf.get("spark.executor.cores").toInt)
    //
    //
    //
    //    val tuples = value.collect()
    //    tuples.foreach(println(_))
    //    value.mapPartitions(p => {
    //        p.map(v => {
    //          val rb = RoaringBitmap.bitmapOf()
    //          v._2.foreach(v0 => rb.add(Integer.valueOf(v0.toString)))
    //          val bytes = BitmapUtils.serializeBitMapToByteArray(rb)
    //          val put = new Put(Bytes.toBytes(v._1))
    //          put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("count"), Bytes.toBytes(v._2.size.toLong))
    //          put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("userset"), bytes)
    //          (new ImmutableBytesWritable, put)
    //        })
    //      })


    //    tuples.size


  }

  def score: Unit = {
    val sql = """select name,lesson,score from score order by lesson,name,score"""
    score(sql)
  }

  def score(sql: String): Unit = {
    val json = spark.read.json("file:///D:\\advance\\bigdata\\spark\\sparktest01\\src\\main\\scala\\com\\hochoy\\spark\\sql\\data\\score.json")

    json.createTempView("score")
    spark.sql("select * from score").show
    spark.sql(sql).show(100)

  }

  def retentionUDF(): Unit = {
    //    spark.udf.register("DATE_DIFF", (startDate: String, endDate: String, pattern: String, dateType: String, defaultValue: String) ⇒
    //      DateUtils.date_diff(startDate, endDate, pattern, dateType, defaultValue))
    //
    //    spark.udf.register("DATE_FIRST", (date: String, pattern: String, dateType: String) ⇒
    //      DateUtils.date_first(date, pattern, dateType))

    spark.udf.register("USERSET_UDF", (userSet: mutable.WrappedArray[String]) => userSetUdf(userSet))

  }

  def test(userSet: mutable.WrappedArray[String]): String = {
    val rb = RoaringBitmap.bitmapOf()
    userSet.foreach(v => rb.add(Integer.valueOf(v)))
    val bytes: Array[Byte] = BitmapUtils.serializeBitMapToByteArray(rb)
    new String(bytes, Charset.forName("UTF-8"))
  }

  val logger = LoggerFactory.getLogger(getClass)

  def funnel0(): Int = {
    val parquet = spark.read.parquet("/user/ubas/parquet")
    parquet.createOrReplaceTempView("parquetTmpTable")
    val sql = "select global_user_id,sessionid,case when action='$exitPage' then pagetitle else case when action='$appClick' then element_id else action end end as action,unix_timestamp(clienttime),platform,version  from parquetTmpTable  where (category in ('event','usinglog') and deviceid is not null and day=20190930 and productid='22' and (action in ('e_sys_login','ylhkh_jh_smxx') or (action = '$exitPage' and pagetitle in('sy001','sy002')) or (action = '$appClick' and element_id in('lc_sjc_xqy_wh')) ))"
    val day = "20190930"
    val funnelId = "23"
    val fF = "e_sys_login||ylhkh_jh_smxx||lc_sjc_xqy_wh||sy001$$sy002"

    funnel(sql, day, funnelId, fF)
  }

  def funnel(sql: String, day: String, funnelId: String, fF: String): Int = {


    val bFunnelDef = fF.split("\\|\\|").toList
    val eventLog = spark.sql(sql)
    eventLog.show()
    val rows = eventLog.collect()
    rows.foreach(println(_))
    val events = eventLog.rdd.map { event => // event.getString(0)获取的可能是deviceid,global_user_id，看传进的sql里面定的是哪个
      Event(event.getString(0), event.getString(1), event.getString(2), event.getLong(3), event.getString(4), event.getString(5))
    }
    val eventC = events.collect()
    eventC.foreach(println(_))
    val eventGroups = events.groupBy { e => // TODO 根据 sessionID gid platform version 分组，再对分组后的value对 clienttime排序
      (e.sessionid, e.global_user_id, e.platform, e.version)
    }.mapValues { eventGroup =>
      eventGroup.toList.sortBy(_.clienttime)
    }

    val eventGroupsC = eventGroups.collect()
    eventGroupsC.foreach(println(_))

    val protoFun = eventGroups.flatMap {
      case (
        (_, gloabal_user_id, platform, version), eventGroup)
      =>
        //      val de = ((_, gloabal_user_id, platform, version), eventGroup)
        //      println(de)
        val funnels = Util.extract(eventGroup.map(_.action), bFunnelDef)
        val pairList = ListBuffer[(String, (Map[Int, Long], String))]() // rowkey ->
        pairList += s"$funnelId${Constants.AL_SPLIT}$day${Constants.AL_SPLIT}$platform${Constants.AL_SPLIT}$version" -> (funnels, gloabal_user_id) //(platform,version)
        pairList += s"$funnelId${Constants.AL_SPLIT}$day${Constants.AL_SPLIT}$platform${Constants.AL_SPLIT}null" -> (funnels, gloabal_user_id) //(platform)
        pairList += s"$funnelId${Constants.AL_SPLIT}$day${Constants.AL_SPLIT}null${Constants.AL_SPLIT}$version" -> (funnels, gloabal_user_id) //(version)
        pairList += s"$funnelId${Constants.AL_SPLIT}$day${Constants.AL_SPLIT}null${Constants.AL_SPLIT}null" -> (funnels, gloabal_user_id) //()


    }.cache()
    val protoFunC = protoFun.collect()
    protoFunC.foreach(println(_))


    val value = protoFun.map { case (key, (funnels, gloabal_user_id)) =>
      val qualifier = "CF"
      (key, qualifier) -> funnels
    }
    val tuples: Array[((String, String), Map[Int, Long])] = value.collect()
    val values1 = value.reduceByKey { (a, b) =>
      a.map { case (k, v) =>
        k -> (v + b(k))
      }
    }
    val tuples1: Array[((String, String), Map[Int, Long])] = values1.collect()
    // (rk,"CF"), map)
    // count total funnels
    val c = values1.map { case (key, map) =>
      val value = (0 until map.size).map(k => map(k)).mkString(",")
      (key, value)
    }.collect()
    c.foreach(println(_))

    // count unique funnels
    val uc = protoFun.map { case (key, (funnels, gloabal_user_id)) =>
      val qualifier = "UF"
      (key, qualifier) -> funnels.map { case (k, v) =>
        k -> (if (v != 0L) Some(gloabal_user_id) else None)
      }
    }.aggregateByKey(mutable.HashMap.empty[Int, mutable.HashSet[String]])(
      Util.mergeValuesToSetByKey,
      Util.unionSetsByKey
    ).mapPartitions { p =>
      val saveUsers = true
      val hTable = if (saveUsers) {
        //        GlobalHConnection.setConf(hbaseZKConfig)
        //        GlobalHConnection.getConn().getTable(TableName.valueOf(resultTable))
      } else null
      val res = p.map {
        // map的类型：HashMap[Int, mutable.HashSet[String]] ，key表示funelId的层次，
        // 比如0->[userid1,userid2,userid3],1->[userid1,userid2],2->[userid1]
        // 即该转换率funnelId，第一个事件[userid1,userid2,userid3]访问了
        //                    第二个事件[userid1,userid2]访问了
        //                    第三个事件[userid1]访问了
        case (key, map) =>
          val value = (0 until map.size)
            .map { k =>
              if (saveUsers) {
                val rb = RoaringBitmap.bitmapOf()
                map(k).foreach { gloabal_user_id =>
                  try {
                    val gid = gloabal_user_id.toInt
                    rb.add(gid)
                  } catch {
                    case e: Exception => println(e)
                  }

                }
                val bytes = BitmapUtils.serializeBitMapToByteArray(rb)
                val put = new Put(Bytes.toBytes(key._1))
                put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("USERS_" + k.toString), bytes)
                //                hTable.put(put)
              }
              map(k).size.toLong
            }.mkString(",")
          (key, value)
      }
      //      if(saveUsers) hTable.close
      res

    }.collect()

    uc.foreach(println(_))
    val res = c ++ uc // (rowkey,qualifer) -> value
    for ((k, v) <- res) {
      //      logger.info(":::::::" + k._1 + "," + k._2 + "===" + v)
    }
    res.size

  }


}


object test {
  val SEPARATOR1 = "@@@@@@@";

  def main(args: Array[String]): Unit = {
    //    maintest()
    test
    //    test22
    //    test2

  }

  def test2: Unit = {
    val str = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm"
    val b = str.getBytes("UTF-8")
    val str1 = new String(b, "UTF-8")
    println(str)
    println(str1)
    println(str == str1)
  }


  def test(): Unit = {
    val sets = Set(251509, 689774, 791875, 585183, 261939, 710521, 548627, 534631, 641978, 744280, 788989, 699206, 787481, 686574, 774796, 770152, 733866, 634724, 715441, 784650, 269714, 821309, 693375, 59061, 811572, 800323, 789657, 761141, 770884, 776932, 452445, 776635, 679616, 697488, 724475, 440930, 824079, 216870, 713072, 148606, 775201, 141714, 825750, 733997, 675330, 747250, 523798, 261752, 481828, 20523, 763074, 335810, 257962, 380568, 683957, 113427, 827644, 153684, 803905, 144817, 703023, 251164, 529006, 747688, 172543, 734667, 739709, 778675, 645290, 359007, 324265, 772756, 707091, 789543, 559597, 744493, 733868, 725950, 692993, 671368, 761605, 122705, 419594, 693748, 118714, 65637, 354261, 384677, 722771, 830193, 33458, 685156, 741151, 379, 485360, 264453, 788707, 705533, 39717, 762294, 790442, 727435, 31659, 709866, 797056, 83292, 530926, 756513, 819605, 218574, 446882, 664861, 677208, 511363, 593555, 825838, 707203, 357745, 74559, 735500, 189993, 714185, 328460, 359521, 715958, 7512, 770233, 735150, 807487, 808006, 33271, 67835, 761086, 706529, 695919, 392649, 214893, 530655, 810664, 827562, 798740, 727204, 446727, 830333, 699944, 775189, 337537, 729605, 796983, 720692, 430610, 777679, 743047, 716804, 701248, 30908, 825194, 748689, 560001, 677439, 761970, 207043, 94028, 398315, 120860, 703793, 389196, 726841, 721984, 718016, 508423, 812608, 711107, 696115, 18086, 682317, 255752, 499082, 722596, 134636, 741454, 793563, 474062, 831283, 784900, 821033, 814338, 382292, 698569, 778978, 488352, 684399, 728008, 709404, 816151, 699534, 695451, 765116, 731668, 733799, 476939, 362007, 720954, 701948, 504413, 760609, 239436, 683566, 213791, 744300, 730851, 415639, 416706, 92499, 757611, 776116, 421565, 558783, 815662, 475210, 794025, 779629, 21067, 733723, 343007, 576826, 736127, 14837, 98018, 741061, 746891, 815876, 19126, 753745, 827160, 241316, 750913, 716473, 696167, 812781, 9822, 784235, 534006, 720241, 724358, 399188, 775131, 765125, 210455, 823723, 565652, 722021, 428341, 751868, 793960, 767676, 57173, 739946, 186683, 692062, 727815, 805912, 129176, 683656, 252210, 510895, 734061, 724608, 765018, 704948, 195429, 818830, 741032, 797389, 790631, 776988, 776970, 720136, 410455, 765292, 726447, 785569, 736603, 715524, 54649, 476919, 146380, 457864, 814432, 659805, 520897, 356037, 788694, 780042, 404619, 755723, 706879, 417840, 699678, 297081, 740905, 740553, 441366, 768794, 768268, 814840, 760684, 72629, 797768, 273597, 473235, 687686, 482300, 782642, 714609, 804950, 529093, 743973, 489094, 470242, 164597, 19998, 690786, 742210, 801491, 227823, 496820, 497040, 754275, 489594, 828427, 524487, 538345, 466574, 817404, 286480, 735420, 793544, 626482, 634561, 682506, 203511, 753531, 824814, 654640, 763513, 714417, 735595, 336391, 386480, 359381, 115522)
    //    val sets = Set(1,2,3,4,5,6,7,8,9)
    val rb = RoaringBitmap.bitmapOf()
    println(sets.toList.sorted)
    sets.foreach(v => rb.add(v))
    println(rb.toArray.toList.sorted)

    val bytes: Array[Byte] = BitmapUtils.serializeBitMapToByteArray(rb)

    val btm1 = BitmapUtils.deSerializeByteArrayToBitMap(bytes)
    println(btm1.toArray.toList.sorted)

    val str = new String(bytes, "iso8859-1")
    val bytes12 = str.getBytes("iso8859-1")
    val bitmap001 = BitmapUtils.deSerializeByteArrayToBitMap(bytes12)
    val array = bitmap001.toArray.toSet
    println("\n" + array.toList.sorted)
  }

  def test22(param: String): Array[Byte] = {
    val bytes: Array[Byte] = param.getBytes()
    val encoded = Base64.getEncoder().encodeToString(bytes);
    val decoded = Base64.getDecoder().decode(encoded);
    decoded
  }

  def test22(param: Array[Byte]): String = {
    //    val bytes: Array[Byte] = param.getBytes()
    val encoded = Base64.getEncoder().encodeToString(param);
    val decoded = Base64.getDecoder().decode(encoded);
    new String(decoded)
  }


  def maintest(): Unit = {

    val s = Set(251509, 689774, 791875, 585183, 261939, 710521, 548627, 534631, 641978, 744280, 788989, 699206, 787481, 686574, 774796, 770152, 733866, 634724, 715441, 784650, 269714, 821309, 693375, 59061, 811572, 800323, 789657, 761141, 770884, 776932, 452445, 776635, 679616, 697488, 724475, 440930, 824079, 216870, 713072, 148606, 775201, 141714, 825750, 733997, 675330, 747250, 523798, 261752, 481828, 20523, 763074, 335810, 257962, 380568, 683957, 113427, 827644, 153684, 803905, 144817, 703023, 251164, 529006, 747688, 172543, 734667, 739709, 778675, 645290, 359007, 324265, 772756, 707091, 789543, 559597, 744493, 733868, 725950, 692993, 671368, 761605, 122705, 419594, 693748, 118714, 65637, 354261, 384677, 722771, 830193, 33458, 685156, 741151, 379, 485360, 264453, 788707, 705533, 39717, 762294, 790442, 727435, 31659, 709866, 797056, 83292, 530926, 756513, 819605, 218574, 446882, 664861, 677208, 511363, 593555, 825838, 707203, 357745, 74559, 735500, 189993, 714185, 328460, 359521, 715958, 7512, 770233, 735150, 807487, 808006, 33271, 67835, 761086, 706529, 695919, 392649, 214893, 530655, 810664, 827562, 798740, 727204, 446727, 830333, 699944, 775189, 337537, 729605, 796983, 720692, 430610, 777679, 743047, 716804, 701248, 30908, 825194, 748689, 560001, 677439, 761970, 207043, 94028, 398315, 120860, 703793, 389196, 726841, 721984, 718016, 508423, 812608, 711107, 696115, 18086, 682317, 255752, 499082, 722596, 134636, 741454, 793563, 474062, 831283, 784900, 821033, 814338, 382292, 698569, 778978, 488352, 684399, 728008, 709404, 816151, 699534, 695451, 765116, 731668, 733799, 476939, 362007, 720954, 701948, 504413, 760609, 239436, 683566, 213791, 744300, 730851, 415639, 416706, 92499, 757611, 776116, 421565, 558783, 815662, 475210, 794025, 779629, 21067, 733723, 343007, 576826, 736127, 14837, 98018, 741061, 746891, 815876, 19126, 753745, 827160, 241316, 750913, 716473, 696167, 812781, 9822, 784235, 534006, 720241, 724358, 399188, 775131, 765125, 210455, 823723, 565652, 722021, 428341, 751868, 793960, 767676, 57173, 739946, 186683, 692062, 727815, 805912, 129176, 683656, 252210, 510895, 734061, 724608, 765018, 704948, 195429, 818830, 741032, 797389, 790631, 776988, 776970, 720136, 410455, 765292, 726447, 785569, 736603, 715524, 54649, 476919, 146380, 457864, 814432, 659805, 520897, 356037, 788694, 780042, 404619, 755723, 706879, 417840, 699678, 297081, 740905, 740553, 441366, 768794, 768268, 814840, 760684, 72629, 797768, 273597, 473235, 687686, 482300, 782642, 714609, 804950, 529093, 743973, 489094, 470242, 164597, 19998, 690786, 742210, 801491, 227823, 496820, 497040, 754275, 489594, 828427, 524487, 538345, 466574, 817404, 286480, 735420, 793544, 626482, 634561, 682506, 203511, 753531, 824814, 654640, 763513, 714417, 735595, 336391, 386480, 359381, 115522)
    val rb = RoaringBitmap.bitmapOf()
    println(s.size)
    s.foreach(v => rb.add(v))

    val bytes: Array[Byte] = BitmapUtils.serializeBitMapToByteArray(rb)

    val str = new String(bytes, "UTF-8")


    val bytes12 = str.getBytes("UTF-8")
    val bitmap001 = BitmapUtils.deSerializeByteArrayToBitMap(bytes12)
    println(bitmap001.getCardinality)
    val set1 = scala.collection.mutable.Set[Int]()
    val iterator = bitmap001.iterator()
    while (iterator.hasNext) {
      val i = iterator.next()
      set1 + (i)
    }

    println(set1.size)
    println(s.toList.sorted)
    println(set1.toList.sorted)


    val str1 = "2019-09-09" + SEPARATOR1 + "null" + SEPARATOR1
    val m = mutable.Map("first_day" -> "20190909", "by_day" -> "null", "usersets" -> str)
    //    val jo = JSONObject(m);
    //    jo.put("first_day","20190909")
    //    jo.put("by_day" -> "null")
    //    jo.put("usersets"->str)
    //    val str11 = jo.toString()

    //    println(str11)
    val xxxxx = scala.util.parsing.json.JSONObject(m.toMap).toString()
    println(xxxxx)


    val splits = str1.split(SEPARATOR1)
    println(splits.size)
    println(splits(0))
    println(splits(1))
    println(splits(2))

    val bytes1 = splits(2).getBytes("UTF-8")

    val bitmap = BitmapUtils.deSerializeByteArrayToBitMap(bytes1)
    println(bitmap)


    System.exit(-1)
    val arr = Array(
      ("2019-08-08", "null", Set(1, 23, 4, 5)),
      ("2019-08-18", "1", Set(12, 3, 4, 5)),
      ("2019-08-08", "2", Set(2, 3, 4, 5, 6)),
      ("2019-08-02", "4", Set(1, 8, 7, 6, 5))
    )

    val strings = arr.map(x => {
      val rb = RoaringBitmap.bitmapOf()
      x._3.foreach(v => rb.add(Integer.valueOf(v)))
      val bytes: Array[Byte] = BitmapUtils.serializeBitMapToByteArray(rb)
      val str = new String(bytes, Charset.forName("UTF-8"))
      (x._1, x._2, str)
    }).map(vv => s"${vv._1}${Constants.AL_SPLIT}${vv._2}${Constants.AL_SPLIT}${vv._3}")
    splits.foreach(v => {
      val ss = v.split(Constants.AL_SPLIT)
      val day = ss(0)
      val by = ss(1)
      val set = ss(2)
      println(s"${day}   $by   $set    ")
      val bytes = set.getBytes(Charset.forName("UTF-8"))
      val bitmap: RoaringBitmap = BitmapUtils.deSerializeByteArrayToBitMap(bytes)


    })
  }
}