package com.hochoy.test;

import com.hochoy.utils.StringHandle;
import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by Cobub on 2018/7/23.
 */
public class GetMonthRate {

    static Map<Object, List<JSONObject>> init() {
        Map<Object, List<JSONObject>> map = new HashMap<Object, List<JSONObject>>();
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jo = new JSONObject();
        jo.put("datevalue", "201803");
        jo.put("monthusers", "12");
        list.add(jo);

        jo = new JSONObject();
        jo.put("datevalue", "201804");
        jo.put("monthusers", "11");
        list.add(jo);
        jo = new JSONObject();
        jo.put("datevalue", "201805");
        jo.put("monthusers", "10");
        list.add(jo);
        map.put("5", list);
        list = new ArrayList<JSONObject>();
        jo = new JSONObject();
        jo.put("datevalue", "201804");
        jo.put("monthusers", "8");
        list.add(jo);
        map.put("6", list);


        return map;
    }

    public static void main(String[] args) {
        Map<Object, List<JSONObject>> map = init();
        System.out.println(map);
        String startDate = "20180404";
        String stopDate = "20180604";
        String startTime = StringHandle.previousMonthByDate(startDate);
        String stopTime = StringHandle.previousMonthByDate(stopDate);

        Set<Object> mapKey = map.keySet();
        List<String> channels = new ArrayList<String>();
        for (Object obj :mapKey){
            channels.add(obj.toString());
        }
//        String currentTime = startTime;

        JSONArray allArr = new JSONArray();
        for (int i =0;i<channels.size();i++){
            JSONArray ja = new JSONArray();
            String currentTime = startTime;

            String c_id = channels.get(i);
            //获取 c_id 渠道的 月活用户
            List<JSONObject> list =map.get(c_id);


            while (Integer.parseInt(currentTime)<=Integer.parseInt(stopTime)){
                String monthdate = currentTime.substring(0, 6);
                JSONObject jobj = new JSONObject();
                for (int j =0;i<list.size();j++){
                    JSONObject jj = list.get(j);
                    if (monthdate.equals(jj.getString("datevalue"))){
                        jobj.put("datevalue", jj.getString("datevalue"));
                        jobj.put("monthusers",  jj.getInt("monthusers"));
//                        map.get(c_id).remove(j);
                        break;
                    }else{
                        jobj = initEmptyMonthObj(monthdate);
                        break;
                    }

                }
                //monthdate 无数据 初始化
                currentTime = StringHandle.nextMonthByDate(currentTime);
                ja.add(jobj);
            }
            JSONObject jo = new JSONObject();
            jo.put(channels.get(i), ja);
            allArr.add(jo);
        }
        System.out.println(allArr);

        System.exit(-1);






        JSONArray chaArr = new JSONArray();
        for (int i =0;i<channels.size();i++){
            String chanId = channels.get(i);
            List<JSONObject> joList = map.get(chanId);

            for (int j =0;j<joList.size();j++){
                JSONObject jo =joList.get(j);
                jo.getString("datevalue");
                jo.getString("monthusers");
            }

        }













//        for (int i =0;i<allArr.size();i++){
//            JSONObject jo = allArr.getJSONObject(i);
//            Iterator<String> keys = jo.keys();
//            while (keys.hasNext()){
//                String key = keys.next();
//                JSONObject j = jo.getJSONObject(key);
//            }
//        }

        Iterator<Object> chanId = map.keySet().iterator();
        while (chanId.hasNext()){
            String key = chanId.next().toString();
            List<JSONObject> joList = map.get(key);
            for (int i=0;i<joList.size();i++){
                JSONObject jo = joList.get(i);
                String datevalue = jo.getString("datevalue");
                int monthUsr = jo.getInt("monthusers");

                System.out.println(monthUsr);
//                allArr.getJSONObject()
            }
        }
        System.exit(-1);




//        for (int i =0;i<channels.size();i++){
//            String channelId = channels.get(i);
////            JSONObject jo = new JSONObject();
//            while (Integer.parseInt(currentTime)<Integer.parseInt(stopTime)){
//                JSONObject jo = new JSONObject();
//                String datevalue = currentTime.substring(0, 6);
//                jo.put("datevalue", currentTime.substring(0, 6));
//
//                int monUser = 0;
//                List<JSONObject> listJO = map.get(channelId);
////                for ()
//                jo.put("monthusers", monUser);
//
//            }
//        }
//
//
//        while (Integer.parseInt(currentTime)<Integer.parseInt(stopTime)){
//
//
//
//        }


    }
    public static JSONObject initEmptyMonthObj(String datevalue) {
        JSONObject obj = new JSONObject();
        obj.put("datevalue", datevalue);
        obj.put("monthusers", 0);
        return obj;
    }
}
