//package com.hochoy.cobub3_test;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.cobub.analytics.web.entity.RetentionEntity;
//import com.cobub.analytics.web.mapper.UserMetaTypeMapper;
//import com.cobub.analytics.web.service.RetainService;
//import com.cobub.analytics.web.util.*;
//import org.apache.commons.lang.StringUtils;
//import org.apache.hadoop.hbase.Cell;
//import org.apache.hadoop.hbase.CellUtil;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.client.ResultScanner;
//import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.client.Table;
//import org.apache.hadoop.hbase.filter.CompareFilter;
//import org.apache.hadoop.hbase.filter.RegexStringComparator;
//import org.apache.hadoop.hbase.filter.RowFilter;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import scala.Tuple2;
//import scala.Tuple3;
//import scala.Tuple5;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class RetainServiceImpl_DateOp implements RetainService {
//
//    private static final Logger LOG = LoggerFactory.getLogger(RetainServiceImpl.class);
//
//    private static final String PLATFORM = "platform";
//    private static final String CHANNEL = "channel";
//    private static final String VERSION = "version";
//    private static final String PRODUCT_ID = "productId";
//    private static final String START_DATE = "startDate";
//    private static final String END_DATE = "endDate";
//    private static final String FIX_REPORT_RETENTION = ":fix_report_retention";
//    String parquetTmpTable = "parquetTmpTable";
//    String usersTable = "usersTable";
//    @Value("${hbasenamespace}")
//    private String hbaseNameSpace;
//
//    @Autowired
//    private UserMetaTypeMapper userMetaTypeMapper;
//
//
//    public static void main(String[] args) throws Exception {
//        String json1 = "{\"measures\":[],\"productId\":\"11148\",\"rangeText\":\"过去119天 - 过去140天\",\"from_date\":\"20190611\",\"to_date\":\"20190617\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"chartsType\":\"raw\",\"first_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"iPhone10,3#_#iPhone10,6\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.is_new_device\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"3G\",\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"},\"relevance_field\":\"\"},\"user_filter\":{\"conditions\":[{\"type\":\"user.networkH\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"WIFI4\",\"WIFI5\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"},{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"or\"},\"is_wastage\":false,\"duration_true_name\":\"8\",\"show_zero_day\":\"\",\"request_id\":\"1563864936887:550544\",\"use_cache\":true}";
//        String json = "{\"measures\":[],\"productId\":\"11148\",\"rangeText\":\"过去119天 - 过去140天\",\"from_date\":\"20190611\",\"to_date\":\"20190617\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"chartsType\":\"raw\",\"first_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[],\"relation\":\"or\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"relevance_field\":\"\"},\"user_filter\":{\"conditions\":[],\"relation\":\"or\"},\"is_wastage\":false,\"duration_true_name\":\"8\",\"show_zero_day\":\"\",\"request_id\":\"1563864936887:550544\",\"use_cache\":true}";
//
//
//        json = "{\"productId\":\"11148\",\"from_date\":\"20190611\",\"to_date\":\"20190617\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"first_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"iPhone10,3#_#iPhone10,6\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.is_new_device\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"3G\",\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"},\"relevance_field\":\"\"},\"user_filter\":{\"conditions\":[{\"type\":\"user.networkH\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"WIFI4\",\"WIFI5\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"},{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"or\"},\"is_wastage\":false,\"duration_true_name\":\"8\",\"slice_by_value\":\"20190716\",\"slice_interval\":\"6\",\"userNum\":\"1000\",\"by_field\":\"first.event.day\"}";
//
//        RetainServiceImpl retainService = new RetainServiceImpl();
//        retainService.queryRetentionResultOrUserList(JSONObject.parseObject(json));
//        //retainService.queryRetentionResultOrUserList(JSON.parseObject(json));
////
////        List<JSONObject> jsonObjectList = retainService.generateRetentionList("week","20190601","20190611",8);
////
////        System.out.println(JSONArray.parseArray(jsonObjectList.toString()));
////        System.out.println();
//    }
//
//
//    /**
//     * 查询留存结果以及明细用户
//     *
//     * @param jsonObject 参数
//     * @return 结果
//     */
//    @Override
//    public JSONArray queryRetentionResultOrUserList(JSONObject jsonObject) throws IOException {
//
//        String fromDay = getEventStartDay(jsonObject)._1;
//        if (DateUtil.dateCompare2Now(fromDay) > 0)
//            return new JSONArray();
//
//
//        Tuple2<String, String> retentionSQL = genSQL(jsonObject);
//        System.out.println("\n\nall SQL ....... :   {}  " + retentionSQL._1 + "\ntaskSQL ....... :   {}  " + retentionSQL._2);
//
//
//        JSONObject responseResult = SchemaUtil.querySparkSql(new StringBuilder(retentionSQL._1), "10000", retentionSQL._2, null, null, null, LOG); // todo
//
//        // first.event.day   first.event.model   second.event.country   user.sex   userGroup.group1
//        String byVal = jsonObject.containsKey("by_field") ? jsonObject.getString("by_field") : "first.event.day";
//        JSONObject res = parseSqlQueryResult(responseResult, byVal);
//
//
//        return new JSONArray();
//
//
//    }
//
//    /**
//     * 保存留存参数
//     *
//     * @param jsonObject
//     * @return
//     */
//    @Override
//    public boolean saveRetentionParams(JSONObject jsonObject) {
//
//
//        return false;
//    }
//
//    /**
//     * 查看留存参数
//     *
//     * @param jsonObject => { productId,reportId 报表id,reportType 报表类型(eg.事件分析、留存分析)}
//     * @return
//     */
//    @Override
//    public JSONObject queryRetentionParams(JSONObject jsonObject) {
//        return null;
//    }
//
//    /**
//     * 修改留存标题
//     *
//     * @param jsonObject jsonObject => { productId,reportId,reportName}
//     * @return
//     */
//    @Override
//    public boolean editRetentionTitle(JSONObject jsonObject) {
//        return false;
//    }
//
//    /**
//     * @param jsonObject
//     * @return 返回拼接的sql 和 动态映射的schema
//     */
//    private Tuple2<String, String> genSQL(JSONObject jsonObject) {
//        Tuple3<String, String, String> sql = genRetentionSql(jsonObject);
//
//        String unit = jsonObject.getString("unit"); // week / day / month
//
//        String joinerSQL = genByDayJoinSQL(sql._1(), sql._2(), unit);
//
//        String allSQLFormat = "SELECT %s FROM (%s) tc GROUP BY %s ";
//
//        String selectFields = "from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') , tc.by_day, collect_set(tc.global_user_id) AS usersets";// todo first_day to groupByField
//
//        // todo first_day to groupByField
//        String groupBy = "from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd'), tc.by_day  GROUPING SETS((from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') ,tc.by_day), (from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') ))";
//
//
//        String allSQL = String.format(allSQLFormat, selectFields, joinerSQL, groupBy);
//
//        return new Tuple2(allSQL, sql._3());
//    }
//
//
//    /**
//     * @param jsonObject
//     * @return 返回初始行为 、 后续行为分别于user表join后的SQL，以及user表需要动态映射的schema ，Tuple3._1() : 初始行为join user的SQL，Tuple3._2() 后续行为join user 的sql，Tuple3._3() 动态映射的schema
//     */
//    private Tuple3<String, String, String> genRetentionSql(JSONObject jsonObject) {
//
//        Boolean isExtend = jsonObject.getBoolean("extend_over_end_date");// false 初始行为，后续行为时间相同，true，后续行为需要加天周月的事件周期长度
//        Integer duration = jsonObject.getInteger("duration"); // 8/7/12 ...
//        String unit = jsonObject.getString("unit"); // week / day / month
//
//        String productId = jsonObject.getString("productId");
//        String from = jsonObject.getString("from_date");
//        String to = jsonObject.getString("to_date");
//
//        JSONObject firstEvent = jsonObject.getJSONObject("first_event");
//        JSONObject secondEvent = jsonObject.getJSONObject("second_event");
//        JSONObject userFilter = jsonObject.getJSONObject("user_filter");
//
//        Tuple2<String, String> eventStartDay = getEventStartDay(jsonObject);
//        String firstCommWhere = String.format(" productid = '%s' AND day >= %s AND  day <= %s ", productId, eventStartDay._1, getFirstEventEndDay(jsonObject));
//        String secondCommWhere = String.format(" productid = '%s' AND day >= %s AND  day <= %s ", productId, eventStartDay._2, getSecondEventEndDay(jsonObject)); // todo week/month from 取值
//        String by_field = jsonObject.containsKey("by_field") ? jsonObject.getString("by_field") : null;
//        // first.event.country / second.event.city / user.sex / userGroup.group1
//        String firstActionSQL = genActionSQL(firstCommWhere, firstEvent, by_field);
//        String secondActionSQL = genActionSQL(secondCommWhere, secondEvent, by_field);
//
//        /**
//         * 判断是否 需要查 用户属性/用户分群，
//         * 没有用户筛选条件  且  不包含 by_field字段(该字段只有在以非 初始行为时间分组时才有，） by_field 不以user或userGroup 分组
//         */
//        boolean hasUser = true;
//        if (userFilter.isEmpty() &&
//                (!jsonObject.containsKey("by_field") ||
//                        (jsonObject.containsKey("by_field") && !jsonObject.getString("by_field").startsWith("user."))
//                )) {
//            hasUser = false;
//        }
//        Tuple3<String, Set<String>, Map<String, String>> userSQL = new Tuple3<>(null, null, null);
//        String taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"{}\",groupid=\"{}\"";
//        if (hasUser) {
//            Map<String, String> userPropertiesMap = getUserPropertiesAndTypes(productId);
//            userSQL = genUserSQL(userFilter, productId, by_field, userPropertiesMap);
//
//            Map<String, String> props = userSQL._3();
//            Set<String> groupIds = userSQL._2();
//            StringJoiner prop = new StringJoiner(",", "{", "}");
//            props.forEach((userPropName, userPropDataType) -> prop.add(String.format("\\\"%s\\\":\\\"%s\\\"", userPropName, userPropDataType)));
//            taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"" + prop.toString() + "\",groupid=\"{" + groupIds.stream().collect(Collectors.joining(",")) + "}\"";
//        }
//
//        String firstJoinerSQL = actionUserSqlJoiner(firstActionSQL, userSQL._1(), productId, true);
//        String secondJoinerSQL = actionUserSqlJoiner(secondActionSQL, userSQL._1(), productId, false);
//
//        return new Tuple3(firstJoinerSQL, secondJoinerSQL, taskSql);
//
//    }
//
//    /**
//     * 后续行为的结束日期：
//     *
//     * @return 返回后续行为事件的结束日期
//     */
//    private String getSecondEventEndDay(JSONObject jsonObject) {
//        Boolean isExtend = jsonObject.getBoolean("extend_over_end_date");// false 初始行为，后续行为时间相同，true，后续行为需要加天周月的事件周期长度
//        Integer duration = jsonObject.getInteger("duration"); // 8/7/12 ...
//        String unit = jsonObject.getString("unit"); // week / day / month
//        String to = jsonObject.getString("to_date");
//        String slice_by_value = jsonObject.getString("slice_by_value");
//        Integer slice_interval = Integer.parseInt(jsonObject.getString("slice_interval"));
//        boolean flag = "".equals(slice_by_value.replaceAll(" ", ""));
//        String secondEndDay = to;
//        String end;
//        if(flag){
//            if (!isExtend) {
//                switch (unit) {
//                    case "week":
//                        end = DateUtil.getLastDayOfWeek(to);
//                        secondEndDay = DateUtil.dateCompare2Now(end) > 0 ? end : DateUtil.getLastDayOfWeek(DateUtil.dateFormat(new Date()));
//                        break;
//                    case "month":
//                        end = DateUtil.getLastDayOfMonth(to);
//                        secondEndDay = DateUtil.dateCompare2Now(end) > 0 ? end : DateUtil.getLastDayOfMonth(DateUtil.dateFormat(new Date()));
//                        break;
//                    default:
//                        secondEndDay = to;
//                        break;
//                }
//            } else
//                switch (unit) {
//                    case "day":
//                        end = DateUtil.stringDateDecrease(to, duration);
//                        //dateCompare2Now:otherDate 跟今天日期对比，在今天之前则返回true，否则返回false
//                        secondEndDay = DateUtil.dateCompare2Now(end) > 0 ? end : DateUtil.dateFormat(new Date());
//                        break;
//                    case "week":
//                        end = DateUtil.getLastDayOfWeek(DateUtil.stringDateDecrease(to, 7 * duration)); // 加 duration * 7天后日期的周日
//                        secondEndDay = DateUtil.dateCompare2Now(end) > 0 ? end : DateUtil.getLastDayOfWeek(DateUtil.dateFormat(new Date()));
//                        break;
//                    case "month":
//                        end = DateUtil.getLastDayOfMonth(DateUtil.dateAddMonth(to, duration));
//                        secondEndDay = DateUtil.dateCompare2Now(end) > 0 ? end : DateUtil.getLastDayOfMonth(DateUtil.dateFormat(new Date()));
//                        break;
//                }
//        }else {
//
//        }
//        return secondEndDay;
//    }
//
//    /**
//     * @return 返回初始行为事件的结束日期
//     */
//    private String getFirstEventEndDay(JSONObject jsonObject) {
//
//        String unit = jsonObject.getString("unit"); // week / day / month
//        String slice_by_value = jsonObject.getString("slice_by_value");
//        String slice_interval = jsonObject.getString("slice_interval");
//        String to = jsonObject.getString("to_date");
//        String fromDay;
//        if ("week".equalsIgnoreCase(unit)) {
//            fromDay = DateUtil.getLastDayOfWeek(to);
//        } else if ("month".equalsIgnoreCase(unit)) {
//            fromDay = DateUtil.getLastDayOfMonth(to);
//        } else {
//            fromDay = to;
//        }
//        return fromDay;
//    }
//
//    /**
//     * 初始行为事件和后续行为事件 的SQL组合  TODO week/month 的datediff 处理，调用自定义函数
//     *
//     * @param firstSQL
//     * @param secondSQL
//     * @param unit
//     * @return
//     */
//    private String genByDayJoinSQL(String firstSQL, String secondSQL, String unit) {
//
//        String joinFormat = " TL.global_user_id, TL.servertime AS first_day, TR.servertime AS second_day,%s AS by_day";
//        String joinSelect;
//        if ("week".equalsIgnoreCase(unit)) {
//            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)"); // todo
//        } else if ("month".equalsIgnoreCase(unit)) {
//            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)"); // todo
//        } else {
//            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)");
//        }
//        String format = "SELECT %s FROM (%s) TL LEFT JOIN (%s) TR ON TL.global_user_id = TR.global_user_id AND TL.servertime < TR.servertime ";
//        return String.format(format, joinSelect, firstSQL, secondSQL);
//    }
//
//    /**
//     * parquetTmpTab 和 usersTable 的join
//     *
//     * @param actionSql
//     * @param userSql
//     * @param productId
//     * @param isFirst
//     * @return
//     */
//    private String actionUserSqlJoiner(String actionSql, String userSql, String productId, boolean isFirst) {
//        String joinSQL;
//        String selectField = isFirst ? "ta.global_user_id, MIN(ta.servertime) AS servertime" : "ta.global_user_id, ta.servertime AS servertime";
//        String byField = isFirst ? String.format(" group by %s", "ta.global_user_id") : "";
//        if (null == userSql) {
//            String s1 = "select %s from  (%s) ta  %s";
//            joinSQL = String.format(s1, selectField, actionSql, byField);
//        } else {
//
//            String s1 = "select %s from  (%s) ta JOIN (%s) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk %s";
//            joinSQL = String.format(s1, selectField, actionSql, userSql, productId, byField);
//        }
//        return joinSQL;
//    }
//
//    /**
//     * 初始行为、 后续行为 action 表select sql 语句
//     * TODO : 非初始行为日期分组情况中，以初始行为事件属性/ 后续行为事件属性分组情况，select 字段需要增加分组字段
//     *
//     * @param actionCommWhere
//     * @param eventCon
//     * @return
//     */
//    private String genActionSQL(String actionCommWhere, JSONObject eventCon, String by_field) {
//        String eventName = eventCon.getString("event_name");
//        JSONObject filter = eventCon.getJSONObject("filter");
//        JSONArray conditions = filter.getJSONArray("conditions");
//        String relation = conditions.size() > 1 ? filter.getString("relation") : Constants.AND;
//
//        String category = SQLUtil.getCategory(eventName);
//        String commWhere = String.join(" AND ", actionCommWhere, String.format(" category = '%s'", category), String.format("action = '%s'", eventName));
//        String actionWhere = commWhere;
//        if (conditions.size() > 0) {
//            actionWhere = String.format("( %s ) AND (%s)", commWhere, SQLUtil.queryConditionOp(conditions, relation, "productId")._2().toString());// todo productId
//        }
//        // TODO : 非初始行为日期分组情况中，以初始行为事件属性/ 后续行为事件属性分组情况，select 字段需要增加分组字段
//        String selectField = (null == by_field || "first.event.day".equalsIgnoreCase(by_field) || by_field.startsWith("user")) ?
//                "global_user_id, servertime" :
//                String.join(",", "global_user_id", "servertime", by_field.substring(by_field.lastIndexOf(".") + 1));
//
//        String sqlFormat = "select %s from %s where %s ";
//        String actionSQL = String.format(sqlFormat, selectField, parquetTmpTable, actionWhere);
//        return actionSQL;
//    }
//
//    /**
//     * usersTable 表的SQL 拼接，
//     *
//     * @param userFilter
//     * @param productId
//     * @return
//     */
//    private Tuple3<String, Set<String>, Map<String, String>> genUserSQL(JSONObject userFilter,
//                                                                        String productId,
//                                                                        String byField,
//                                                                        Map<String, String> userPropertiesMap) {
//
//        String commWhere = String.format("pk > '%s' AND pk < '%s'", productId + "_", productId + "_a");
//
//        JSONArray conditions = userFilter.getJSONArray("conditions");
//        String relation = conditions.size() > 1 ? userFilter.getString("relation") : Constants.AND;
//
//        String userWhere = commWhere;
//        Set<String> groupIds = new HashSet<>();
//        Map<String, String> userProps = new HashMap<>();
//        if (conditions.size() > 0) {
//            // userWhere, actionWhere, groupId, actionFields, userProps
//            Tuple5 userConTuple = SQLUtil.queryConditionOp(conditions, relation, productId);
//            groupIds = (Set<String>) userConTuple._3();
//            userProps = (Map<String, String>) userConTuple._5();
//            userWhere = String.format("( %s ) AND ( %s )", commWhere, userConTuple._1());
//        }
//        String selectField = "pk";//(null != byField && byField.startsWith("user")) ? "pk," + String.join("_", productId, byField.split("\\.", 2)[1]) : "pk";// TODO : 非初始行为日期分组情况中，以用户属性作为分组条件时，select 字段需要增加分组字段
//        if (null != byField && byField.startsWith("user")) {
//            String userFiled = String.join("_", productId, byField.substring(byField.lastIndexOf(".") + 1));
//            selectField = String.join(",", "pk" + userFiled);
//            if (byField.startsWith("userGroup.")) {
//                groupIds.add(userFiled);
//            } else {
//                userProps.put(userFiled, userPropertiesMap.get(byField.split("\\.", 2)[1]));
//            }
//        }
//        String sqlFormat = "select %s from %s where %s";
//        String SQL = String.format(sqlFormat, selectField, usersTable, userWhere);
//        return new Tuple3(SQL, groupIds, userProps);
//    }
//
//    /**
//     * 查询结构处理 todo
//     *
//     * @param responseResult
//     * @param byVal
//     * @return
//     */
//    private JSONObject parseSqlQueryResult(JSONObject responseResult, String byVal) {
//        if (responseResult.isEmpty() ||
//                !responseResult.containsKey("result") ||
//                (responseResult.containsKey("result") && responseResult.getJSONArray("result").isEmpty())) {
//            return new JSONObject();
//        }
//
//        // country(china) byDay,
//        //2019801,<0/1/2/total,<people/percent,num>
//        Map<String, Map<String, Map<String, Set<String>>>> res = new HashMap<>();
//
//        JSONArray result = responseResult.getJSONArray("result");
//        result.forEach(v -> {
//            String[] split = v.toString().split(Constants.SEPARATOR_U0001);
//            String byValue = split[0];// 20190729
//            String byDay = ("null".equalsIgnoreCase(split[1])) ? "total" : split[1];
//            String globalids = split[2].replaceAll(" ", "");
//            Map<String, Map<String, Set<String>>> m = res.getOrDefault(byValue, new HashMap<>());
//
//            Set<String> userSet = new HashSet(Arrays.asList(globalids.substring(13, globalids.length() - 1).split(",")));
//
//            Map<String, Set<String>> map = m.getOrDefault(byValue, new HashMap<String, Set<String>>());
////            map
//
//
//        });
//        if ("first.event.day".equalsIgnoreCase(byVal)) {
//            // sort by first_day
//        } else {
//            // sort by zero day's users num
//        }
//
//
//        return responseResult;
//    }
//
//    //todo 用户属性及其类型
//    private Map<String, String> getUserPropertiesAndTypes(String productId) {
//        Map<String, String> map = new HashMap<>();
//        UserMetaType userMetaType2 = new UserMetaType();
//        userMetaType2.setProductId(Long.parseLong(productId));
//        //userMetaTypeMapper.getAllActiveMetaTypeListForFilter(userMetaType2).forEach(domain->map.put(domain.getType(),domain.getDatatype()) );
//        return map;
//    }
//
////    public JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine, String params, JobHistoryDao jobHistoryDao, JobServer jobServer, String jobName) throws IOException {
////        String jo2s = "{\"jobId\":\"6b9245c0-1f39-485a-8489-75a1e21742be\",\"result\":[\"2019-06-13\\u0001null\\u0001WrappedArray(677840, 755925, 744561)\",\"2019-06-15\\u0001null\\u0001WrappedArray(12035)\",\"2019-06-10\\u0001null\\u0001WrappedArray(54054, 76520, 36187)\",\"2019-06-12\\u0001null\\u0001WrappedArray(558652, 24494, 505066, 565563, 340700)\",\"2019-06-11\\u0001null\\u0001WrappedArray(19490)\",\"2019-06-15\\u0001null\\u0001WrappedArray(12035)\",\"2019-06-10\\u0001null\\u0001WrappedArray(54054, 76520, 36187)\",\"2019-06-11\\u0001null\\u0001WrappedArray(19490)\",\"2019-06-17\\u0001null\\u0001WrappedArray(1116533, 237451, 273788)\",\"2019-06-12\\u0001null\\u0001WrappedArray(558652, 24494, 505066, 565563, 340700)\",\"2019-06-17\\u0001null\\u0001WrappedArray(1116533, 237451, 273788)\",\"2019-06-13\\u00010\\u0001WrappedArray(497108, 745416)\",\"2019-06-13\\u0001null\\u0001WrappedArray(677840, 497108, 745416, 755925, 744561)\"]}";
////        JSONObject jo2 = JSONObject.parseObject(jo2s);
////        return jo2;
////    }
//
//
//    /**
//     * @return 返回初始行为和后续行为的起始时间
//     */
//    private Tuple2<String, String> getEventStartDay(JSONObject jsonObject) {
//
//        String unit = jsonObject.getString("unit"); // week / day / month
//        String from = jsonObject.getString("from_date");
//        String slice_by_value = jsonObject.getString("slice_by_value");
//        Integer slice_interval = Integer.parseInt(jsonObject.getString("slice_interval"));
//        String firstStartDay;
//        String secondStartDay;
//        boolean flag = "".equals(slice_by_value.replaceAll(" ", ""));
//        if ("week".equalsIgnoreCase(unit)) {
//            firstStartDay = flag ? DateUtil.getFirstDayOfCurrentWeek(from, "yyyyMMdd") : slice_by_value;
//            secondStartDay = flag ? firstStartDay : DateUtil.getLastDayOfWeek(DateUtil.stringDateDecrease(from, 7 * slice_interval)); // 加 duration * 7天后日期的周日
//        } else if ("month".equalsIgnoreCase(unit)) {
//            firstStartDay = flag ? DateUtil.getFirstDayOfMonth(from, "yyyyMMdd") : slice_by_value;
//            secondStartDay = flag ? firstStartDay : DateUtil.getLastDayOfMonth(DateUtil.dateAddMonth(from, slice_interval));
//        } else {
//            firstStartDay = flag ? from : slice_by_value;
//            secondStartDay = flag ? firstStartDay : DateUtil.stringDateDecrease(from, slice_interval);
//        }
//        return new Tuple2<>(firstStartDay, secondStartDay);
//    }
//
//
//    @Override
//    @Deprecated
//    public JSONArray getUserWeekRetain(JSONObject jsonObject) throws IOException {
//        String productId = jsonObject.getString(PRODUCT_ID);
//        String startDate = jsonObject.getString(START_DATE);
//        String endDate = jsonObject.getString(END_DATE);
//        String platform = "all".equals(jsonObject.getString(PLATFORM)) ? "null" : jsonObject.getString(PLATFORM);
//        String channel = "all".equals(jsonObject.getString(CHANNEL)) ? "null" : jsonObject.getString(CHANNEL);
//        String version = "all".equals(jsonObject.getString(VERSION)) ? "null" : jsonObject.getString(VERSION);
//
//        Table table = Connection2hbase.getTable(hbaseNameSpace + FIX_REPORT_RETENTION);
//
//        // 查询参数
//        Scan scan = new Scan();
//        scan.setCaching(Constants.SCANCACHING);
//        String starRow = productId + Constants.SEPARATOR + "week" + Constants.SEPARATOR + startDate;
//        String stopRow = productId + Constants.SEPARATOR + "week" + Constants.SEPARATOR + endDate;
//        scan.setStartRow(starRow.getBytes(Constants.CODE));
//        scan.setStopRow(stopRow.getBytes(Constants.CODE));
//
//        // qualifier
//        addColumnQualifier(scan);
//
//        // 过滤
//        String filter = ".*" + platform + Constants.SEPARATOR + channel + Constants.SEPARATOR + version + ".*";
//        scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(filter)));
//
//        ResultScanner scanner = table.getScanner(scan);
//        JSONArray retainList = new JSONArray();
//        for (Result result : scanner) {
//            String[] rowKey = new String(result.getRow(), Constants.CODE).split(Constants.SEPARATOR);
//            if (rowKey.length != 6) {
//                continue;
//            }
//            final String date = rowKey[2];
//            JSONObject retain = new JSONObject();
//
//            // 初始化周留存对象
//            initializationRetain(retain);
//            final String formatDate = LocalDateTimeUtil.changeDateFormat(LocalDateTimeUtil.getDateTimeMinusDay(date, 6)) + "~" + LocalDateTimeUtil.changeDateFormat(date);
//            for (Cell cell : result.rawCells()) {
//                retain.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
//            }
//            retain.put("date", formatDate);
//            retainList.add(retain);
//        }
//
//        // 关闭连接
//        Connection2hbase.closeTable(table, scanner);
//        LOG.debug("getUserWeekRetain result {}", retainList);
//
//        return retainList;
//    }
//
//    @Override
//    @Deprecated
//    public JSONArray getUserMonthRetain(JSONObject jsonObject) throws IOException {
//        String productId = jsonObject.getString(PRODUCT_ID);
//        String startDate = jsonObject.getString(START_DATE);
//        String endDate = jsonObject.getString(END_DATE);
//        String platform = "all".equals(jsonObject.getString(PLATFORM)) ? "null" : jsonObject.getString(PLATFORM);
//        String channel = "all".equals(jsonObject.getString(CHANNEL)) ? "null" : jsonObject.getString(CHANNEL);
//        String version = "all".equals(jsonObject.getString(VERSION)) ? "null" : jsonObject.getString(VERSION);
//
//        Table table = Connection2hbase.getTable(hbaseNameSpace + FIX_REPORT_RETENTION);
//
//        // 查询参数
//        Scan scan = new Scan();
//        scan.setCaching(Constants.SCANCACHING);
//        String starRow = productId + Constants.SEPARATOR + "month" + Constants.SEPARATOR + startDate;
//        String stopRow = productId + Constants.SEPARATOR + "month" + Constants.SEPARATOR + LocalDateTimeUtil.getDateTimePlus(endDate);
//        scan.setStartRow(starRow.getBytes(Constants.CODE));
//        scan.setStopRow(stopRow.getBytes(Constants.CODE));
//
//        // qualifier
//        addColumnQualifier(scan);
//
//        // 过滤
//        String filter = ".*" + platform + Constants.SEPARATOR + channel + Constants.SEPARATOR + version + ".*";
//        scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(filter)));
//
//        ResultScanner scanner = table.getScanner(scan);
//        JSONArray retainList = new JSONArray();
//        for (Result result : scanner) {
//            String[] rowKey = new String(result.getRow(), Constants.CODE).split(Constants.SEPARATOR);
//            if (rowKey.length != 6) {
//                continue;
//            }
//            final String date = rowKey[2];
//            JSONObject retain = new JSONObject();
//
//            // 初始化月留存对象
//            initializationRetain(retain);
//            for (Cell cell : result.rawCells()) {
//                retain.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
//            }
//            retain.put("date", LocalDateTimeUtil.changeDateFormat(date).substring(0, 7));
//            retainList.add(retain);
//        }
//
//        // 关闭连接
//        Connection2hbase.closeTable(table, scanner);
//        LOG.debug("getUserMonthRetain result {}", retainList);
//
//        return retainList;
//    }
//
//    /**
//     * 添加列限制
//     *
//     * @param scan 查询对象
//     */
//    private void addColumnQualifier(Scan scan) throws UnsupportedEncodingException {
//        scan.addColumn("f".getBytes(Constants.CODE), "0".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "1".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "2".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "3".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "4".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "5".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "6".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "7".getBytes(Constants.CODE));
//        scan.addColumn("f".getBytes(Constants.CODE), "8".getBytes(Constants.CODE));
//    }
//
//    /**
//     * 初始化留存对象
//     *
//     * @param retain 留存对象
//     */
//    private void initializationRetain(JSONObject retain) {
//        retain.put("0", "0");
//        retain.put("1", "");
//        retain.put("2", "");
//        retain.put("3", "");
//        retain.put("4", "");
//        retain.put("5", "");
//        retain.put("6", "");
//        retain.put("7", "");
//        retain.put("8", "");
//    }
//
//
//    /**
//     * 构造留存空列表
//     *
//     * @param unit
//     * @param beginDate
//     * @param endDate
//     * @param duration
//     * @return
//     */
//    private List<JSONObject> generateRetentionList(String unit, String beginDate, String endDate, int duration) {
//
//        unit = StringUtils.isBlank(unit) ? "day" : unit;
//        beginDate = StringUtils.isBlank(beginDate) ? "20190601" : beginDate;
//        endDate = StringUtils.isBlank(endDate) ? "20190611" : endDate;
//        duration = duration == 0 ? 1 : duration; //次日留存
//
//        List<JSONObject> resultList = new ArrayList<>();
//        if (StringUtils.equals("day", unit)) {
//            List<String> dayList = DateUtil.getDayOfRange(beginDate, endDate);
//            resultList = fillZeroForList(dayList, duration);
//            System.out.println(resultList);
//
//        } else if (StringUtils.equals("week", unit)) {
//            Set<String> mondaySet = DateUtil.getMondaySet(beginDate, endDate);
//            resultList = fillZeroForList(mondaySet, duration);
//            System.out.println(resultList);
//        } else if (StringUtils.equals("month", unit)) {
//            List<String> monthList = DateUtil.getMonthList(beginDate, endDate);
//            resultList = fillZeroForList(monthList, duration);
//            System.out.println(resultList);
//
//        }
//        return resultList;
//    }
//
//    /**
//     * 填充列表值为0
//     *
//     * @param dateList
//     * @param duration
//     * @return
//     */
//    private List<JSONObject> fillZeroForList(Collection<String> dateList, int duration) {
//        JSONObject jsonObject;
//        List<RetentionEntity> retentionEntityList;
//        RetentionEntity retentionEntity;
//        List<JSONObject> resultList = new ArrayList<>();
//        for (String date : dateList) {
//            jsonObject = new JSONObject();
//            jsonObject.put("by_value", date);
//            jsonObject.put("total_people", "0");
//            retentionEntityList = new ArrayList<>();
//            //构造空列表
//            for (int i = 0; i <= duration; i++) {
//                retentionEntity = new RetentionEntity(i + "", "0", "0");
//                retentionEntityList.add(retentionEntity);
//            }
//
//            JSONArray retentionArray = JSONArray.parseArray(JSON.toJSONString(retentionEntityList));
//            jsonObject.put("cells", retentionArray);
//            resultList.add(jsonObject);
//        }
//        return resultList;
//    }
//
//}
