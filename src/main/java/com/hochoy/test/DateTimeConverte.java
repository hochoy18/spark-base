package com.hochoy.test;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Cobub on 2018/7/31.
 */
public class DateTimeConverte {
    public static void main(String[] args) throws ParseException{
        String starttime = "2018030221";
        String endtime = "2018030323";
        convert(starttime,endtime);

    }

    static List<List<String>> convert(String starttime,String endtime) throws ParseException{
        List<List<String>> lists = new ArrayList<List<String>>();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        Date st = null;
        Date et = null;
        try {
            st = format.parse(starttime);
            et  = format.parse(endtime);
        } catch (Exception e) {
//            LOGGER.error("(Constants.AN_ERROR_WAS_CAUGHT", e);
        }
        if (st == null)
            return null ;

        String startDate = starttime.substring(0,8);
        String endDate = endtime.substring(0,8);


        if (startDate.equals(endDate)){
            List<String > list  = new ArrayList<String>();
            while (st.before(et)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(st);
                cal.add(Calendar.HOUR, 1);// 24小时制
                st = cal.getTime();
                String dayHour = format.format(st);
                list.add(dayHour);
            }
            lists.add(list);
        }else {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            Date sd = format1.parse(startDate);
            Date ed = format1.parse(endDate);
            List<String > list  = new ArrayList<String>();
            while (sd.before(ed)){
                Calendar cal = Calendar.getInstance();
                cal.setTime(sd);
                while (st.before(et)){
                    list.add(format.format(st));
                }

                cal.add(Calendar.DATE,1);

            }

        }

        return null;
    }





























}
