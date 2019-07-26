//package com.hochoy.utils;
//
////import org.apache.hadoop.hbase.client.Get;
////import org.apache.hadoop.hbase.client.HTable;
////import org.apache.hadoop.hbase.client.Result;
////import org.apache.hadoop.hbase.util.Bytes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//
//public class StringHandle {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(StringHandle.class);
//
//	private StringHandle() {
//	}
//
//	/*
//    	输入的是String，格式诸如20120102，实现加一天的功能，返回的格式为String，诸如20120103
//    */
//	public static String stringDatePlus(String row) throws ParseException{
//		String year=row.substring(0, 4);
//		String month=row.substring(4,6);
//		String day=row.substring(6);
//		String date1=year+"-"+month+"-"+day;
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		Date startDate=sdf.parse(date1);
//		Calendar cd = Calendar.getInstance();
//		cd.setTime(startDate);
//		cd.add(Calendar.DATE, 1);
//		String dateStr =sdf.format(cd.getTime());
//		String year1=dateStr.substring(0,4);
//		String month1=dateStr.substring(5,7);
//		String day1=dateStr.substring(8);
//		return year1+month1+day1;
//	}
//
//	/*
//	 输入的是String，格式诸如20120102，实现减一天的功能，返回的格式为String，诸如20120101
//	 */
//	public static String stringDateDecrease(String row) throws ParseException{
//		String year=row.substring(0, 4);
//		String month=row.substring(4,6);
//		String day=row.substring(6);
//		String date1=year+"-"+month+"-"+day;
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//		Date startDate=sdf.parse(date1);
//		Calendar cd = Calendar.getInstance();
//		cd.setTime(startDate);
//		cd.add(Calendar.DATE, -1);
//		String dateStr =sdf.format(cd.getTime());
//		String year1=dateStr.substring(0,4);
//		String month1=dateStr.substring(5,7);
//		String day1=dateStr.substring(8);
//		return year1+month1+day1;
//	}
//	/*
//	 输入的是String，格式诸如20120102，实现减num天的功能，返回的格式为String，诸如20120101
//	 */
////	public static String stringDateDecrease(String row,int num) throws ParseException{
////		String year=row.substring(0, 4);
////		String month=row.substring(4,6);
////		String day=row.substring(6);
////		String date1=year+"-"+month+"-"+day;
////		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
////		Date startDate=sdf.parse(date1);
////		Calendar cd = Calendar.getInstance();
////		cd.setTime(startDate);
////		cd.add(Calendar.DATE, -num);
////		String dateStr =sdf.format(cd.getTime());
////		String year1=dateStr.substring(0,4);
////		String month1=dateStr.substring(5,7);
////		String day1=dateStr.substring(8);
////		return year1+month1+day1;
////	}
//
//	/*
//	 输入的格式为String，诸如20120101，返回的格式为String，诸如2012-01-01
//	 */
//	public static String stringDateChange(String date)
//	{
//		if(date.length()=="20120101".length()){
//			String year=date.substring(0, 4);
//			String month=date.substring(4,6);
//			String day=date.substring(6);
//			return year+"-"+month+"-"+day;
//		}else{
//			return date;
//		}
//	}
//	/**
//	 * 日期向后推一天
//	 * @param date 格式：20120101
//	 * @return  20120102
//	 */
//	public static String tonextday(String date){
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day+1);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		String da = format.format(newdate);
//		return da;
//	}
//
//	/**
//	 * 获取当前日期上一周的开始日期 （周日）
//	 */
//	public static String previousWeekByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    if(1 == dayWeek) {
//	    	cal.add(Calendar.DAY_OF_MONTH, -1);
//	    }
//	    cal.setFirstDayOfWeek(Calendar.SUNDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
//	    int s = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-s);//根据日历的规则，给当前日期减往星期几与一个星期第一天的差值
//	    cal.add(Calendar.WEEK_OF_YEAR, -1);
//	    String imptimeBegin = sdf.format(cal.getTime());
//	    return imptimeBegin;
//	}
//
//
//	/**
//	 * 获取当前日期上一周的结束日期 （周六）
//	 */
//	public static String previousWeekEndDayByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    if(1 == dayWeek) {
//	    	cal.add(Calendar.DAY_OF_MONTH, -1);
//	    }
//	    cal.setFirstDayOfWeek(Calendar.SUNDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
//	    int s = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()+(6-s));
//	    cal.add(Calendar.WEEK_OF_YEAR, -1);
//	    String imptimeBegin = sdf.format(cal.getTime());
//	    return imptimeBegin;
//	}
//
//	/**
//	 * 获取当前日期下一周的结束日期 （周六）
//	 */
//	public static String nextWeekEndDayByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    if(1 == dayWeek) {
//	    	cal.add(Calendar.DAY_OF_WEEK,1);
//	    }
//	    if(7!=dayWeek){//如果不是周六
//		    cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
//		    int s = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//		    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-s+5);
//	    }else if(7==dayWeek){
//	    	cal.add(Calendar.DATE, 7);
//	    }
//	    String  imptimeBegin = sdf.format(cal.getTime());
//	    return imptimeBegin;
//	}
//
//	/**
//	 * 获取当前日期当前一周的开始日期 （周日）
//	 */
//	public static String getCurrentWeekFirstDayByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    if(1 == dayWeek) {
//	    	cal.add(Calendar.DAY_OF_MONTH, -1);
//	    }
//	    cal.setFirstDayOfWeek(Calendar.SUNDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
//	    int s = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-s);//根据日历的规则，给当前日期减往星期几与一个星期第一天的差值
//
//	    String imptimeBegin = sdf.format(cal.getTime());
//	    return imptimeBegin;
//	}
//	/**
//	 * 获取当前日期当前一周的结束日期 （周六）
//	 */
//	public static String getCurrentWeekEndDayByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    if(1 == dayWeek) {
//	    	cal.add(Calendar.DAY_OF_MONTH, -1);
//	    }
//	    cal.setFirstDayOfWeek(Calendar.SUNDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
//	    int s = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
//	    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()+(6-s));
//
//	    String imptimeBegin = sdf.format(cal.getTime());
//	    return imptimeBegin;
//	}
//	/**
//	 * 返回上一个月的第一天
//	 * @param date
//	 * @return  20120201
//	 * */
//	public static String previousMonthByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-2, 1);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    String imptimeBegin = sdf.format(cal.getTime());
//		return imptimeBegin;
//	}
//
//	/**
//	 * 返回下一个月的第一天
//	 * @param date
//	 * @return  20120401
//	 */
//	public static String nextMonthByDate(String date) {
//		// TODO Auto-generated method stub
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month, 1);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    String imptimeBegin = sdf.format(cal.getTime());
//		return imptimeBegin;
//	}
//	/**
//	 * 返回当前月的第一天
//	 * @param date
//	 * @return 20120101
//	 */
//	public static String getCurrentMonthFirstDayByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, 1);
//		Date newdate = calendar.getTime();
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(newdate);
//	    String imptimeBegin = sdf.format(cal.getTime());
//		return imptimeBegin;
//	}
//
//	public static String getCurrenWeekByDate(String date) {
//
//        int year = Integer.parseInt(date.substring(0,4));
//        int month = Integer.parseInt(date.substring(4,6));
//        int day = Integer.parseInt(date.substring(6));
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month-1, day);
//        Date newdate = calendar.getTime();
//
//        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
//        calendar.setTime(newdate);
//
//        return String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
//    }
//
//	public static String getWeekofMonthByDate(String date) {
//		int year = Integer.parseInt(date.substring(0,4));
//		int month = Integer.parseInt(date.substring(4,6));
//		int day = Integer.parseInt(date.substring(6,8));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(year, month-1, day);
//		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
//		return String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));
//	}
//
//	public static String getCurrentWeekDates(int year,int weekindex)
//	{
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.YEAR, year);
//
//		calendar.set(Calendar.WEEK_OF_YEAR, weekindex);
//
//		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//
//		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(calendar.getTime());
//
//	    String Begin = sdf.format(cal.getTime());
//
//		calendar.roll(Calendar.DAY_OF_WEEK, 6);
//
//		String end = sdf.format(calendar.getTime());
//
//		return Begin+"-"+end;
//	}
//	/**
//	 * 根据任务类型转换时间格式
//	 * @param date 2014-11-17 12:12:00
//	 * @param datatype
//	 * @return
//	 */
//	public static String getFormatData(String date,String datatype){
//		String newdate = date.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
//		if("2".equals(datatype)){//小时 2014111209
//			return newdate.substring(0, 10);
//		}
//		if("3".equals(datatype)){//天20141112
//			return newdate.substring(0, 8);
//		}
//		if("4".equals(datatype)){//周 20141101:11月第一周
//			String index =getWeekofMonthByDate(newdate);
//			String yyyymm = newdate.substring(0,6);
//			return yyyymm+index;
//
//		}
//		if("5".equals(datatype)){//月201411
//			return newdate.substring(0, 6);
//		}
//		return "";//once return ""
//	}
//
//	/**
//	 * 获取前一天的result
//	 * @param stopdate
//	 * @param productid
//	 * @return
//	 */
////	public static Result getYesterdayACCNP(String stopdate, String productid, HTable table){
////		Result re = null;
////		try {
////			String yesterday = StringHandle.stringDateDecrease(stopdate);
////			Get get = new Get(Bytes.toBytes(productid + "_" + yesterday));
////			re = table.get(get);
////		} catch (ParseException e) {
////			LOGGER.info("ParseException",e);
////		} catch (IOException e) {
////			LOGGER.info("IOException",e);
////		}
////		return re;
////	}
//}
