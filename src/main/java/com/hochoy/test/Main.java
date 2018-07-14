package com.hochoy.test;//package com.hochoy.common.test;
//
//import com.hochoy.utils.StringHandle;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.apache.avro.generic.GenericData;
//
//import java.util.*;
//
///**
// * Created by Cobub on 2018/7/4.
// */
//public class Main {
//
//    public static void main(String[] args) {
////        String qualifier = "Z581_2.21_8-LC_5";
////        String vre = qualifier.substring(qualifier.indexOf("_")+1,qualifier.lastIndexOf("_"));
////        String cha = qualifier.substring(qualifier.lastIndexOf("_")+1);
////        System.out.println("version ......   "+vre);
////
////        System.out.println("channel.......    "+cha);
//        Map<Object, List<JSONObject>> map = new HashMap<Object,List<JSONObject>>();
//        List<JSONObject> list = new ArrayList<JSONObject>();
//        JSONObject obj = new JSONObject();
//        obj.put("20180301",12);
//        obj.put("20180401",13);
//        obj.put("20180501",15);
//        list.add(obj);
//        map.put("5",list);
//
//        list = new ArrayList<JSONObject>();
//        obj = new JSONObject();
//        obj.put("20180301",12);
//        obj.put("20180501",15);
//        list.add(obj);
//        map.put("6",list);
//
//        list = new ArrayList<JSONObject>();
//        obj = new JSONObject();
//        obj.put("20180201",12);
//        obj.put("20180501",15);
//        list.add(obj);
//        map.put("7",list);
//
//        JSONObject jo = new JSONObject();
//        Set<Object> set = map.keySet();
//        Iterator<Object> it = set.iterator();
//        JSONArray arr = new JSONArray();
//        loopItValue(arr,map,it,jo,20180601,"20180301");
//        System.out.println(arr);
//    }
//
//
//
//
//    private  static  void loopItValue(JSONArray arr, Map<Object, List<JSONObject>> map, Iterator<Object> it, JSONObject jo, int stopTimeint, String startTime) {
//        String currentTime = startTime;
//        while (it.hasNext()) {
//            jo = new JSONObject();
//            String channelid = (String) it.next();
//            List<JSONObject> listobj = map.get(channelid);
//            JSONArray arr1 = new JSONArray();
//            for (int i = 0; i < listobj.size(); i++) {
//                while (Integer.parseInt(currentTime) <= stopTimeint) {
//                    JSONObject joo = listobj.get(i);
//                    int monUser = joo.containsKey(currentTime)? Integer.parseInt(joo.getString(currentTime)):0;
//                    JSONObject obj = new JSONObject();
//                    obj.put("datevalue", currentTime.substring(0,6));
//                    obj.put("monthusers", monUser);
//                    currentTime = StringHandle.nextMonthByDate(currentTime);
//                    arr1.add(obj);
//                    System.out.println(channelid+"......."+arr1);
//                }
//            }
//            currentTime = startTime;
//            if (!jo.containsKey(channelid)){
//                jo.put(channelid,arr1);
//            }
//            arr.add(jo);
//        }
//
//    }
//
//
//
//
//
//
//}
