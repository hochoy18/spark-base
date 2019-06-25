package com.hochoy.cobub3_test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    private DateUtil(){}

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


    public static int getGapDays(String s1, String s2) {
        Date d1 = str2Date(s1);
        Date d2 = str2Date(s2);
        int days = -1;
        if (d1 != null && d2 != null) {
            days = (int) (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
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
        Date date = new Date();
        try {
            date = sdf.parse(dateDay);
        } catch (ParseException e) {
            logger.error(PARSE_EXCEPTION, e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return dateFormat(cal.getTime());
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
}
