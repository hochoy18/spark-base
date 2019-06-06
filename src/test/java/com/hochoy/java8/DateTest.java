package com.hochoy.java8;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/5/28
 */
public class DateTest {
    /**
     * 计算两个日期间相隔的周数
     *
     * @param startDate
     *            开始日期
     * @param endDate
     *            结束日期
     * @return
     */
    public static int computeWeek(Date startDate, Date endDate) {

        int weeks = 0;

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        while (beginCalendar.before(endCalendar)) {

            // 如果开始日期和结束日期在同年、同月且当前月的同一周时结束循环
            if (beginCalendar.get(Calendar.YEAR) == endCalendar
                    .get(Calendar.YEAR)
                    && beginCalendar.get(Calendar.MONTH) == endCalendar
                    .get(Calendar.MONTH)
                    && beginCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == endCalendar
                    .get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                break;

            } else {

                beginCalendar.add(Calendar.DAY_OF_YEAR, 7);
                weeks += 1;
            }
        }

        return weeks;
    }
    public static void main(String[] args) throws ParseException {
//        between();
        String str1 = "2019-04-30";
        String str2 = "2019-05-01";
        int diff = getMonthDiff(str1, str2);
        System.out.println("month diff   "+diff);



        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date1 = format.parse("20190525");
        Date date2 = format.parse("20190527");
        int weeks = computeWeek(date1,date2);
        System.out.println("weeks...   "+weeks);


//        int i =getMonthDiff(date1,date2);
//        System.out.println(i);

    }

    static void between() {
        LocalDate nextDate = LocalDate.of(2016, 2, 29);
        LocalDate preDate = LocalDate.of(2016, 1, 29);

        Period p = Period.between(preDate, nextDate);
        System.out.println(p);
        System.out.println("months..." + p.getMonths());
        System.out.println("days..." + p.getDays());
        System.out.println("years..." + p.getYears());
    }

    /**
     * java 计算两个日期相差了几个月
     */

    public static int getMonthDiff(String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(sdf.parse(startDate));
        aft.setTime(sdf.parse(endDate));
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


    public static int getWeeksDiff1(Date dtS, Date dtE){
        Calendar calS = Calendar.getInstance();
        calS.setTime(dtS);
        long after = calS.getTimeInMillis();
        if(calS.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){//不是周一不计入
            calS.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        long before = calS.getTimeInMillis();
//        System.out.printf("after = %f \nbefore = %f \nafter - before = %f ",before,after,(after-before)/(1000 * 60 * 60  * 24));
        System.out.printf("after = %d \nbefore = %d \nafter - before = %d \n",before,after,(after-before)/(1000 * 60 * 60  * 24));

        Calendar calE = Calendar.getInstance();
        calE.setTime(dtE);
        return (int) (Math.ceil((calE.getTimeInMillis()-calS.getTimeInMillis())/(86400000.0 * 7))-1);
        //return 1;
    }



}







class CalendarTest {
    static int xiqiji(Calendar c){
        int dayForWeek;
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            dayForWeek = 7;
        }else{
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;}
        return dayForWeek;
    }

    static Calendar From(Calendar c){
        switch(xiqiji(c)){
            case 1:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-0);
                break;
            case 2:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
                break;
            case 3:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-2);
                break;
            case 4:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-3);
                break;
            case 5:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-4);
                break;
            case 6:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-5);
                break;
            case 7:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-6);
        }
        return c;

    }

    static Calendar To(Calendar c){
        switch(xiqiji(c)){
            case 1:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+6);
                break;
            case 2:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+5);
                break;
            case 3:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+4);
                break;
            case 4:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+3);
                break;
            case 5:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+2);
                break;
            case 6:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+1);
                break;
            case 7:c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)+0);
        }
        return c;

    }

    static  long  weeksDiff(String from,String to ) throws  Exception{
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        c1.setTime(sdf.parse(from));
        c2.setTime(sdf.parse(to));

        long cc1=From(c1).getTimeInMillis()/(1000*3600*24);
        long cc2=To(c2).getTimeInMillis()/(1000*3600*24);
        long weeks = (cc2-cc1+1)/7;
        System.out.println(weeks);
        return weeks;

    }

    public static void main(String[] args) throws Exception {

        long  weeks = weeksDiff("2019-05-23","2019-05-25");
        System.out.println("weeks   "+ weeks);



    }
}


class GetWeek {
    public static void main(String[] args) throws ParseException {
        String staDate = "2019-05-23";
        String endDate = "2019-05-25";

        SimpleDateFormat myMat = new SimpleDateFormat("yyyy-MM-dd");
        //算出相隔天数
        int dayNum =  (int) ((myMat.parse(endDate).getTime() -
                myMat.parse(staDate).getTime())/(1000*60*60*24));

        Calendar cal = Calendar.getInstance();
        cal.setTime(myMat.parse(staDate));
        int staWeek = cal.get(Calendar.DAY_OF_WEEK);//获取星期几，星期日为第一天值为1
        staWeek = yourWeek(staWeek);//把星期几换成你要的，星期一为第一天值为1
        System.out.println("staWeek:"+staWeek);
        dayNum = dayNum + (staWeek-1);//把第一个星期已经过去的天数加到总数中;(staWeek-1)当天算已经过去了

        cal.setTime(myMat.parse(endDate));
        int endWeek = cal.get(Calendar.DAY_OF_WEEK);
        endWeek = yourWeek(endWeek);
        System.out.println("endWeek:"+endWeek);
        dayNum = dayNum + (7-(endWeek-1));//把最后一个星期剩余的天数加到总数；(staWeek-1)当天算刚开始

        System.out.println(dayNum/7.0);//除以7.0是为了验证会不会有小数

    }
    public static int yourWeek(int oldWeek){
        int newWeek = -1;
        newWeek = oldWeek - 1;
        if(newWeek == 0){
            newWeek = 7;
        }
        return newWeek;

    }

}



















