package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class SchemaUtil {

    /**
     * 动态创建schema
     *
     * @param groupDataEntity
     */
    public static boolean dynamicCreateSchema(GroupDataEntity groupDataEntity, String hbaseNameSpace, JobServer jobServer, String maxLine, String jobName, Logger logger) {
        String groupId = groupDataEntity.getGroupId();
        String jobId = jobName + UUID.randomUUID().toString();
        StringBuilder groupid = new StringBuilder();//用户分群
        groupid.append("{").append(groupId).append("}");
        String params = ",namespace=\"" + hbaseNameSpace + "\",prop=\"{}\",groupid=\"" + groupid + "\",isImport = \"true\"";

        HttpEntity entity = CobubHttpClient.getInstance()
                .doPost(String.format(jobServer.schemaCreateUrl(), "cobub-session-context"),
                        "jobId=" + jobId + ",maxLine=" + maxLine + params);

        JSONObject responseResult = null;
        boolean result = true;
        try {
            String httpR = EntityUtils.toString(entity, Consts.UTF_8);
            responseResult = JSONObject.parseObject(httpR);
        } catch (Exception e) {
            logger.error("动态创建schema出错", e);
        }

        if (responseResult != null && responseResult.containsKey("status") && "ERROR".equalsIgnoreCase(responseResult.getString("status"))   //FileNotFoundException说明parquetTmpTable表的文件被合并，找不到了，需要刷新表
                && responseResult.getJSONObject("result").getString("message").contains("java.io.FileNotFoundException")) {
            result = false;
            logger.error("动态创建schema出错");
        }
        return result;

    }


    /**
     * 事件分析实时查询
     *
     * @param sqlBuilder sql语句
     * @param maxLine 返回最大记录数
     * @return 结果
     * @throws IOException 异常
     */
//    public static JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine, String params, JobHistoryDao jobHistoryDao, JobServer jobServer, String jobName, Logger logger) throws IOException {
//        String jobId = jobName + UUID.randomUUID().toString();
//        CobubJobHistory cobubJobHistory = new CobubJobHistory();
//        cobubJobHistory.setJobId(jobId);
//        cobubJobHistory.setTaskId("");
//        cobubJobHistory.setSqlDetail(sqlBuilder.toString());
//        jobHistoryDao.logJobHistory(cobubJobHistory);
//
//        // 查询
//        HttpEntity entity = CobubHttpClient.getInstance()
//                .doPost(String.format(jobServer.startJobUrl(), "cobub-session-context"),
//                        "jobId=" + jobId + ",maxLine=" + maxLine + ",sql = \" " + sqlBuilder.toString() +params+ " "); //一开始别加refreshtable=true, 文件数量大的情况比较耗时
//        String httpR = EntityUtils.toString(entity, Consts.UTF_8);
//
//        JSONObject responseResult = null;
//
//        try {
//            responseResult = JSONObject.parseObject(httpR);
//        } catch (Exception e) {
//            logger.error("some exception", e);
//        }
//
//        if (responseResult != null && responseResult.containsKey("status") && "ERROR".equalsIgnoreCase(responseResult.getString("status"))   //FileNotFoundException说明parquetTmpTable表的文件被合并，找不到了，需要刷新表
//                && responseResult.getJSONObject("result").getString("message").contains("java.io.FileNotFoundException")) {
//            logger.info(".... need to refresh table ");
//            entity = CobubHttpClient.getInstance()
//                    .doPost(String.format(jobServer.startJobUrl(), "cobub-session-context"),
//                            "refreshtable=true,maxLine=" + maxLine + ",jobId=" + jobId + ",sql = \" " + sqlBuilder.toString() + " \"");
//            httpR = EntityUtils.toString(entity, Consts.UTF_8);
//            try {
//                responseResult = JSONObject.parseObject(httpR);
//            } catch (Exception e) {
//                logger.error("some exception", e);
//            }
//        }
//        return responseResult;
//    }
    public static JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine, String params, JobHistoryDao jobHistoryDao, JobServer jobServer, String jobName, Logger logger) throws IOException {
        String jo2s = "{\"jobId\":\"6b9245c0-1f39-485a-8489-75a1e21742be\",\"result\":[\"2019-06-13\\u0001null\\u0001WrappedArray(677840, 755925, 744561)\",\"2019-06-15\\u0001null\\u0001WrappedArray(12035)\",\"2019-06-10\\u0001null\\u0001WrappedArray(54054, 76520, 36187)\",\"2019-06-12\\u0001null\\u0001WrappedArray(558652, 24494, 505066, 565563, 340700)\",\"2019-06-11\\u0001null\\u0001WrappedArray(19490)\",\"2019-06-15\\u0001null\\u0001WrappedArray(12035)\",\"2019-06-10\\u0001null\\u0001WrappedArray(54054, 76520, 36187)\",\"2019-06-11\\u0001null\\u0001WrappedArray(19490)\",\"2019-06-17\\u0001null\\u0001WrappedArray(1116533, 237451, 273788)\",\"2019-06-12\\u0001null\\u0001WrappedArray(558652, 24494, 505066, 565563, 340700)\",\"2019-06-17\\u0001null\\u0001WrappedArray(1116533, 237451, 273788)\",\"2019-06-13\\u00010\\u0001WrappedArray(497108, 745416)\",\"2019-06-13\\u0001null\\u0001WrappedArray(677840, 497108, 745416, 755925, 744561)\"]}";
        JSONObject jo2 = JSONObject.parseObject(jo2s);
        return jo2;
    }
}
