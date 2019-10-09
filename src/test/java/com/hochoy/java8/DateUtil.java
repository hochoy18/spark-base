package com.hochoy.java8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static final SimpleDateFormat sdf_d = new SimpleDateFormat("yyyy-MM-dd");
    private static final String PARSE_EXCEPTION = "parse exception";

    public static synchronized Date str2Date(String date) {
        Date d = null;
        try {
            d =  sdf_d.parse(date);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        return d;
    }

    public static synchronized String dateFormat(Date date){
        return sdf_d.format(date);
    }

    public static String dateFormatHms(Date date){
        SimpleDateFormat sdfHms = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return sdfHms.format(date);
    }

    public static int getGapDays(String s1, String s2) {
        Date d1 = str2Date(s1);
        Date d2 = str2Date(s2);
        int days = -1;
        if(d1 != null && d2 != null) {
            days =(int) ((d1.getTime() - d2.getTime()) / (1000 * 3600 * 24));
        }
        return days;
    }

    public static boolean compareDate(String s1, String s2, Integer count) {
        boolean flag = false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(str2Date(s1));
        cal2.setTime(str2Date(s2));
        cal2.add(Calendar.DAY_OF_MONTH, count);
        if (cal1.compareTo(cal2) > 0) {
            flag = true;
        }
        return flag;
    }



    public static String getDate(Date date, Integer days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);

        return sdf_d.format(cal.getTime());
    }

    public static String getDayHour() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd-HH");
        Calendar cal = Calendar.getInstance();
        return sd.format(cal.getTime());
    }


    /**
     * 获取当前日期的本周的最后一天
     * @param dateDay 当前日期
     * @return
     */
    public static String getLastDayOfWeek(String dateDay) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK,1);
        cal.add(Calendar.DAY_OF_WEEK,6);
//    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return dateFormat(cal.getTime());
    }

    /**
     * 获取周日到dateDay之间的所有日期(包含周日和dateDay)
     * @param dateDay
     * @return
     */
    public static List<String> getDayOfWeekList(String dateDay) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(1);
        cal.set(Calendar.DAY_OF_WEEK,cal.getFirstDayOfWeek());
        List<String> dateList = new ArrayList<>();
        for(;;){
            String dateStr = sdf_d.format(cal.getTime());
            if(Integer.parseInt(dateStr)>Integer.parseInt(dateDay)){
                break;
            }
            dateList.add(dateStr);
            cal.add(Calendar.DAY_OF_YEAR,1);
        }
        return dateList;
    }



    /**
     * 获取本月一号到dateDay之间的所有日期(包含一号和dateDay)
     * @param dateDay
     * @return
     */
    public static List<String> getDayOfMonthList(String dateDay) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//    cal.add(Calendar.MONTH_OF_YEAR, -1);// 上一月
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        List<String> dateList = new ArrayList<>();
        for(;;){
            String dateStr = sdf_d.format(cal.getTime());
            if(Integer.parseInt(dateStr)>Integer.parseInt(dateDay)){
                break;
            }
            dateList.add(dateStr);
            cal.add(Calendar.DAY_OF_YEAR,1);
        }
        return dateList;
    }

    /**
     * 获取当前日期的前n个月的[开始日期，结束日期]
     * @param dateDay
     * @return
     */
    public static List<String> getLastMonth(String dateDay, int n) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);// 上一月
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        List<String> dateList = new ArrayList<>();
        String dateStr = sdf_d.format(cal.getTime());
        dateList.add(dateStr);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        dateStr = sdf_d.format(cal.getTime());
        dateList.add(dateStr);

        return dateList;
    }

    /**
     * 获取当前日期的本月最后一天日期
     * @param dateDay  当前日期
     * @return  当前日期的本月的最后一天
     */
    public static String getLastDayOfMonth(String dateDay) {
        Date date = null;
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sdf_d.format(cal.getTime());
    }

    /**
     * 获取当前日期的本月的第一天
     * @return
     */
    public static String getFirstDayOfMonth(String dateDay) {
        Date date = null;
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return sdf_d.format(cal.getTime());
    }

    /**
     * 获取当前日期的前一天日期
     * @param dateDay  当前日期
     * @return  当前日期的前一天
     */
    public static String getDayBefore(String dateDay) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf_d.format(cal.getTime());
    }

    /**
     * get day after or before dateDay by n days
     * @param dateDay
     * @param n
     * @return
     */
    public static String getDayDiff(String dateDay,int n) {
        Date date = new Date();
        try {
            date = sdf_d.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, n);
        return sdf_d.format(cal.getTime());
    }


    /**
     * 获取两个日期之间的所有日期
     * @param dateDay
     * @return
     */
    public static List<String> getDayOfRange(String startDate,String endDate) {
        List<String> dateList = new ArrayList<>();
        Date start = new Date();
        Date end = new Date();
        try {
            start = sdf_d.parse(startDate);
            end = sdf_d.parse(endDate);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION,e);
        }
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
//    tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);


        while (tempStart.compareTo(tempEnd) <= 0) {
            dateList.add(sdf_d.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateList;
    }

    /**
     * 获取当天是星期几
     * @param day
     * @return
     */
    public static int getDayOfTheWeek(String day) {
        Date theDay = new Date();
        try {
            theDay = sdf_d.parse(day);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(theDay);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当前天的下个星期六
     * @param day
     * @return
     */
    public static String getNextSatTheWeek(String day) {
        Date theDay = new Date();
        Calendar calendar = Calendar.getInstance();
        try {
            theDay = sdf_d.parse(day);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        calendar.setTime(theDay);
        calendar.add(Calendar.DATE, 7 - calendar.get(Calendar.DAY_OF_WEEK));
        return sdf_d.format(calendar.getTime());
    }

    /**
     * 获取该天所在月的最后一天
     * @param day
     * @return
     */
    public static String getLastDayTheMonth(String day) {
        Calendar calendar = Calendar.getInstance();
        Date theDay = new Date();
        try {
            theDay = sdf_d.parse(day);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        calendar.setTime(theDay);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        return sdf_d.format(calendar.getTime());
    }

    public static String getSundayOfDay(String date ,String pattern) throws Exception{
        SimpleDateFormat sdf=new SimpleDateFormat(pattern); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time=sdf.parse(date);
        cal.setTime(time);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        cal.add(Calendar.DATE, 6);
        return  sdf.format(cal.getTime());
    }

//    public static void main(String[] args) throws  Exception{
//
//        System.out.println(getSundayOfDay("2019-08-19", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-20", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-21", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-22", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-23", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-24", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-25", "yyyy-MM-dd"));
//        System.out.println(getSundayOfDay("2019-08-26", "yyyy-MM-dd"));
////    System.out.println(getFirstDayOfMonth("20180829"));
////    System.out.println(getDayDiff("20181208",-6));
////    System.out.println(getDayOfRange("20180811","20180811"));
////    System.out.println(getDate(new Date(),-1));
////    System.out.println(getDayHour());
////    System.out.println(getDayOfMonthList("20181231"));
////    System.out.println(getLastMonth("20190131",-1));
////    System.out.println(getGapDays("20190101","20181001"));
//        //System.out.println(getDayOfTheWeek("20190518"));
//        System.out.println(getLastDayOfWeek("20190821"));
//        //System.out.println(getLastDayTheMonth("20190402"));
//    }



    public static String getLastDayOfWeek(String date , String pattern, int startFlag) {
        if (startFlag < 1 || startFlag > 7)
            throw  new RuntimeException("startFlag is between 1 and 7 ,and startFlag is :" + startFlag);
        SimpleDateFormat sdf=new SimpleDateFormat(pattern); //设置时间格式
        Calendar cal = Calendar.getInstance();
        try{
            Date time =sdf.parse(date);
            cal.setTime(time);
        }catch (ParseException e ){
            logger.error("ParseException caused by ",e);
        }

        if(1 ==  cal.get(Calendar.DAY_OF_WEEK)) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(startFlag);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-cal.get(Calendar.DAY_OF_WEEK));//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        cal.add(Calendar.DATE, 6);
        return  sdf.format(cal.getTime());
    }


    public static String getFirstDayOfWeek(String currentDate, String pattern,  int startFlag) {
        if (startFlag < 1 || startFlag > 7)
            throw  new RuntimeException("startFlag is between 1 and 7 ,and startFlag is :" + startFlag);
        String returnDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(currentDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int weekIndex = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DAY_OF_YEAR, -weekIndex + 2);
            date = c.getTime();
            returnDate = sdf.format(date);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        return returnDate;
    }

    /**
     * 输入的是String，格式诸如20120102，实现加(num > 0)/减(num < 0)num天的功能，返回的格式为String，诸如20120101
     *
     * @param date
     * @param num
     * @return
     * @throws ParseException
     */
    public static String stringDateDecrease(String date, int num) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        String date1 = year + "-" + month + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        try {
            startDate = sdf.parse(date1);
        } catch (ParseException e) {
            logger.error("ParseException",e);
        }
        Calendar cd = Calendar.getInstance();
        cd.setTime(startDate);
        cd.add(Calendar.DATE, num);
        String dateStr = sdf.format(cd.getTime());
        String year1 = dateStr.substring(0, 4);
        String month1 = dateStr.substring(5, 7);
        String day1 = dateStr.substring(8);
        return year1 + month1 + day1;
    }


    public static void main(String[] args) {
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",1));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",2));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",3));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",4));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",5));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",6));
        System.out.println(getLastDayOfWeek("20190916","yyyyMMdd",7));
        System.out.println(getLastDayOfWeek("20190909"));
    }
}
