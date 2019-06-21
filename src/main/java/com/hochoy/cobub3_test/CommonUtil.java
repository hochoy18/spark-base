package com.hochoy.cobub3_test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 普通的工具类
 */
public class CommonUtil {

    private CommonUtil() {
    }

    /**
     * 整数二维数组对应的值相加
     *
     * @param oldCount 老数据
     * @param newCount 新数据
     */
    public static void arrayCorrespondingValueAdd(int[][] oldCount, int[][] newCount) {
        for (int j = 0; j < newCount.length; j++) {
            for (int k = 0; k < newCount[j].length; k++) {
                newCount[j][k] += oldCount[j][k];
            }
        }
    }

    /**
     * 针对事件分析查询的分组结果的排序
     *
     * @param countMap 结果
     * @return 排序结果
     */
    public static Map<String, int[][]> mapValueSort(Map<String, int[][]> countMap) {
        List<Map.Entry<String, int[][]>> list = new ArrayList<>(countMap.entrySet());
        list.sort((o1, o2) -> {
            Integer total1 = 0;
            for (int i = 0; i < o1.getValue().length; i++) {
                for (int j = 0; j < o1.getValue()[i].length; j++) {
                    total1 += o1.getValue()[i][j];
                }
            }

            Integer total2 = 0;
            for (int i = 0; i < o2.getValue().length; i++) {
                for (int j = 0; j < o2.getValue()[i].length; j++) {
                    total2 += o2.getValue()[i][j];
                }
            }
            return total2.compareTo(total1);
        });

        Map<String, int[][]> newCountMap = new LinkedHashMap<>();
        for (Map.Entry<String, int[][]> entry : list) {
            newCountMap.put(entry.getKey(), entry.getValue());
        }
        return newCountMap;
    }

    /**
     * 对Map<String, Integer> map的值排序
     *
     * @param countMap 结果
     * @return 排序结果
     */
    public static Map<String, Integer> mapIntValueSort(Map<String, Integer> countMap) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(countMap.entrySet());
        list.sort((o1, o2) -> {
            Integer total1 = o1.getValue();
            Integer total2 = o2.getValue();
            return total2.compareTo(total1);
        });

        Map<String, Integer> newCountMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            newCountMap.put(entry.getKey(), entry.getValue());
        }
        return newCountMap;
    }
}
