package com.hochoy.java8;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/5/29
 */
public class MonthDiff {
    public static void main(String[] args) throws ParseException {
//        between();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date1 = format.parse("20190525");
        Date date2 = format.parse("20190527");
        System.out.println("--------" + getFirst("20190520","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190521","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190522","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190523","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190524","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190525","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190526","yyyyMMdd"));
        System.out.println("--------" + getFirst("20190527","yyyyMMdd"));
        convertWeekByDate(date2,"yyyyMMdd");
        System.exit(-1);







        String str1 = "2019-04-27";
        String str2 = "2019-05-29";
        String str22 = "2019-05-19";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));

        str22 = "2019-05-20";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        str22 = "2019-05-21";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
         str22 = "2019-05-22";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        str22 = "2019-05-23";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        str22 = "2019-05-24";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        str22 = "2019-05-25";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        str22 = "2019-05-26";
        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));

//        int diff = getMonthDiff(str1, str2,"yyyy-MM-dd");


//        System.out.println("sssssssss "+getFirstDayOfCurrentWeek(str1,"yyyy-MM-dd"));
//        System.out.println("xxxxxxxxx  "+getFirstDayOfCurrentWeek(str2,"yyyy-MM-dd"));
//        System.out.println("xxxxxxxxx  "+getFirstDayOfCurrentWeek(str22,"yyyy-MM-dd"));
        System.exit(-1);

        String weekDiff = getWeekDiff(str1, str2,"yyyy-MM-dd");
        System.out.println("weekDiff   "+weekDiff);




        String firstDay = getFirstDayOfMonth("20190525", "yyyyMMdd");
        System.out.println("firstDay ....  "+ firstDay);
//        System.out.println("month diff   "+diff);



    }

    private static String getFirst(String time, String pattern) {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        String result = null;
        try {
            Date date = sdf.parse(time);
            result =convertWeekByDate(date,pattern);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Get the Monday of the week in which the specified date is located
     * @param time
     * @param pattern
     * @return
     */
    private static String convertWeekByDate(Date time,String pattern) {

        SimpleDateFormat sdf=new SimpleDateFormat(pattern); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        String imptimeBegin = sdf.format(cal.getTime());
        System.out.println("所在周星期一的日期："+imptimeBegin);
        cal.add(Calendar.DATE, 2);
        String imptimeMi = sdf.format(cal.getTime());
        System.out.println("所在周星期三的日期："+imptimeMi);
        cal.add(Calendar.DATE, 4);
        String imptimeEnd = sdf.format(cal.getTime());
        System.out.println("所在周星期五的日期："+imptimeEnd);
        return imptimeBegin;

    }




    public static String getFirstDayOfCurrentWeek(String currentDate,String pattern){
        String returnDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(currentDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int weekIndex = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DAY_OF_YEAR,-weekIndex+2);
            date = c.getTime();
            returnDate = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }










    static String getFirstDayOfMonth(String date, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String firstDay = null;
        try {
            Date date1 = format.parse(date);
            firstDay = getFirstDayOfMonth(date1,pattern);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return firstDay;
    }

    static String getFirstDayOfMonth(Date date, String pattern){
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
     * 某一个月第一天和最后一天
     */
    public static Map<String, Object> getFirstLastDayByMonth(Date date, String pattern, boolean isNeedHms) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        Date theDate = calendar.getTime();

        // 第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        if (isNeedHms)
            str.append(" 00:00:00");
        day_first = str.toString();

        // 最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        String day_last = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(day_last);
        if (isNeedHms)
            endStr.append(" 23:59:59");
        day_last = endStr.toString();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("first", day_first);
        map.put("last", day_last);
        return map;
    }



    /**
     * 获取当前季度
     *
     */
    public static String getQuarter() {
        Calendar c = Calendar.getInstance();
        int month = c.get(c.MONTH) + 1;
        int quarter = 0;
        if (month >= 1 && month <= 3) {
            quarter = 1;
        } else if (month >= 4 && month <= 6) {
            quarter = 2;
        } else if (month >= 7 && month <= 9) {
            quarter = 3;
        } else {
            quarter = 4;
        }
        return quarter + "";
    }

    /**
     * 获取某季度的第一天和最后一天
     */
//    public static String[] getCurrQuarter(int num) {
//        String[] s = new String[2];
//        String str = "";
//        // 设置本年的季
//        Calendar quarterCalendar = null;
//        switch (num) {
//            case 1: // 本年到现在经过了一个季度，在加上前4个季度
//                quarterCalendar = Calendar.getInstance();
//                quarterCalendar.set(Calendar.MONTH, 3);
//                quarterCalendar.set(Calendar.DATE, 1);
//                quarterCalendar.add(Calendar.DATE, -1);
//                str = DateUtils.formatDate(quarterCalendar.getTime(), "yyyy-MM-dd");
//                s[0] = str.substring(0, str.length() - 5) + "01-01";
//                s[1] = str;
//                break;
//            case 2: // 本年到现在经过了二个季度，在加上前三个季度
//                quarterCalendar = Calendar.getInstance();
//                quarterCalendar.set(Calendar.MONTH, 6);
//                quarterCalendar.set(Calendar.DATE, 1);
//                quarterCalendar.add(Calendar.DATE, -1);
//                str = DateUtils.formatDate(quarterCalendar.getTime(), "yyyy-MM-dd");
//                s[0] = str.substring(0, str.length() - 5) + "04-01";
//                s[1] = str;
//                break;
//            case 3:// 本年到现在经过了三个季度，在加上前二个季度
//                quarterCalendar = Calendar.getInstance();
//                quarterCalendar.set(Calendar.MONTH, 9);
//                quarterCalendar.set(Calendar.DATE, 1);
//                quarterCalendar.add(Calendar.DATE, -1);
//                str = DateUtils.formatDate(quarterCalendar.getTime(), "yyyy-MM-dd");
//                s[0] = str.substring(0, str.length() - 5) + "07-01";
//                s[1] = str;
//                break;
//            case 4:// 本年到现在经过了四个季度，在加上前一个季度
//                quarterCalendar = Calendar.getInstance();
//                str = DateUtils.formatDate(quarterCalendar.getTime(), "yyyy-MM-dd");
//                s[0] = str.substring(0, str.length() - 5) + "10-01";
//                s[1] = str.substring(0, str.length() - 5) + "12-31";
//                break;
//        }
//        return s;
//    }

    /**
     * 用途：以指定的格式格式化日期字符串
     * @return String 已格式化的日期字符串
     * @throws NullPointerException 如果参数为空
     */
//    public static String formatDate(Date currentDate, String pattern){
//        if(currentDate == null || "".equals(pattern) || pattern == null){
//            return null;
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//        return sdf.format(currentDate);
//    }



    public static int getMonthDiff(String startDate, String endDate,String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        try {
            bef.setTime(sdf.parse(startDate));
            aft.setTime(sdf.parse(endDate));
        }catch (ParseException e){

        }
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        System.out.println(Math.abs(month + result));
        return month + result;
    }

    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值        
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数       
        if (month1 < month2 || month1 == month2 && day1 < day2)
            yearInterval--;
        // 获取月数差值       
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2)
            monthInterval--;
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    public static String getWeekDiff(String startDate,String endDate,String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            long from = sdf.parse(startDate).getTime();
            long to = sdf.parse(endDate).getTime();

            Date time = sdf.parse(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            String weekInterval = getWeekInterval(startDate, calendar, sdf);
            String weekStartDate = weekInterval.split("~")[0];
            String weekEndDate = weekInterval.split("~")[1];
            //如果开始时间和结束时间在一周内
            if (DateUtil.getDayOfRange(weekStartDate, weekEndDate).contains(endDate)) {
                System.out.println("相差周数为：0");
                return "0";
            }
            String weekDiff = (int) Math.ceil((to - from) * 1.0 / (1000 * 3600 * 24 * 7)) + "";
            System.out.println("相差周数为：" + weekDiff);
            return weekDiff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String getWeekInterval(String startDate,Calendar cal,SimpleDateFormat sdf) {

        if (DateUtil.getDayOfTheWeek(startDate) == 2) {
            String sunday = DateUtil.getDayDiff(startDate, 6);
            return startDate + "~" + sunday;

        } else if (DateUtil.getDayOfTheWeek(startDate) == 1) {
            String sunday = DateUtil.getDayDiff(startDate, -6);
            return startDate + "~" + sunday;

        } else {//startDate不是星期一
            DateUtil.getDayOfTheWeek(startDate);
            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
            String monday = sdf.format(cal.getTime());
            String sunday = DateUtil.getDayDiff(monday, 6);
            return monday + "~" + sunday;
        }
    }


}
