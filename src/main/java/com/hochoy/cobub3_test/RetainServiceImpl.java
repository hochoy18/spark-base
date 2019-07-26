package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hochoy.cobub3_test.DateUtil.*;


public class RetainServiceImpl {

    String parquetTmpTable = "parquetTmpTable";
    String usersTable = "usersTable";
    private String hbaseNameSpace = "cobub3";

    public JSONObject getUserDayRetain(JSONObject jsonObject) throws IOException {

        String fromDay = getStartDay(jsonObject.getString("unit"), jsonObject.getString("from_date"));
        if (dateCompare2Now(fromDay) > 0)
            return new JSONObject();


        Tuple2<String, String> retentionSQL = genSQL(jsonObject);
        System.out.println(retentionSQL._1 + "\n" + retentionSQL._2);


        JSONObject responseResult = querySparkSql();


        JSONObject res = queryResultOp(responseResult);


        return res;
    }


    private Tuple2<String, String> genSQL(JSONObject jsonObject) {
        Tuple3<String, String, String> sql = genRetentionSql(jsonObject);

        String unit = "";
        String joinerSQL = genByDayJoinSQL(sql._1(), sql._2(), unit);

        String allSQLFormat = "SELECT %s FROM (%s) tc GROUP BY %s ";

        String selectFields = "from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') , tc.by_day, collect_set(tc.global_user_id) AS usersets";

        String groupBy = "from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd'), tc.by_day  GROUPING SETS((from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') ,tc.by_day), (from_unixtime(unix_timestamp(tc.first_day, 'yyyy-MM-dd'), 'yyyy-MM-dd') ))";

        String allSQL = String.format(allSQLFormat, selectFields, joinerSQL, groupBy);

        return new Tuple2(allSQL, sql._3());
    }


    private Tuple3<String, String, String> genRetentionSql(JSONObject jsonObject) {

        Boolean isExtend = jsonObject.getBoolean("extend_over_end_date");// false 初始行为，后续行为时间相同，true，后续行为需要加天周月的事件周期长度
        Integer duration = jsonObject.getInteger("duration"); // 8/7/12 ...
        String unit = jsonObject.getString("unit"); // week / day / month

        String productId = jsonObject.getString("productId");
        String from = jsonObject.getString("from_date");
        String to = jsonObject.getString("to_date");

        JSONObject firstEvent = jsonObject.getJSONObject("first_event");
        JSONObject secondEvent = jsonObject.getJSONObject("second_event");
        JSONObject userFilter = jsonObject.getJSONObject("user_filter");

        String fromDay = getStartDay(unit, from);
        String firstCommWhere = String.format(" productid = '%s' AND day >= '%s' AND  day <= '%s' ", productId, fromDay, getFirstEventEndDay(unit, from));
        String secondCommWhere = String.format(" productid = '%s' AND day >= '%s' AND  day <= '%s' ", productId, fromDay, getSecondEventEndDay(isExtend, duration, unit, to)); // todo week/month from 取值
        String by_field = jsonObject.containsKey("by_field") ? jsonObject.getString("by_field") : null;
        // first.event.country / second.event.city / user.sex / userGroup.group1
        String firstActionSQL = genActionSQL(firstCommWhere, firstEvent,by_field);
        String secondActionSQL = genActionSQL(secondCommWhere, secondEvent,by_field);

        /**
         * 判断是否 需要查 用户属性/用户分群，
         * 没有用户筛选条件  且  不包含 by_field字段(该字段只有在以非 初始行为时间分组时才有，） by_field 不以user或userGroup 分组
         */
        boolean hasUser = true;
        if (userFilter.isEmpty() &&
                (!jsonObject.containsKey("by_field") ||
                        (jsonObject.containsKey("by_field") && !jsonObject.getString("by_field").startsWith("user."))
                )) {
            hasUser = false;
        }
        Tuple3<String, Set<String>, Map<String, String>> userSQL = new Tuple3<>(null, null, null);
        String taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"{}\",groupid=\"{}\"";
        if (hasUser) {
            Map<String, String> userPropertiesMap = getUserPropertiesAndTypes(productId);
            userSQL = genUserSQL(userFilter, productId, by_field, userPropertiesMap);

            Map<String, String> props = userSQL._3();
            Set<String> groupIds = userSQL._2();
            StringJoiner prop = new StringJoiner(",", "{", "}");
            props.forEach((userPropName, userPropDataType) -> prop.add(String.format("\\\"%s\\\":\\\"%s\\\"", userPropName, userPropDataType)));
            taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"" + prop.toString() + "\",groupid=\"{" + groupIds.stream().collect(Collectors.joining(",")) + "}\"";
        }

        String firstJoinerSQL = actionUserSqlJoiner(firstActionSQL, userSQL._1(), productId, true);
        String secondJoinerSQL = actionUserSqlJoiner(secondActionSQL, userSQL._1(), productId, false);

        return new Tuple3(firstJoinerSQL, secondJoinerSQL, taskSql);

    }

    /**
     * 后续行为的结束日期：
     *
     * @param isExtend true : to + duration * unit  ; false : to/ to所在周/月的最后一天
     * @param duration 跨度：{7}天留存，{8}周留存 {12}月留存 ...
     * @param unit     留存类型：day/week/month
     * @param to       初始行为结束日期，以此及以上三个条件 计算后续行为的结束日期
     * @return
     */
    private String getSecondEventEndDay(Boolean isExtend, Integer duration, String unit, String to) {
        String endDay = to;
        String end;
        if (!isExtend) {
            switch (unit) {
                case "week":
                    end = getLastDayOfWeek(to);
                    endDay = dateCompare2Now(end) > 0 ? end : getLastDayOfWeek(dateFormat(new Date()));
                    break;
                case "month":
                    end = getLastDayOfMonth(to);
                    endDay = dateCompare2Now(end) > 0 ? end : getLastDayOfMonth(dateFormat(new Date()));
                    break;
                default:
                    endDay = to;
                    break;
            }
        } else
            switch (unit) {
                case "day":
                    end = stringDateDecrease(to, duration);
                    //dateCompare2Now:otherDate 跟今天日期对比，在今天之前则返回true，否则返回false
                    endDay = dateCompare2Now(end) > 0 ? end : dateFormat(new Date());
                    break;
                case "week":
                    end = getLastDayOfWeek(stringDateDecrease(to, 7 * duration)); // 加 duration * 7天后日期的周日
                    endDay = dateCompare2Now(end) > 0 ? end : getLastDayOfWeek(dateFormat(new Date()));
                    break;
                case "month":
                    end = getLastDayOfMonth(dateAddMonth(to, duration));
                    endDay = dateCompare2Now(end) > 0 ? end : getLastDayOfMonth(dateFormat(new Date()));
                    break;
            }
        return endDay;
    }


    private String getStartDay(String unit, String from) {
        String fromDay;
        if ("week".equalsIgnoreCase(unit)) {
            fromDay = getFirstDayOfCurrentWeek(from, "yyyyMMdd");
        } else if ("month".equalsIgnoreCase(unit)) {
            fromDay = getFirstDayOfMonth(from, "yyyyMMdd");
        } else {
            fromDay = from;
        }
        return fromDay;
    }

    private String getFirstEventEndDay(String unit, String from) {
        String fromDay;
        if ("week".equalsIgnoreCase(unit)) {
            fromDay = getLastDayOfWeek(from);
        } else if ("month".equalsIgnoreCase(unit)) {
            fromDay = getLastDayOfMonth(from);
        } else {
            fromDay = from;
        }
        return fromDay;
    }


    private String genByDayJoinSQL(String firstSQL, String secondSQL, String unit) {

        String joinFormat = " TL.global_user_id, TL.servertime AS first_day, TR.servertime AS second_day,%s AS by_day";
        String joinSelect;
        if ("week".equalsIgnoreCase(unit)) {
            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)"); // todo
        } else if ("month".equalsIgnoreCase(unit)) {
            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)"); // todo
        } else {
            joinSelect = String.format(joinFormat, "DATEDIFF(TR.servertime, TL.servertime)");
        }
        String format = "SELECT %s FROM (%s) TL LEFT JOIN (%s) TR ON TL.global_user_id = TR.global_user_id AND TL.servertime < TR.servertime ";
        return String.format(format, joinSelect, firstSQL, secondSQL);
    }


    private String actionUserSqlJoiner(String actionSql, String userSql, String productId, boolean isFirst) {
        String joinSQL;
        String selectField = isFirst ? "ta.global_user_id, MIN(ta.servertime) AS servertime" : "ta.global_user_id, ta.servertime AS servertime";
        String byField = isFirst ? String.format(" group by %s", "ta.global_user_id") : "";
        if (null == userSql) {
            String s1 = "select %s from  (%s) ta  %s";
            joinSQL = String.format(s1, selectField, actionSql, byField);
        } else {

            String s1 = "select %s from  (%s) ta JOIN (%s) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk %s";
            joinSQL = String.format(s1, selectField, actionSql, userSql, productId, byField);
        }
        return joinSQL;
    }

    /**
     * 初始行为、 后续行为 action 表select sql 语句
     * TODO : 非初始行为日期分组情况中，以初始行为事件属性/ 后续行为事件属性分组情况，select 字段需要增加分组字段
     *
     * @param actionCommWhere
     * @param eventCon
     * @return
     */
    private String genActionSQL(String actionCommWhere, JSONObject eventCon,String by_field) {
        String eventName = eventCon.getString("event_name");
        JSONObject filter = eventCon.getJSONObject("filter");
        JSONArray conditions = filter.getJSONArray("conditions");
        String relation = conditions.size() > 1 ? filter.getString("relation") : Constants.AND;

        String category = SQLUtil.getCategory(eventName);
        String commWhere = String.join(" AND ", actionCommWhere, String.format(" category = '%s'", category), String.format("action = '%s'", eventName));
        String actionWhere = commWhere;
        if (conditions.size() > 0) {
            actionWhere = String.format("( %s ) AND (%s)", commWhere, SQLUtil.queryConditionOp(conditions, relation, "productId")._2().toString());// todo productId
        }
        // TODO : 非初始行为日期分组情况中，以初始行为事件属性/ 后续行为事件属性分组情况，select 字段需要增加分组字段
        String selectField = "global_user_id, servertime";// null == by_field ?  "global_user_id, servertime" :"";

        String sqlFormat = "select %s from %s where %s ";
        String actionSQL = String.format(sqlFormat, selectField, parquetTmpTable, actionWhere);
        return actionSQL;
    }

    /**
     * usersTable 表的SQL 拼接，
     *
     * @param userFilter
     * @param productId
     * @return
     */
    private Tuple3<String, Set<String>, Map<String, String>> genUserSQL(JSONObject userFilter,
                                                                        String productId,
                                                                        String byField,
                                                                        Map<String, String> userPropertiesMap) {

        String commWhere = String.format("pk > '%s' AND pk < '%s'", productId + "_", productId + "_a");

        JSONArray conditions = userFilter.getJSONArray("conditions");
        String relation = conditions.size() > 1 ? userFilter.getString("relation") : Constants.AND;

        String userWhere = commWhere;
        Set<String> groupIds = new HashSet<>();
        Map<String, String> userProps = new HashMap<>();
        if (conditions.size() > 0) {
            // userWhere, actionWhere, groupId, actionFields, userProps
            Tuple5 userConTuple = SQLUtil.queryConditionOp(conditions, relation, productId);// todo productId

            groupIds = (Set<String>) userConTuple._3();
            userProps = (Map<String, String>) userConTuple._5();


            userWhere = String.format("( %s ) AND ( %s )", commWhere, userConTuple._1());// todo productId
        }


        String selectField = "pk";//(null != byField && byField.startsWith("user")) ? "pk," + String.join("_", productId, byField.split("\\.", 2)[1]) : "pk";// TODO : 非初始行为日期分组情况中，以用户属性作为分组条件时，select 字段需要增加分组字段
        if (null != byField && byField.startsWith("user")) {
            String userFiled = String.join("_", productId, byField.split("\\.", 2)[1]);
            selectField = String.join(",", "pk" + userFiled);
            if (byField.startsWith("userGroup.")) {
                groupIds.add(userFiled);
            } else {
                userProps.put(userFiled, userPropertiesMap.get(byField.split("\\.", 2)[1]));
            }
        }

        String sqlFormat = "select %s from %s where %s";

        String SQL = String.format(sqlFormat, selectField, usersTable, userWhere);
        return new Tuple3(SQL, groupIds, userProps);
    }


    public static void main(String[] args) throws IOException {
//        dateTest();
//        System.exit(-1);
        RetainServiceImpl retainService = new RetainServiceImpl();
        String input;
//        input = "{\"measures\":[],\"productId\":\"11148\",\"rangeText\":\"过去119天 - 过去140天\",\"from_date\":\"20190305\",\"to_date\":\"20190326\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"chartsType\":\"raw\",\"first_event\":{\"event_name\":\"SignInClick\",\"filter\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.country\",\"function\":\"equal\",\"params\":[\"中国\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"BandCardSetFinish\",\"filter\":{\"conditions\":[{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"iPhone8,2\",\"iPhone10,3#_#iPhone10,6\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.city\",\"function\":\"equal\",\"params\":[\"七台河\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"},\"relevance_field\":\"\"},\"user_filter\":{\"conditions\":[{\"type\":\"user.networkH\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"WIFI1#_#WIFI10#_#WIFI11#_#WIFI12\",\"WIFI5\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"},{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"or\"},\"is_wastage\":false,\"duration_true_name\":\"8\",\"show_zero_day\":\"\",\"request_id\":\"1563864936887:550544\",\"use_cache\":true}";

        // 有用户属性过滤
        input = "{\"measures\":[],\"productId\":\"11148\",\"rangeText\":\"过去119天 - 过去140天\",\"from_date\":\"20190611\",\"to_date\":\"20190617\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"chartsType\":\"raw\",\"first_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"iPhone10,3#_#iPhone10,6\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.is_new_device\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"3G\",\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"},\"relevance_field\":\"\"},\"user_filter\":{\"conditions\":[{\"type\":\"user.networkH\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"WIFI4\",\"WIFI5\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"},{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"or\"},\"is_wastage\":false,\"duration_true_name\":\"8\",\"show_zero_day\":\"\",\"request_id\":\"1563864936887:550544\",\"use_cache\":true}";

        // 无用户属性过滤
        input = "{\"measures\":[],\"productId\":\"11148\",\"rangeText\":\"过去119天 - 过去140天\",\"from_date\":\"20190611\",\"to_date\":\"20190617\",\"extend_over_end_date\":true,\"duration\":8,\"unit\":\"week\",\"chartsType\":\"raw\",\"first_event\":{\"event_name\":\"$launch\",\"filter\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"iPhone10,3#_#iPhone10,6\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"},\"relevance_field\":\"\"},\"second_event\":{\"event_name\":\"cf_aefzt\",\"filter\":{\"conditions\":[{\"type\":\"event.is_new_device\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"3G\",\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"},\"relevance_field\":\"\"},\"user_filter\":{},\"is_wastage\":false,\"duration_true_name\":\"8\",\"show_zero_day\":\"\",\"request_id\":\"1563864936887:550544\",\"use_cache\":true}";
        JSONObject jo = JSONObject.parseObject(input);
        retainService.getUserDayRetain(jo);


    }

    static void dateTest() {
        String date = "20190712";

        String firstDayOfMonth = getFirstDayOfMonth(date, "yyyyMMdd");
        System.out.println("getFirstDayOfMonth        20190701  --  " + firstDayOfMonth);
        String lastDayOfMonth = getLastDayOfMonth(date);
        System.out.println("getLastDayOfMonth         20190731  --  " + lastDayOfMonth);


        System.out.println("-----------------------------");
        String firstDayOfCurrentWeek = getFirstDayOfCurrentWeek(date, "yyyyMMdd");
        System.out.println("getFirstDayOfCurrentWeek  20190708  --  " + firstDayOfCurrentWeek);

        String lastDayOfWeek = getLastDayOfWeek(date);
        System.out.println("getLastDayOfWeek          20190714  --  " + lastDayOfWeek);

        String dateDecrease = stringDateDecrease(date, 30);
        System.out.println("dateDecrease.......     " + dateDecrease);

    }


    //todo 用户属性及其类型
    private Map<String, String> getUserPropertiesAndTypes(String productId) {
        Map<String, String> map = new HashMap<>();
        UserMetaType userMetaType2 = new UserMetaType();
        userMetaType2.setProductId(Long.parseLong(productId));
//        userMetaTypeMapper.getAllActiveMetaTypeListForFilter(userMetaType2).forEach(domain->map.put(domain.getType(),domain.getDatatype()) );
        return map;
    }

    private JSONObject queryResultOp(JSONObject responseResult) {
        return responseResult;
    }


    public JSONObject querySparkSql() {
        String jo2s = "{\"result\":[\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u00012\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u0001120\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00014\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u00014\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u000116\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u000132\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u00018\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u000115\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u000130\"],\"jobId\":\"tmp_actionreport_job_89d8bbca-51b5-43f5-bcfe-0cd128b177ab\"}";
        JSONObject jo2 = JSONObject.parseObject(jo2s);
        return jo2;
    }


}

