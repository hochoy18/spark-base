package com.hochoy.cobub3_test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.HashSet;
import java.util.ArrayList;

public class DateUtil {

    private DateUtil() {
    }

    public static final String YYYY_MM_DD = "yyyyMMdd";
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static final String PARSE_EXCEPTION = "parse exception";

    public static Date str2Date(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        return d;
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        return sdf.format(date);
    }


    public static Long getGapDays(String s1, String s2) {
        Date d1 = str2Date(s1);
        Date d2 = str2Date(s2);
        Long days = -1L;
        if (d1 != null && d2 != null) {
            days = (Long) (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
        }
        return days;
    }

    public static String getDate(Date date, Integer days) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);

        return sdf.format(cal.getTime());
    }

    public static String getDayHour() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd-HH");
        Calendar cal = Calendar.getInstance();
        return sd.format(cal.getTime());
    }


    /**
     * 获取当前日期的本周的最后一天
     *
     * @param dateDay 当前日期
     * @return
     */
    public static String getLastDayOfWeek(String dateDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date;
        String firstDay = null;
        try {
            date = sdf.parse(dateDay);
            firstDay = getLastDayOfWeek(date, "yyyyMMdd");
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
//        Calendar cal = Calendar.getInstance();
//        cal.setFirstDayOfWeek(Calendar.MONDAY);
//        cal.setTime(date);
//        cal.set(Calendar.DAY_OF_WEEK, 1);
//        cal.add(Calendar.DAY_OF_WEEK, 6);
        return firstDay;//dateFormat(cal.getTime());
    }

    public static String getLastDayOfWeek(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // 如果是周日直接返回
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            return df.format(date);
        }
        System.out.println(c.get(Calendar.DAY_OF_WEEK));
        c.add(Calendar.DATE, 7 - c.get(Calendar.DAY_OF_WEEK) + 1);
        return df.format(c.getTime());
    }


    /**
     * 获取周日到dateDay之间的所有日期(包含周日和dateDay)
     *
     * @param dateDay
     * @return
     */
    public static List<String> getDayOfWeekList(String dateDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(1);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        List<String> dateList = new ArrayList<>();
        for (; ; ) {
            String dateStr = sdf.format(cal.getTime());
            if (Integer.parseInt(dateStr) > Integer.parseInt(dateDay)) {
                break;
            }
            dateList.add(dateStr);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dateList;
    }

    /**
     * 获取本月一号到dateDay之间的所有日期(包含一号和dateDay)
     *
     * @param dateDay
     * @return
     */
    public static List<String> getDayOfMonthList(String dateDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//    cal.add(Calendar.MONTH_OF_YEAR, -1);// 上一月
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        List<String> dateList = new ArrayList<>();
        for (; ; ) {
            String dateStr = sdf.format(cal.getTime());
            if (Integer.parseInt(dateStr) > Integer.parseInt(dateDay)) {
                break;
            }
            dateList.add(dateStr);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dateList;
    }

    /**
     * 获取当前日期的本月最后一天日期
     *
     * @param dateDay 当前日期
     * @return 当前日期的本月的最后一天
     */
    public static String getLastDayOfMonth(String dateDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当前日期的前一天日期
     *
     * @param dateDay 当前日期
     * @return 当前日期的前一天
     */
    public static String getDayBefore(String dateDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf.format(cal.getTime());
    }

    /**
     * get day after or before dateDay by n days
     *
     * @param dateDay
     * @param n
     * @return
     */
    public static String getDayDiff(String dateDay, int n) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, n);
        return sdf.format(cal.getTime());
    }


    /**
     * 获取两个日期之间的所有日期
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> getDayOfRange(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        List<String> dateList = new ArrayList<>();
        Date start = new Date();
        Date end = new Date();
        try {
            start = sdf.parse(startDate);
            end = sdf.parse(endDate);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);


        while (tempStart.compareTo(tempEnd) <= 0) {
            dateList.add(sdf.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateList;
    }

    /**
     * @param date
     * @param pattern
     * @return 返回 date 日期所在月份的 一号 日期
     */
    public static String getFirstDayOfMonth(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String firstDay = null;
        try {
            Date date1 = format.parse(date);
            firstDay = getFirstDayOfMonth(date1, pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return firstDay;
    }

    public static String getFirstDayOfMonth(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        Date theDate = calendar.getTime();

        // 第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        return day_first;
    }

    /**
     * @param currentDate
     * @param pattern
     * @return 返回 date 日期所在周的 < 周一 > 的 日期
     */
    public static String getFirstDayOfCurrentWeek(String currentDate, String pattern) {
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
            e.printStackTrace();
        }
        return returnDate;
    }


    /**
     * 输入的是String，格式诸如20120102，实现减num天的功能，返回的格式为String，诸如20120101
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

//    /**
//     * @param cur       输入日期
//     * @param period    周期：周，月，年，天，小时，分。。。
//     * @param peroidNum 增加/减少的周期数 ，支持正/负
//     * @return
//     */
//    public Date dateAddAssignPeriod(Date cur, int period, int peroidNum) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(cur);
//        switch (period) {
//            case Calendar.MONTH:
//                c.add(Calendar.MONTH, peroidNum);
//                break;
//            case Calendar.DAY:
//                c.add(Calendar.Day, peroidNum);
//                break;
//            case Calendar.WEEK_OF_YEAR:
//                c.add(Calendar.WEEK_OF_MONTH, peroidNum);
//                break;
//        }
//        c.add(Calendar.MINUTE, 1);
//
//        Date date = c.getTime();
//        return date;
//    }


    public static String dateAddMonth(String date, int addNum) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String resDay = null;
        try {
            Date now = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, addNum);
            resDay = sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resDay;
    }

    /**
     * beginTime 在 endTime 之前，返回true， 否则返回false
     *
     * @param beginTime
     * @param endTime
     * @param pattern
     * @return when beginTime <  endTime  return true, else return false
     */
    public static int towDateCompare(String beginTime, String endTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date bt = sdf.parse(beginTime);
            Date et = sdf.parse(endTime);
            return bt.compareTo(et);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -999;
    }

    public static int towDateCompare(String beginTime, String endTime) {
        return towDateCompare(beginTime, endTime, "yyyyMMdd");
    }

    /**
     * otherDate 跟今天日期对比，在今天之前则返回true，否则返回false
     *
     * @param otherDate
     * @param pattern
     * @return when otherDate < today return true else false
     */
    public static int dateCompare2Now(String otherDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return towDateCompare(otherDate, sdf.format(new Date()), pattern);
    }

    /**
     * @param otherDate
     * @return otherDate 跟今天日期对比，
     * otherDate < now  -1,
     * otherDate == now 0 ,
     * otherDate > now 1
     */
    public static int dateCompare2Now(String otherDate) {
        return dateCompare2Now(otherDate, "yyyyMMdd");
    }


    public static void main(String[] args) {
        System.out.println(getGapDays("20190801","20190802"));
        System.out.println(getGapDays("20190801","20190805"));
        System.out.println(getGapDays("20190805","20190802"));
        System.out.println(getGapDays("20190801","20190801"));
        System.out.println(getGapDays("20190722","20190623"));

        //otherDate 跟今天日期对比，在今天之前则返回true，否则返回false
//        if (DateUtil.dateCompare2Now("20190731")) {
//
//        }

    }

}
