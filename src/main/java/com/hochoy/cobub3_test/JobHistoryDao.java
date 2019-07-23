package com.hochoy.cobub3_test;


import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2017/2/17.
 */
public interface JobHistoryDao {

    List<CobubJobHistory> getJobHistoryList(Map<String, Object> map);
    int logJobHistory(CobubJobHistory cobubJobHistory);
}
