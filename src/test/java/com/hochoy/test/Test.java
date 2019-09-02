package com.hochoy.test;

import com.hochoy.utils.BitmapUtils;
import org.roaringbitmap.RoaringBitmap;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hochoy on 2019/07/07.
 */
public class Test {

    public static void main(String[] args) throws  Exception{

//        Map<Long, Map<String, Long>> stringMapMap = MapTest.test1(genList(), genData());
//        System.out.println(stringMapMap);

        String s = ":0\u0000\u0000\u0001\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0010\u0000\u0000\u0000\u0004\u0000";
        RoaringBitmap integers = BitmapUtils.deSerializeByteArrayToBitMap(s.getBytes("iso8859-1"));
        for (int i : integers.toArray()) {
            System.out.println(i);
        }
        s= ":0\\u0000\\u0000\\u0001\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0010\\u0000\\u0000\\u0000\\u0004\\u0000";
        integers = BitmapUtils.deSerializeByteArrayToBitMap(s.getBytes("iso8859-1"));
        for (int i : integers.toArray()) {
            System.out.println(i);
        }

        System.exit(-1);
        Integer[] arr = {1,2,3,4,5,6};
        List<Integer> list = Arrays.asList(arr);
        List<Integer> res = new ArrayList<>();
        list.forEach(res::add);
        res.forEach(System.out::println);

    }

    static Map<Long, Map<String, Long>> genData() {
        Map<Long, Map<String, Long>> data = new HashMap<Long, Map<String, Long>>();
        Map<String, Long> m = new HashMap<>();
        m.put("count",111L);
        data.put(20190614L,m);
        m.clear();

        m.put("count",111L);
        data.put(20190613L,m);
        m.clear();
        m.put("count",111L);
        data.put(20190612L,m);
        m.clear();
        //////
        m.put("count",222L);
        data.put(20190617L,m);
        m.clear();
        m.put("count",222L);
        data.put(20190628L,m);
        m.clear();
        m.put("count",222L);
        data.put(20190622L,m);
        m.clear();
        m.put("count",333L);m.put("count",333L);m.put("count",333L);
        data.put(20190629L,m);
        return data;
    }

    static Map<Long,List<Long>> genList(){
        Map<Long,List<Long>> map = new HashMap<Long,List<Long>>();
        List l = new ArrayList<>();
        for (int i = 20190609  ; i <= 20190615; i++) {
            l.add(i);
        }
        map.put(20190615L,l);
        l = new ArrayList<>();
        for (int i = 20190616  ; i <= 20190622; i++) {
            l.add(i);
        }
        map.put(20190622L,l);
        l = new ArrayList<>();
        for (int i = 20190623  ; i <= 20190629; i++) {
            l.add(i);
        }
        map.put(20190629L,l);
        return map;
    }

}

class CustomerList extends ArrayList{

}
class MapTest {

    public static Map<Long, Map<String, Long>> test1(Map<Long, List<Long>> dateList, Map<Long, Map<String, Long>> data) {


        Set<Map.Entry<Long, List<Long>>> entries = dateList.entrySet();
        Set<Long> keySet = dateList.keySet();

        Map<String, Long> m = new HashMap<>();
        m.put("count", 0L);


        Map<Long, Map<String, Long>> res = new HashMap<>();

        for (Long key : keySet) {
            res.put(key, m);
        }

        Set<Map.Entry<Long, Map<String, Long>>> dataEntries = data.entrySet();
        for (Map.Entry<Long, Map<String, Long>> dataEntry : dataEntries) {
            Long date = dataEntry.getKey();
            Map<String, Long> countNum = dataEntry.getValue();
            for (Map.Entry<Long, List<Long>> entry : entries) {
                List<Long> dateLists = entry.getValue();
                Long key = entry.getKey();
                if (dateLists.contains(date)) {
                    Map<String, Long> d = res.get(key);
                    d.put("count", d.get("count") + countNum.get("count"));
                    res.put(key, d);
                }
            }
        }


//        for (Map.Entry<String, List<String>> entry : entries) {
//            String date = entry.getKey();
//            List<String> dateLists = entry.getValue();
////            dateLists.contains()
//
//
//
//        }
        return res;
    }

}
