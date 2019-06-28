package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 */
public class LocalDateTimeUtil {

    private LocalDateTimeUtil() {
    }

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 日期加一天
     *
     * @param date 日期
     * @return 日期
     */
    public static String getDateTimePlus(String date) {
        return LocalDate.parse(date, dateTimeFormatter).plusDays(1).format(dateTimeFormatter);
    }

    /**
     * 日期减一天
     *
     * @param date 日期
     * @param day  几天
     * @return 日期
     */
    public static String getDateTimeMinus(String date, long day) {
        return LocalDate.parse(date, dateTimeFormatter).minusDays(day).format(dateTimeFormatter);
    }

    /**
     * 如果开始日期小于结束日期减12周的日期，则返回开始日期，否则返回结束日期减12周的日期
     *
     * @return 日期
     */
    public static String getWeekDateTimeMinus(String fromDate, String toDate) {
        String minusTwelveWeekDate = LocalDate.parse(toDate, dateTimeFormatter).with(DayOfWeek.SATURDAY).minusWeeks(12).format(dateTimeFormatter);
        if (Integer.parseInt(fromDate) < Integer.parseInt(minusTwelveWeekDate)){
            return fromDate;
        }
        return minusTwelveWeekDate;
    }

    /**
     * 获取日期的上周六
     *
     * @return 日期
     */
    public static String getSaturdayDateTime(String date) {
        String saturday = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY).format(dateTimeFormatter);
        if (Integer.parseInt(saturday) >= Integer.parseInt(date)) {
            saturday = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY).minusWeeks(1).format(dateTimeFormatter);
        }
        return saturday;
    }
    /**
     * 获取日期的本周周六
     *
     * @return 日期
     */
    public static String getSaturdayNewDateTime(String date) {
        String saturday = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY).format(dateTimeFormatter);
        if (Integer.parseInt(saturday) >= Integer.parseInt(date)) {
            saturday = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY).minusWeeks(0).format(dateTimeFormatter);
        }else{
            saturday = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY).minusWeeks(-1).format(dateTimeFormatter);
        }
        return saturday;
    }

    //获取当前周六
    public static String getNowDateFriday() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String saturday = LocalDate.parse(df.format(new Date()), dateTimeFormatter).with(DayOfWeek.SATURDAY).format(dateTimeFormatter);
        return saturday;
    }

    //获取当前周日
    public static String getNowDateSaturday() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String saturday = LocalDate.parse(df.format(new Date()), dateTimeFormatter).with(DayOfWeek.SUNDAY).minusWeeks(1).format(dateTimeFormatter);
        return saturday;
    }

    /**
     * 获取日期的上上周六
     *
     * @return 日期
     */
    public static String getRingSaturdayDateTime(String date) {
        return LocalDate.parse(getSaturdayDateTime(date), dateTimeFormatter).with(DayOfWeek.SATURDAY).minusWeeks(1).format(dateTimeFormatter);
    }

    /**
     * 获取日期的上月的上周六
     *
     * @return 日期
     */
    public static String getSameSaturdayDateTime(String sameDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(getSaturdayDateTime(sameDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        //第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.WEEK_OF_MONTH, week);
        calendar.set(Calendar.DAY_OF_WEEK, 7);

        return sdf.format(calendar.getTime());
    }

    /**
     * 获取日期的上月的上周六
     *
     * @return 日期
     */
    public static String getNewSameSaturdayDateTime(String sameDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(getSaturdayNewDateTime(sameDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        //第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.WEEK_OF_MONTH, week);
        calendar.set(Calendar.DAY_OF_WEEK, 7);

        return sdf.format(calendar.getTime());
    }

    /**
     * 获取日期的上月末
     *
     * @return 日期
     */
    public static String getLastMonthDayDateTime(String date) {
        return LocalDate.parse(date, dateTimeFormatter).minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 获取日期的本月末
     *
     * @return 日期
     */
    public static String getNewLastMonthDayDateTime(String date) {
        return LocalDate.parse(date, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 获取日期的上上月末
     *
     * @return 日期
     */
    public static String getRingMonthDayDateTime(String date) {
        return LocalDate.parse(date, dateTimeFormatter).minusMonths(2).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 获取日期的去年日期上月末
     *
     * @return 日期
     */
    public static String getSameMonthDayDateTime(String date) {
        return LocalDate.parse(date, dateTimeFormatter).minusMonths(13).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 获取日期的去年日期上月末
     *
     * @return 日期
     */
    public static String getNewSameMonthDayDateTime(String date) {
        return LocalDate.parse(date, dateTimeFormatter).minusMonths(12).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 如果开始日期小于结束日期减12月的日期，则返回开始日期，否则返回结束日期减12月的日期
     *
     * @return 日期
     */
    public static String getMonthDateTimeMinus(String fromDate, String toDate) {
        String minusTwelveMonthDate = LocalDate.parse(toDate, dateTimeFormatter).minusMonths(12).format(dateTimeFormatter);
        if (Integer.parseInt(fromDate) < Integer.parseInt(minusTwelveMonthDate)){
            return fromDate;
        }
        return minusTwelveMonthDate;
    }

    /**
     * 当前日期加几天
     *
     * @param days 天数
     * @return 日期
     */
    public static String getDateTimePlusDays(long days) {
        return LocalDate.now().plusDays(days).format(dateTimeFormatter);
    }

    /**
     * 日期20190101变成2019-01-01
     *
     * @param date 日期
     * @return 日期
     */
    public static String changeDateFormat(String date) {
        StringBuilder builder = new StringBuilder(date);
        builder.insert(4, "-");
        builder.insert(7, "-");
        return builder.toString();
    }


    /**
     * 判断当月和本月
     * 获取某个时间在一段时间内的位置索引
     * 如参数 ： 20190101，20190110，20190102  返回 1
     * 如果传的日期不在范围内，返回 -1
     * @param date
     * @return
     */
    public static int getNewDateIndex(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String endDate = LocalDate.parse(df.format(new Date()), dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
        String startDate = LocalDate.parse(df.format(new Date()), dateTimeFormatter).with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter);

        String temp = startDate;
        int index = 0;

        final int parseInt = Integer.parseInt(date);
        if (parseInt < Integer.parseInt(startDate) || parseInt > Integer.parseInt(endDate)) {
            return -1;
        }

        for (; ; ) {
            if (parseInt == Integer.parseInt(temp)) {
                break;
            }
            temp = getDateTimePlus(temp);
            index++;
        }
        return index;
    }

    /**
     * 获取某个时间在一段时间内的位置索引
     * 如参数 ： 20190101，20190110，20190102  返回 1
     * 如果传的日期不在范围内，返回 -1
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param date      日期
     * @return 位置
     */
    public static int getDateIndex(String startDate, String endDate, String date) {
        String temp = startDate;
        int index = 0;

        final int parseInt = Integer.parseInt(date);
        if (parseInt < Integer.parseInt(startDate) || parseInt > Integer.parseInt(endDate)) {
            return -1;
        }

        for (; ; ) {
            if (parseInt == Integer.parseInt(temp)) {
                break;
            }
            temp = getDateTimePlus(temp);
            index++;
        }
        return index;
    }

    /**
     * 获取某个周六在一段时间内是第几个周六
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param weekDate  日期
     * @return 位置
     */
    public static int getWeekDateIndex(String startDate, String endDate, String weekDate) {
        int index = 0;
        final int parseInt = Integer.parseInt(weekDate);
        if (parseInt < Integer.parseInt(startDate) || parseInt > Integer.parseInt(endDate)) {
            return -1;
        }

        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(DayOfWeek.SATURDAY);
        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (format.equals(weekDate)) {
                break;
            }
            localDate = localDate.plusWeeks(1);
            if (Integer.parseInt(format) >= Integer.parseInt(startDate)) {
                index++;
            }
        }

        return index;
    }

    /**
     * 判断日期是否是周六
     *
     * @param weekDate 日期
     * @return 结果
     */
    public static boolean judgeDateSaturday(String weekDate) {
        return LocalDate.parse(weekDate, dateTimeFormatter).getDayOfWeek() == DayOfWeek.SATURDAY;
    }

    /**
     * 获取某个时间在一段时间内是第几个月份
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param monthDate 日期
     * @return 位置
     */
    public static int getMonthDateIndex(String startDate, String endDate, String monthDate) {
        int index = 0;
        final int parseInt = Integer.parseInt(monthDate);
        if (parseInt < Integer.parseInt(startDate) || parseInt > Integer.parseInt(endDate)) {
            return -1;
        }

        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth());
        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (format.equals(monthDate)) {
                break;
            }
            index++;
            localDate = localDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        return index;
    }

    /**
     * 判断日期是否是月末
     *
     * @param monthDate 日期
     * @return 结果
     */
    public static boolean judgeLastDayOfMonth(String monthDate) {
        return Period.between(LocalDate.parse(monthDate, dateTimeFormatter),
                LocalDate.parse(monthDate, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth())).getDays() == 0;
    }

    /**
     * 获取下周六日期
     *
     * @param date 日期
     * @return 日期
     */
    public static String getNextSaturdayDate(String date) {
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter).with(DayOfWeek.SATURDAY);
        String format = localDate.format(dateTimeFormatter);
        if (Integer.parseInt(format) < Integer.parseInt(date)) {
            localDate = localDate.plusWeeks(1);
        }

        return localDate.format(dateTimeFormatter);
    }

    /**
     * 日期减几天
     *
     * @param date 日期
     * @param day  天数
     * @return 日期
     */
    public static String getDateTimeMinusDay(String date, long day) {
        return LocalDate.parse(date, dateTimeFormatter).minusDays(day).format(dateTimeFormatter);
    }

    /**
     * 相差几天
     *
     * @param date 日期
     * @return 负的天数
     */
    public static String getDateTimePeriodDay(String date) {
        String currentDate = LocalDate.now().format(dateTimeFormatter);
        if (Integer.parseInt(date) <= Integer.parseInt(currentDate)) {
            return "-" + Period.between(LocalDate.parse(date, dateTimeFormatter), LocalDate.now()).getDays();
        }
        return String.valueOf(Period.between(LocalDate.now(), LocalDate.parse(date, dateTimeFormatter)).getDays());
    }

    /**
     * 获取一段时间内的周六列表，包含结束时间为周六的日期
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 周六日期集合
     */
    public static List<String> getWeekList(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(DayOfWeek.SATURDAY);
        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                break;
            }
            if (Integer.parseInt(format) < Integer.parseInt(startDate)) {
                localDate = localDate.plusWeeks(1);
            } else {
                localDate = localDate.plusWeeks(1);
                dateList.add(format);
            }
        }

        return dateList;
    }

    /**
     * 获取一段时间内的周六列表，包含结束时间为周六的日期不包含本周周六
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 周六日期集合
     */
    public static JSONArray getJsonArrayWeekList(String startDate, String endDate) {
        JSONArray jsonArray = new JSONArray();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(DayOfWeek.SATURDAY);
        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                break;
            }
            if (Integer.parseInt(format) < Integer.parseInt(startDate)) {
                localDate = localDate.plusWeeks(1);
            } else {
                localDate = localDate.plusWeeks(1);
                jsonArray.add(changeDateFormat(format));
            }
        }

        return jsonArray;
    }

    /**
     * 获取一段时间内的周六列表，包含结束时间为周六的日期以及本周周六
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 周六日期集合
     */
    public static JSONArray getJsonArrayNewWeekList(String startDate, String endDate) {
        JSONArray jsonArray = new JSONArray();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(DayOfWeek.SATURDAY);
        Integer i = 0;
        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                if(Integer.parseInt(format) == Integer.parseInt(endDate)){
                    break;
                }else{
                    i++;
                    if(i > 1){
                        break;
                    }
                }
            }
            if (Integer.parseInt(format) < Integer.parseInt(startDate)) {
                localDate = localDate.plusWeeks(1);
            } else {
                localDate = localDate.plusWeeks(1);
                jsonArray.add(changeDateFormat(format));
            }
        }

        return jsonArray;
    }

    /**
     * 获取一段时间内的月份集合
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 月份集合
     */
    public static List<String> getMonthList(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth());

        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                break;
            }
            dateList.add(format);
            localDate = localDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }
        return dateList;
    }

    /**
     * 获取一段时间内的月份集合
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 月份集合
     */
    public static JSONArray getJsonArrayNewMonthList(String startDate, String endDate) {
        JSONArray jsonArray = new JSONArray();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth());
        Integer i = 0;

        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                i++;
                if(i > 1){
                    break;
                }
            }
            jsonArray.add(changeDateFormat(format).substring(0, 7));
            localDate = localDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        }
        return jsonArray;
    }

    /**
     * 获取一段时间内的月份集合
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 月份集合
     */
    public static JSONArray getJsonArrayMonthList(String startDate, String endDate) {
        JSONArray jsonArray = new JSONArray();
        LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth());

        for (; ; ) {
            String format = localDate.format(dateTimeFormatter);
            if (Integer.parseInt(format) > Integer.parseInt(endDate)) {
                break;
            }
            jsonArray.add(changeDateFormat(format).substring(0, 7));
            localDate = localDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }
        return jsonArray;
    }

    /**
     * 月的一号
     *
     * @param date 日期
     * @return 月份份一号
     */
    public static String getMonthFirstDay(String date) {
        return LocalDate.parse(date, dateTimeFormatter).with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 月的最后一天
     *
     * @param date 日期
     * @return 月份份最后一号
     */
    public static String getMonthLastDay(String date) {
        return LocalDate.parse(date, dateTimeFormatter).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter);
    }

    /**
     * 获取日期集合
     * 如：20190101,20190103 返回 2019-01-01,2019-01-02,2019-01-03
     *
     * @param fromDate 开始日期
     * @param toDate   结束日期
     * @return 日期集合
     */
    public static JSONArray getPeriodDateList(String fromDate, String toDate) {

        if (StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
            return new JSONArray();
        }

        if (Integer.parseInt(fromDate) > Integer.parseInt(toDate)) {
            return new JSONArray();
        }

        JSONArray dates = new JSONArray();
        String date = fromDate;
        for (; ; ) {
            dates.add(changeDateFormat(date));
            if (Integer.parseInt(date) >= Integer.parseInt(toDate)) {
                break;
            }
            date = getDateTimePlus(date);
        }
        return dates;
    }
}
