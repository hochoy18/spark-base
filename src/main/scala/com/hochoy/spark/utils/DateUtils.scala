package com.hochoy.spark.utils

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date, GregorianCalendar}

import com.hochoy.spark.sql.datasources.SQLDataSourceTest1.{spark, warehouse_dir}
import com.hochoy.spark.utils.Constants.{FILE_PATH, USER_SPARK_PATH}
import org.apache.spark.sql.SaveMode
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.util.Try

/** Describe:
  * Created by IntelliJ IDEA.
  * Time: 11:26
  *
  * @author hochoy <hochoy18@sina.com> 
  * @Version V1.0.0
  */
object DateUtils {

  final val NOT_USER_DEFAULT = "-9999999"

  /**
    * 处理 日期间隔 < 0 的数据，<= 0 则使用默认值 "0"
    *
    * @param startDate
    * @param endDate
    * @param pattern
    * @param default
    * @param f
    * @return
    */
  def getDiffWithDefault(startDate: String,
                         endDate: String,
                         pattern: String = "yyyy-MM-dd",
                         default: String = NOT_USER_DEFAULT,
                         f: (String, String, String) ⇒ String): String = {
    val sub = f(startDate, endDate, pattern)
    try {
      val res = sub.toInt
      if (NOT_USER_DEFAULT == default || res >= 0) {
        sub
      } else {
        default
      }
    } catch {
      case e: NumberFormatException ⇒ null
    }
  }

  def getFirstDay(date: String, pattern: String = "yyyy-MM-dd", f: (String, String) ⇒ String): String = {
    val firstDay = f(date, pattern)
    firstDay
  }

  def date_first(date: String, pattern: String = "yyyy-MM-dd", dateType: String): String = {
    val diff = dateType match {
      case "week" ⇒ getFirstDay(date, pattern, DateBasicFunction.getFirstDayOfWeek)
      case "month" ⇒ getFirstDay(date, pattern, DateBasicFunction.getFirstDayOfMonth)
      case _ ⇒ null
    }
    diff
  }

  def main(args: Array[String]): Unit = {


    println(date_diff("20190611", "20190219", "yyyyMMdd", "month"))
    println(date_diff("20190611", "20190915", "yyyyMMdd", "month"))
    println(date_diff("20190611", "20190609", "yyyyMMdd", "month", "0"))
    println(date_diff("20190611", "20190601", "yyyyMMdd", "month", "0"))


    System.exit(-1)
    (20190601 to 20190630).foreach(x ⇒ println(s"week : ", date_first(x.toString, "yyyyMMdd", "week")))
    println("----------------------------")
    (20190601 to 20190630).foreach(x ⇒ println(s"month : ", date_first(x.toString, "yyyyMMdd", "month")))
    println("----------------------------")
    (20190701 to 20190731).foreach(x ⇒ println(s"month : ", date_first(x.toString, "yyyyMMdd", "month")))

  }

  /**
    * 获取两个日期间的 间隔的天/周/月数
    *
    * @param startDate
    * @param endDate
    * @param pattern
    * @param dateType
    * @return
    */
  def date_diff(startDate: String, endDate: String, pattern: String, dateType: String): String = {
    date_diff(startDate, endDate, pattern, dateType, NOT_USER_DEFAULT)
  }

  def date_diff(startDate: String, endDate: String, pattern: String, dateType: String, defaultValue: String): String = {
    val diff = dateType match {
      case "day" ⇒ getDiffWithDefault(startDate, endDate, pattern, defaultValue, DateBasicFunction.getDateDiff)
      case "week" ⇒ getDiffWithDefault(startDate, endDate, pattern, defaultValue, DateBasicFunction.getWeekDiff)
      case "month" ⇒ getDiffWithDefault(startDate, endDate, pattern, defaultValue, DateBasicFunction.getMonthDiff)
      case _ ⇒ null
    }
    diff
  }


}


object DateBasicFunction {
  val logger = LoggerFactory.getLogger(getClass)


  def dateOp(start: String, end: String, endDate: String, unit: String = "day"): List[Int] = {
    unit match {
      case "day" ⇒ {

        null
      }
      case "week" ⇒ {

        null
      }
      case "month" ⇒ {

        null
      }
      case _ ⇒ List.empty
    }

  }


  /**
    * 返回 指定的两个日期间隔的自然月份数
    *
    * @param startDate
    * @param endDate
    * @param pattern
    * @return
    */
  @throws(classOf[ParseException])
  def getMonthDiff(startDate: String, endDate: String, pattern: String = "yyyy-MM-dd"): String = {
    if (null == startDate || null == endDate) null
    else {
      val sdf = new SimpleDateFormat(pattern)
      val bef = Calendar.getInstance()
      val aft = Calendar.getInstance()
      bef.setTime(sdf.parse(startDate))
      aft.setTime(sdf.parse(endDate))
      val result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH)
      val month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12
      logger.debug(s"${startDate} and ${endDate}  between ${month + result} months")
      (month + result).toString
    }
  }

  /**
    * 获取指定日期 date 所在月的第一天
    *
    * @param date
    * @param pattern
    * @return 日期格式不正确时，返回null
    */
  def getFirstDayOfMonth(date: String, pattern: String = "yyyy-MM-dd"): String = {
    val format = new SimpleDateFormat(pattern)
    var firstDay: String = null;
    try {
      val date1 = format.parse(date)
      firstDay = getFirstDayOfMonth1(date1, pattern);
    } catch {
      case ex: ParseException ⇒ logger.error("ParseException: ", ex)
      case ex: Exception ⇒ logger.error("Exception: ", ex);
    }
    return firstDay;
  }

  /**
    * 获取指定日期 date 所在月的第一天
    *
    * @param date type is Date
    * @param pattern
    * @return
    */
  private def getFirstDayOfMonth1(date: Date, pattern: String = "yyyy-MM-dd"): String = {
    val df = new SimpleDateFormat(pattern);
    val calendar = Calendar.getInstance();

    calendar.setTime(date);
    val theDate = calendar.getTime();

    // 第一天
    val gcLast: GregorianCalendar = Calendar.getInstance().asInstanceOf[GregorianCalendar];
    gcLast.setTime(theDate);
    gcLast.set(Calendar.DAY_OF_MONTH, 1);
    val firstDay: String = df.format(gcLast.getTime());
    logger.debug(s"the first day of ${date} is ${firstDay}")
    firstDay
  }


  /**
    * 返回指定日期 所在周的周一日期
    *
    * @param time
    * @param pattern
    * @return
    */
  def getFirstDayOfWeek(time: String, pattern: String): String = {
    val sdf = new SimpleDateFormat(pattern);
    var result: String = null;
    try {
      val date = sdf.parse(time);
      result = getFirstDayOfWeek(date, pattern);
    } catch {
      case e: ParseException ⇒ e.printStackTrace()
    }
    result
  }

  private def getFirstDayOfWeek(time: Date, pattern: String): String = {

    val sdf = new SimpleDateFormat(pattern); //设置时间格式
    val cal = Calendar.getInstance();
    cal.setTime(time);
    val dayWeek = cal.get(Calendar.DAY_OF_WEEK); //获得当前日期是一个星期的第几天
    if (1 == dayWeek) {
      cal.add(Calendar.DAY_OF_MONTH, -1);
    }
    cal.setFirstDayOfWeek(Calendar.MONDAY);
    val day = cal.get(Calendar.DAY_OF_WEEK);
    cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
    val imptimeBegin = sdf.format(cal.getTime());
    return imptimeBegin;

  }


  /**
    * 获取两个日期间的自然周间隔数
    *
    * @param startDate
    * @param endDate
    * @param pattern
    * @return
    */
  def getWeekDiff(startDate: String, endDate: String, pattern: String = "yyyy-MM-dd"): String = {
    if (null == startDate || null == endDate) null else {
      val sdf = new SimpleDateFormat(pattern)
      try {
        val from = sdf.parse(startDate).getTime()
        val to = sdf.parse(endDate).getTime()

        val time = sdf.parse(startDate)
        val calendar = Calendar.getInstance()
        calendar.setTime(time)
        val weekInterval = getWeekInterval(startDate, calendar, sdf, pattern)
        val weekStartDate = weekInterval.split("~")(0)
        val weekEndDate = weekInterval.split("~")(1)
        //如果开始时间和结束时间在一周内
        if (getDayOfRange(weekStartDate, weekEndDate, pattern).contains(endDate)) "0" else
          Math.floor((to - from) * 1.0 / (1000 * 3600 * 24 * 7)).toInt.toString
      } catch {
        case e: ParseException ⇒
          logger.error("ParseException", e)
          null
      }
    }
  }

  def getWeekInterval(startDate: String, cal: Calendar, sdf: SimpleDateFormat, pattern: String): String = {
    if (getDayOfTheWeek(startDate, pattern) == 2) {
      val sunday = getDayDiff(startDate, 6, pattern);
      startDate + "~" + sunday;
    } else if (getDayOfTheWeek(startDate, pattern) == 1) {
      val sunday = getDayDiff(startDate, -6, pattern);
      startDate + "~" + sunday;
    } else {
      //startDate不是星期一
      getDayOfTheWeek(startDate, pattern);
      cal.setFirstDayOfWeek(Calendar.MONDAY);
      //设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
      val day = cal.get(Calendar.DAY_OF_WEEK); //获得当前日期是一个星期的第几天  
      cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
      //根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
      val monday = sdf.format(cal.getTime());
      val sunday = getDayDiff(monday, 6, pattern);
      monday + "~" + sunday;
    }
  }

  /**
    * 获取两个日期之间的所有日期
    *
    * @return
    */
  def getDayOfRange(startDate: String, endDate: String, pattern: String): List[String] = {
    val sdf_d = new SimpleDateFormat(pattern);
    val dateList = new ListBuffer[String];
    var start = new Date();
    var end = new Date();
    try {
      start = sdf_d.parse(startDate);
      end = sdf_d.parse(endDate);
    } catch {
      case e: ParseException ⇒ logger.error("ParseException", e);
    }
    val tempStart = Calendar.getInstance();
    tempStart.setTime(start);

    val tempEnd = Calendar.getInstance();
    tempEnd.setTime(end);


    while (tempStart.compareTo(tempEnd) <= 0) {
      dateList.+=(sdf_d.format(tempStart.getTime()));
      tempStart.add(Calendar.DAY_OF_YEAR, 1);
    }

    dateList.toList
  }

  /**
    * get day after or before dateDay by n days
    *
    * @param dateDay
    * @param n
    * @return
    */
  def getDayDiff(dateDay: String, n: Int, pattern: String): String = {
    val sdf_d = new SimpleDateFormat(pattern)
    var date = new Date()
    try {
      date = sdf_d.parse(dateDay)
    } catch {
      case e: ParseException ⇒ logger.error("ParseException", e);
    }
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.add(Calendar.DAY_OF_MONTH, n)
    sdf_d.format(cal.getTime())
  }

  /**
    * 获取当天是星期几
    *
    * @param day
    * @return
    */
  def getDayOfTheWeek(day: String, pattern: String): Int = {
    val sdf_d = new SimpleDateFormat(pattern);
    var theDay = new Date();
    try {
      theDay = sdf_d.parse(day);
    } catch {
      case e: ParseException ⇒ logger.error("PARSE_EXCEPTION", e);
    }
    val calendar = Calendar.getInstance();
    calendar.setTime(theDay);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /**
    * 获取两个日期的间隔天数
    */

  @throws(classOf[ParseException])
  def getDateDiff(startDate: String, endDate: String, pattern: String): String = {
    if (null == startDate || null == endDate) null else {
      val dateFormat: SimpleDateFormat = new SimpleDateFormat(pattern)
      val startCal: Calendar = Calendar.getInstance()
      val endCal: Calendar = Calendar.getInstance()

      val startDt: Date = dateFormat.parse(startDate)
      val endDt: Date = dateFormat.parse(endDate)

      startCal.setTime(startDt)
      val startTime = startCal.getTimeInMillis()
      endCal.setTime(endDt)
      val endTime = endCal.getTimeInMillis()
      val daysDiff = (endTime - startTime) / (1000 * 3600 * 24)
      daysDiff.toInt.toString
    }
  }


}
