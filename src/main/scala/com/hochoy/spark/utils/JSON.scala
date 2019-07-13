package com.hochoy.spark.utils

import java.text.SimpleDateFormat

import com.fasterxml.jackson.core.JsonParseException
import org.apache.spark.sql.{ColumnName, Row, SQLContext, SaveMode}
import org.apache.spark.sql.types.{DataTypes, StructType}

import scala.collection.mutable
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer

object JSON {
  /**
    * Convert a one-level JSON String to a mutable.Map.
    *
    * @param j JSON String
    * @return if j is a valid JSON String, return a mutable.Map[String, String]. Otherwise, return
    *         an empty Map.
    */
  def toMutableMap(j: String, defaultValue: String = Constants.Unknown): mutable.Map[String, String] = {
    try {
      val map = parse(j).values.asInstanceOf[Map[String, AnyRef]].mapValues(Some(_).mkString.trim)
        .withDefaultValue(defaultValue)
      mutable.HashMap(map.toSeq: _*)
    } catch {
      case ex: JsonParseException => mutable.Map.empty
    }
  }

  /**
    * String to Map
    * [[ notice:  for only one level  map's string  ]]
    *
    * @param j
    * @param defaultValue
    * @return
    */
  def toKVMap(j: String, defaultValue: String = Constants.Unknown): mutable.Map[String, String] = {


    if (!(j.endsWith("}") && j.startsWith("{"))) mutable.Map[String, String]()

    if (JSON.toMutableMap(j, defaultValue = defaultValue).isEmpty && "{}" != (j.trim) && j.length > 2) {
      val result = collection.mutable.Map[String, String]()
      j.substring(1, j.length - 1).split(",").map(x ⇒ {
        val strings: Array[String] = x.split(":", 2)
        if (strings.length == 2) result += (strings(0) → strings(1))
      })
      result
    } else {
      JSON.toMutableMap(j, defaultValue = defaultValue)
    }
  }


  def toListMap(j: String): List[Map[String, _]] = {
    try {
      (parse(j) \ "data").children.map { jValue =>
        val properties = jValue \ "properties"
        val removed = jValue.removeField({
          case ("properties", _) => true
          case _ => false
        })
        properties.merge(removed).values.asInstanceOf[Map[String, _]]
      }
    } catch {
      case ex: JsonParseException => {
        List.empty
      }
      case ex: Exception => {
        List.empty
      }
    }
  }
  val actionSchema = new StructType()
    .add("action", DataTypes.StringType, false)
    .add("appkey", DataTypes.StringType, false)
    .add("productid", DataTypes.StringType, false)
    .add("deviceid", DataTypes.StringType, false)
    .add("userid", DataTypes.StringType, true)
    .add("sessionid", DataTypes.StringType, false)
    .add("latitude", DataTypes.FloatType, true)
    .add("longitude", DataTypes.FloatType, true)
    .add("clienttime", DataTypes.TimestampType, false)
    .add("servertime", DataTypes.TimestampType, true)
    .add("uuid", DataTypes.StringType, true)
    .add("ip", DataTypes.StringType, true)
    .add("country", DataTypes.StringType, true)
    .add("region", DataTypes.StringType, true)
    .add("city", DataTypes.StringType, true)
    .add("useragent", DataTypes.StringType, true)
    .add("request", DataTypes.StringType, true)
    .add("exceptiontype", DataTypes.StringType, true)
    .add("action_content", DataTypes.StringType, true)
    .add("lib_version", DataTypes.StringType, true)
    .add("global_user_id", DataTypes.StringType, true) //根据user_id映射的整数
    .add("is_new_user", DataTypes.BooleanType, true) //根据user_id判断是否是新的user_id,sparksql有个bug，针对booleanType，设置schema含有该is_user_id,如果某个parquet文件实际上没有这个字段，查的时候会报错，如果booleanType字段改成StringType就没有问题
    .add("is_new_device", DataTypes.BooleanType, true)
    //properties
    .add("channelid", DataTypes.StringType, true)
    .add("duration", DataTypes.LongType, true)
    .add("platform", DataTypes.StringType, true)
    .add("version", DataTypes.StringType, true)
    .add("osversion", DataTypes.StringType, true)
    .add("network", DataTypes.StringType, true)
    .add("page", DataTypes.StringType, true)
    .add("pagetitle", DataTypes.StringType, true)
    .add("element_id", DataTypes.StringType, true)
    .add("refer", DataTypes.StringType, true)
    .add("language", DataTypes.StringType, true)
    .add("manufacturer", DataTypes.StringType, true)
    .add("model", DataTypes.StringType, true)
    .add("screen_width", DataTypes.ShortType, true)
    .add("screen_height", DataTypes.ShortType, true)
    .add("mccmnc", DataTypes.StringType, true)
    .add("is_update", DataTypes.BooleanType, true)
    .add("utm_source", DataTypes.StringType, true)
    .add("utm_medium", DataTypes.StringType, true)
    .add("utm_campaign", DataTypes.StringType, true)
    .add("utm_content", DataTypes.StringType, true)
    .add("category", DataTypes.StringType, false)
    .add("actionattach", DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType, true), true)
    .add("by_day", DataTypes.IntegerType, true)
    .add("day", DataTypes.StringType, false)
    .add("hour", DataTypes.StringType, false)


  def main(args: Array[String]): Unit = {
//    test()
    test111

//    val aa = "{\"col\":21,\"line\":113}"
//    val map: Map[String, String] = Utils.toMapOrNull(aa)
//    val line = map("line")
//    println(line.isInstanceOf[Int])
//    println(line.isInstanceOf[String])
  }


  def test(): Unit = {

    //{"data":[{"appkey":"10afd440920111e6ac2374dfbf1bfb16","properties":{"utm_campaign":"双十一剁手节","is_update":"false","network":"EDGE","language":"ca_ES_EURO","utm_content":"0814-tool","channelid":"360","screen_width":256,"utm_source":"今日头条","mccmnc":"45400","pagetitle":"PageTitle_1444538903572","platform":"android","version":"1.1","utm_medium":"BottomBanner","screen_height":800,"duration":15912,"is_new_device":"false","model":"华为Mate20 Pro","osversion":"2.3.7","page":"RegistActivity","manufacturer":"nokia","refer":""},"userid":"20000459021","longitude":"121.5111137000","lib_version":"1.0.0","clienttime":"2019-01-04 00:01:18.653","latitude":"31.1676971000","sessionid":"28b85cafcbea492284dadb2c412e1272","deviceid":"iOS_DEVICEID_1535066713776476","action":"e_sys_transaction","actionattach":{}}]}
    var str1 = ""
    str1 = "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"10014\",\"city\":\"\",\"latitude\":\"\",\"ip\":\"192.168.1.82\",\"useragent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36\",\"servertime\":\"2019-07-09 20:54:09.000\",\"sessionid\":\"1m97x1vfsq5dw8i5grm9y9qk54m382rd\",\"lib_version\":\"3.0.0\",\"deviceid\":\"FCB834B2-D059-437B-BC5E-394732C52C30\",\"userid\":\"151915191519\",\"uuid\":\"a24b70aa4ee7138647eb51c18ede0173\",\"actionattach\":{\"msg\":\"ReferenceError: arr is not defined\\n    at clickTagA (file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html:113:21)\\n    at HTMLAnchorElement.onclick (file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html#:50:39)\",\"col\":21,\"line\":113,\"url\":\"file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html\"},\"action\":\"$error\",\"appkey\":\"45245246246246\",\"clienttime\":\"2019-07-09 21:09:06.355\",\"region\":\"局域网\",\"properties\":{\"screen_width\":1920,\"utm_campaign\":\"\",\"utm_medium\":\"\",\"element_id\":\"\",\"language\":\"zh-cn\",\"version\":\"1.0\",\"platform\":\"pc\",\"manufacturer\":\"Apple\",\"screen_height\":1080,\"is_new_device\":true,\"refer\":\"html/h5event.html#\",\"model\":\"Mac\",\"page\":\"html/h5event.html#\",\"pagetitle\":\"事件页面\",\"osversion\":\"MacOS10.13.6\",\"channelid\":\"001\",\"utm_source\":\"\",\"utm_content\":\"\"},\"longitude\":\"\"}]}"
//    str1 = "{ \"data\": [{ \"country\": \"局域网\", \"request\": \"POST /action HTTP/1.1\", \"productid\": \"10014\", \"city\": \"\", \"latitude\": \"\", \"ip\": \"192.168.1.82\", \"useragent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36\", \"servertime\": \"2019-07-09 20:54:09.000\", \"sessionid\": \"1m97x1vfsq5dw8i5grm9y9qk54m382rd\", \"lib_version\": \"3.0.0\", \"deviceid\": \"FCB834B2-D059-437B-BC5E-394732C52C30\", \"userid\": \"151915191519\", \"uuid\": \"a24b70aa4ee7138647eb51c18ede0173\", \"actionattach\": {}, \"action\": \"$error\", \"appkey\": \"45245246246246\", \"clienttime\": \"2019-07-09 21:09:06.355\", \"region\": \"局域网\", \"properties\": { \"screen_width\": 1920, \"utm_campaign\": \"\", \"utm_medium\": \"\", \"element_id\": \"\", \"language\": \"zh-cn\", \"version\": \"1.0\", \"platform\": \"pc\", \"manufacturer\": \"Apple\", \"screen_height\": 1080, \"is_new_device\": true, \"refer\": \"html/h5event.html#\", \"model\": \"Mac\", \"page\": \"html/h5event.html#\", \"pagetitle\": \"事件页面\", \"osversion\": \"MacOS10.13.6\", \"channelid\": \"001\", \"utm_source\": \"\", \"utm_content\": \"\" }, \"longitude\": \"\" }] }"
//    str1 = "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"10014\",\"city\":\"\",\"latitude\":\"\",\"ip\":\"192.168.1.82\",\"useragent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36\",\"servertime\":\"2019-07-09 20:54:09.000\",\"sessionid\":\"1m97x1vfsq5dw8i5grm9y9qk54m382rd\",\"lib_version\":\"3.0.0\",\"deviceid\":\"FCB834B2-D059-437B-BC5E-394732C52C30\",\"userid\":\"151915191519\",\"uuid\":\"a24b70aa4ee7138647eb51c18ede0173\",\"actionattach\":{\"msg\":\"ReferenceError: arr is not defined\\n    at clickTagA (file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html:113:21)\\n    at HTMLAnchorElement.onclick (file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html#:50:39)\",\"col\":\"21\",\"line\":\"113\",\"url\":\"file:///Users/xingduxu/Desktop/jsbank_analysis_ios/jsyh_v3/JS_SDK/Web_JS/html/h5event.html\"},\"action\":\"$error\",\"appkey\":\"45245246246246\",\"clienttime\":\"2019-07-09 21:09:06.355\",\"region\":\"局域网\",\"properties\":{\"screen_width\":1920,\"utm_campaign\":\"\",\"utm_medium\":\"\",\"element_id\":\"\",\"language\":\"zh-cn\",\"version\":\"1.0\",\"platform\":\"pc\",\"manufacturer\":\"Apple\",\"screen_height\":1080,\"is_new_device\":true,\"refer\":\"html/h5event.html#\",\"model\":\"Mac\",\"page\":\"html/h5event.html#\",\"pagetitle\":\"事件页面\",\"osversion\":\"MacOS10.13.6\",\"channelid\":\"001\",\"utm_source\":\"\",\"utm_content\":\"\"},\"longitude\":\"\"}]}"
    val v = toListMap(str1)

    v.map { case json ⇒ {
      val action = json("action").toString
      val productid = json("productid").toString
      val deviceid = json("deviceid").toString
      val userid = json("userid").toString
      val sessionid = json("sessionid").toString
      val servertime = json("servertime").toString
      val transformedResult = transformServerTime(servertime)
      val day = transformedResult._1
      val hour = transformedResult._2
      val isUpdate = Utils.parseBoolean(json.getOrElse("is_update", null))
      var byDay = -999
      val isNewDevice0 = Utils.parseBoolean(json.getOrElse("is_new_device", null))
      val category = defineCategory(action)
      var isNewUser = false
      var isNewDevice = false
      val duration = Utils.toLongOrZero(json.getOrElse("duration", null))

      val row:Row = Row(
        Utils.toStringOrNull(json("action")),
        Utils.toStringOrNull(json("appkey")),
        Utils.toStringOrNull(json("productid")),
        Utils.toStringOrNull(json("deviceid")),
        Utils.toStringOrNull(json.getOrElse("userid", null)),
        Utils.toStringOrNull(json("sessionid")),
        Utils.toFloat(json.getOrElse("latitude", null)),
        Utils.toFloat(json.getOrElse("longitude", null)),
        Utils.getTimestamp(json("clienttime")),
        Utils.getTimestamp(json.getOrElse("servertime", null)),
        Utils.toStringOrNull(json.getOrElse("uuid", null)),
        Utils.toStringOrNull(json.getOrElse("ip", null)),
        Utils.toStringOrNull(json.getOrElse("country", null)),
        Utils.toStringOrNull(json.getOrElse("region", null)),
        Utils.toStringOrNull(json.getOrElse("city", null)),
        Utils.toStringOrNull(json.getOrElse("useragent", null)),
        Utils.toStringOrNull(json.getOrElse("request", null)),
        Utils.toStringOrNull(json.getOrElse("exceptiontype", null)),
        Utils.toStringOrNull(json.getOrElse("action_content", null)),
        Utils.toStringOrNull(json.getOrElse("lib_version", null)),
        Utils.toStringOrNull("gid________"),
        isNewUser,
        isNewDevice,
        Utils.toStringOrNull(json.getOrElse("channelid", null)),
        if( duration > 1800000L ) 0L else duration,
        Utils.toStringOrNull(json.getOrElse("platform", null)),
        Utils.toStringOrNull(json.getOrElse("version", null)),
        Utils.toStringOrNull(json.getOrElse("osversion", null)),
        Utils.toStringOrNull(json.getOrElse("network", null)),
        Utils.toStringOrNull(json.getOrElse("page", null)),
        Utils.toStringOrNull(json.getOrElse("pagetitle", null)),
        Utils.toStringOrNull(json.getOrElse("element_id", null)),
        Utils.toStringOrNull(json.getOrElse("refer", null)),
        Utils.toStringOrNull(json.getOrElse("language", null)),
        Utils.toStringOrNull(json.getOrElse("manufacturer", null)),
        Utils.toStringOrNull(json.getOrElse("model", null)),
        Utils.toShortOrZero(json.getOrElse("screen_width", null)),
        Utils.toShortOrZero(json.getOrElse("screen_height", null)),
        Utils.toStringOrNull(json.getOrElse("mccmnc", null)),
        isUpdate,
        Utils.toStringOrNull(json.getOrElse("utm_source", null)),
        Utils.toStringOrNull(json.getOrElse("utm_medium", null)),
        Utils.toStringOrNull(json.getOrElse("utm_campaign", null)),
        Utils.toStringOrNull(json.getOrElse("utm_content", null)),
        category,
        Utils.toMapOrNull(json.getOrElse("actionattach", null)),
        if( byDay >= 0 ) byDay else null,
        day,
        hour
      )
      println(row)
      val l = new ListBuffer[Row]()
      l+=row


//      System.exit(-1)
      val sparkContext = SparkUtils.createSparkContext("hhhhhhhhhh")
      val rows = sparkContext.parallelize(l)
      val  sqlContext = new SQLContext(sparkContext)
      val dataFrame = sqlContext.createDataFrame(rows,actionSchema)
      dataFrame.printSchema()
      dataFrame.repartition(new ColumnName("day"), new ColumnName("hour"), new ColumnName("productid"), new ColumnName("category"))
        .write
        .mode(SaveMode.Append)
        .partitionBy("day", "hour", "productid", "category")
        .format("parquet")
        .save("target/spark-warehouse/test_11.parquet")

      row

    }

    }

    println(v)

  }
  private def defineCategory(action: String): String = {
    var category = "event"
    if("$launch".equalsIgnoreCase(action)){
      category = "cd"
    }else if("$exitPage".equalsIgnoreCase(action)){
      category = "usinglog"
    }else if("$crash".equalsIgnoreCase(action) || "$error".equalsIgnoreCase(action)){
      category = "error"
    }else{
      category = "event"
    }
    return category
  }

  private def transformServerTime(serverTime: String): (String, String) = {

    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val date = formatter.parse(serverTime)
    val dayFormatter = new SimpleDateFormat("yyyyMMdd")
    val hourFormatter = new SimpleDateFormat("HH")
    val day = dayFormatter.format(date)
    val hour = hourFormatter.format(date)
    (day, hour)
  }



  def test111(): Unit ={

    val spark = SparkUtils.createSparkSession("test111")
    val df = spark.read.parquet("target/spark-warehouse/")
    df.createOrReplaceTempView("tmp_user")
    val users = spark.sql("select actionattach  from tmp_user")
    users.show()
    val frame = spark.sql("select * from tmp_user")
    frame.show()

  }






}
