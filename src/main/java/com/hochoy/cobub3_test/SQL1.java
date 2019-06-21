
//package com.hochoy.cobub3_test;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.commons.lang3.StringUtils;
//import scala.Tuple2;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * Describe:
// *
// * @author hochoy <hochoy18@sina.com>
// * @version V1.0.0
// * @date 2019/6/17
// */
//public class SQL1 {
//
//    private String hbaseNameSpace = "cobub3";
//
//    public static void main(String[] args) throws IOException {
//        Long start = System.currentTimeMillis();
//        new SQL1().getQueryResult();
//        Long end = System.currentTimeMillis();
//        System.out.println((end - start));
//    }
//
//
//    // 拼接group by 字段和select 字段
//
//
//    /**
//     * @param actions 多指标 ，
//     *                每个指标的帅选条件:子查询语句where ， 可能有 event,user,userGroup 类型
//     *                条件间的关系: and / or
//     *                指标类型 :(count(action), count(userid),count(distinct(userid)))
//     * @param filters : 主查询语句的筛选条件： where (可能有 event,user,userGroup 类型
//     * @param fields  ：最后的group by 字段
//     * @return
//     */
//    private List<String> sqlSplice(JSONArray actions, JSONObject filters, JSONArray fields) {
//
//
//        StringJoiner outGroupByUser = new StringJoiner(", "); // user group by 字段
//        StringJoiner outGroupByAction = new StringJoiner(", "); //  action group by 字段
//        StringJoiner outSelectFieldUser = new StringJoiner(", "); //  user select  字段
//        StringJoiner outSelectFieldAction = new StringJoiner(", "); // action select  字段
//
//        // filter 过滤条件
//        String outRelation = filters.getString("relation"); // 主查询 and / or // todo or 处理 判断 outWhereUser 是否是空 (outRelation == "or" and !outWhereUser.toString().isEmpty()) 不能下推
//        StringJoiner outWhereAction = new StringJoiner(String.format(" %s ", outRelation)); // action where 条件
//        StringJoiner outWhereUser = new StringJoiner(String.format(" %s ", outRelation));   // user where 条件
//
//
//        // select 字段来源于 by_field  action 的 eventOriginal 及eventType
//        // group by 字段来源于 by_field
//        // where 条件字段来源于 ：action 的childFilterParam 和filter
//        class GroupAndSelectFiels {
//            /**
//             *  group by 和 select 字段
//             * @param field
//             */
//            void by_field_Op(String field) {
//                String[] split = field.split("\\.", 2);
//                if ("event".equals(split[0])) {
//                    //parquetTmpTable 表
//                    outGroupByAction.add(split[1]);//  group by 字段拼接
//                    outSelectFieldAction.add(split[1]); // select 字段拼接
//
//                } else if ("user".equals(split[0])) {
//                    //usersTable  表的用户属性
//                    outGroupByUser.add(split[1]);// group by 字段拼接
//                    outSelectFieldUser.add(split[1]);// select 字段拼接
//
//                    // todo 动态 schema 获取和拼接
//                } else if ("userGroup".equals(split[0])) {
//                    //usersTable  表的用户分群属性处理
//                    outGroupByUser.add(split[1]);// group by 字段拼接
//                    outSelectFieldUser.add(split[1]);// select 字段拼接
//                    // todo 动态 schema 获取和拼接
//                }
//            }
//
//
//            /**
//             * String :
//             * equal: in parquetTmpTable
//             * notEqual： not in
//             * contain： like
//             * notContain : xxx is not null  and xxx not like
//             *
//             * Number:
//             * equal : usersTable ->      (duration is not null ) AND (duration = 200 or duration = 500 )
//             * notEqual: usersTable ->    (duration is not null ) AND (duration <> 100 and duration <> 200 and duration <> 300 )
//             * more : (duration is not null ) AND (duration>100)
//             * less :  (duration is not null ) AND (duration<100)
//             * region :(duration is not null ) AND ( duration>=100) AND(duration<=200)
//             *
//             * @param filter
//             * @return Tuple2(eventWhere, userWhere)
//             */
//            Tuple2<StringJoiner, StringJoiner> filter_op(String filter) {
//                JSONObject jo = JSONObject.parseObject(filter);
//                String type = jo.getString("type");
//                String function = jo.getString("function");
//                JSONArray params = jo.getJSONArray("params");
//                Tuple2<StringJoiner, StringJoiner> outWhere = whereFunctionOp(type, function, params, outWhereAction, outWhereUser);
//                return outWhere;
//
//
//
//            }
//
//            // 返回product 下各个字段 及其对应的 数据类型
//            Map<String, String> getMetaTypeMap() {
//                Map<String, String> metaTypes = new HashMap<String, String>();
//                return metaTypes;
//            }
//
//
//            private StringJoiner eventWhereFunctionOp(String type, String function, JSONArray params, StringJoiner action) {
//                String[] split = type.split("\\.", 2);
//                String condition;
//                StringJoiner eventWhere = action;
//                switch (function) {
//                    case "equal": { // string  and  Number
//                        StringJoiner joiner = new StringJoiner(", ", "(", ")");
//                        params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                        eventWhere.add(String.format("(%s IN  %s)", split[1], joiner.toString()));
//                        break;
//                    }
//                    case "notEqual": {  // string  and  Number
//                        StringJoiner joiner = new StringJoiner(", ", "(", ")");
//                        params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                        eventWhere.add(String.format("(%s NOT IN  %s)", split[1], joiner.toString()));// IN  v.s NOT IN
//                        break;
//                    }
//                    case "contain": { //string
//                        String param = params.getString(0);
//                        eventWhere.add(String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", split[1], split[1], param));// LIKE  v.s    LIKE
//                        break;
//                    }
//
//                    case "notContain": { //string
//                        String param = params.getString(0);
//                        eventWhere.add(String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", split[1], split[1], param));// LIKE  v.s NOT LIKE
//                        break;
//                    }
//                    case "more": {  // Number >
//                        String param = params.getString(0);
//                        condition = String.format("%s  >  %s", split[1], param);
//                        eventWhere.add(condition);
//                        break;
//                    }
//                    case "less": { // Number <
//                        String param = params.getString(0);
//                        condition = String.format("%s  <  %s", split[1], param);
//                        eventWhere.add(condition);
//                        break;
//                    }
//                    case "region": {  // Number  between ：  >= and <=
//                        String param1 = params.getString(0);
//                        String param2 = params.getString(0);
//                        condition = String.format("(%s  >=  %s AND  %s  <=  %s)", split[1], param1, split[1], param2);
//                        eventWhere.add(condition);
//                        break;
//                    }
//
//                    // todo boolean
//                }
//                return eventWhere;
//            }
//
//
//            private StringJoiner userWhereFunctionOp(String type, String function, JSONArray params, StringJoiner whereJoin) {
//                String[] split = type.split("\\.", 2);
//                String condition;
//                StringJoiner joiner1 = whereJoin;
//                switch (function) {
//                    // String:
//                    case "equal": { // string  and todo Number
//                        StringJoiner joiner = new StringJoiner(" or ", "(", ")");
//                        params.forEach(x -> joiner.add(String.format("  %s = '%s'", split[1], x)));
//
//                        joiner1.add(String.format("(%s IS NOT NULL ) AND  %s", split[1], joiner.toString()));
//                        break;
//                    }
//                    case "notEqual": {  // string  and  todo Number
//                        StringJoiner joiner = new StringJoiner(" AND ", "(", ")");  // and v.s and  or
//                        params.forEach(x -> joiner.add(String.format("  %s <> '%s'", split[1], x)));
//                        joiner1.add(String.format("(%s IS NOT NULL ) AND  %s ", split[1], joiner.toString()));
//                        break;
//                    }
//                    case "contain": { //string like
//                        String param = params.getString(0);
//                        joiner1.add(String.format("(%s IS NOT NULL ) AND ( %s LIKE  '%s') ", split[1], split[1], param));// LIKE  v.s    LIKE
//                        break;
//                    }
//                    case "notContain": { //string
//                        String param = params.getString(0);
//                        joiner1.add(String.format("(%s IS NOT NULL ) AND (%s NOT LIKE  '%s') ", split[1], split[1], param));//NOT LIKE    v.s NOT LIKE
//                        break;
//                    }
//
//                    case "more": {  // Number >
//                        String param = params.getString(0);
//                        condition = String.format("%s  >  %s", split[1], param);
//                        joiner1.add(condition);
//                        break;
//                    }
//                    case "less": { // Number <
//                        String param = params.getString(0);
//                        condition = String.format("%s  <  %s", split[1], param);
//                        joiner1.add(condition);
//                        break;
//                    }
//                    case "region": {  // Number  between ：  >= and <=
//                        String param1 = params.getString(0);
//                        String param2 = params.getString(0);
//                        condition = String.format("(%s  >=  %s AND  %s  <=  %s)", split[1], param1, split[1], param2);
//                        joiner1.add(condition);
//                        break;
//                    }
//                    // todo boolean 类型处理
//
//
//                }
//                return joiner1;
//            }
//
//            // where
//
//            /**
//             *
//             * @param type
//             * @param function
//             * @param params
//             * @param actionWhereJoin
//             * @param userWhereJoin
//             * @return 二元组 :第一个元素  eventWhere, 第一个元素  userWhere
//             */
//            private Tuple2<StringJoiner, StringJoiner> whereFunctionOp(String type, String function, JSONArray params, StringJoiner actionWhereJoin, StringJoiner userWhereJoin) {
//                String[] split = type.split("\\.", 2); //event.country
//                StringJoiner eventWhere = actionWhereJoin;
//                StringJoiner userWhere = userWhereJoin;
//
//                switch (split[0]) {
//                    case "event": {
//                        eventWhere = eventWhereFunctionOp(type, function, params, eventWhere);
//                        break;
//                    }
//                    case "user": {
//                        userWhere = userWhereFunctionOp(type, function, params, userWhere);
//                        break;
//                    }
//                    case "userGroup": {
//                        String param = "true";//params.getString(0);
//                        userWhere = userWhere.add(String.format("(%s IS NOT NULL ) AND (%s = %s) ", split[1],split[1], param));// todo true or false op
//                        break;
//                    }
//                }
//                return new Tuple2<>(eventWhere, userWhere);
//
//            }
//
//
//            void actionsOp(String action, char p, int ii) {
//                JSONObject actionO = JSONObject.parseObject(action);
//
//                String eventOriginal = actionO.getString("eventOriginal");
//                String eventType = actionO.getString("eventType");
//                JSONObject childFilter = actionO.getJSONObject("childFilterParam");
//                String relation = childFilter.getString("relation");
//                JSONArray conditions = childFilter.getJSONArray("conditions");
//
//                StringJoiner inSelectFieldUser = new StringJoiner(", "); //  user select  字段
//                StringJoiner inSelectFieldAction = new StringJoiner(", "); // action select  字段
//                StringJoiner inWhereAction = new StringJoiner(String.format(" %s ", relation)); // action where 条件
//                StringJoiner inWhereUser = new StringJoiner(String.format(" %s ", relation));
//                // 需要处理 or 逻辑
//                conditions.forEach(x -> {
//                    JSONObject jo = JSONObject.parseObject(x.toString());
//                    String type = jo.getString("type");
//                    String function = jo.getString("function");
//                    JSONArray params = jo.getJSONArray("params");
//                    Tuple2<StringJoiner, StringJoiner> inWhereOfActionAndUser = whereFunctionOp(type, function, params, inWhereAction, inWhereUser);
//
//                });
//
//
////                p = (char) (p + 1);
////                ii = ii + 1;
//
//            }
//
//            private String index(String eventType) {
//
//                String s = "";
//                switch (eventType) {
//                    case "acc":
//                        s = "count(action)";
//                        break;
//                    case "userid":
//                        s = "count(distinct deviceid)";
//                        break;
//                    case "loginUser":
//                        s = "count(distinct userid) ";
//                        break;
//                    default:
//                        break;
//                }
//                return s;
//
//            }
//
//            /**
//             *
//             * @param op event/user/userGroup
//             * @param type column which in table, e.g: country,network ...
//             * @param params : value of type
//             */
//           /* private void commWhere_Op(String op, String type, JSONArray params, String condition) {
////                String.format("%s IN  %s", type, joiner.toString())
//                switch (op) {
//                    case "event": {
//                        StringJoiner joiner = new StringJoiner(", ", "(", ")");
//                        params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                        outWhereAction.add(condition);// IN  v.s NOT IN
//                        break;
//                    }
//                    case "user": {
//                        // (network is not null ) AND (network <> 'wifi002' and network <> 'wifi003' and network <> 'wifi004' and network <> 'wifi005' )
//                        StringJoiner joiner = new StringJoiner(" or ", "(", ")");  // and v.s and  or
//                        params.forEach(x -> joiner.add(String.format("  %s = '%s'", type, x)));
//                        outWhereUser.add(String.format("%s IS NOT NULL ", op)).add(joiner.toString());
//                        break;
//                    }
//                    case "userGroup": {
//                        outWhereUser.add(String.format("%s IS NOT NULL ", type)).add(String.format("(%s = %s)", type, ""));// todo true or false op
//                        break;
//                    }
//                }
//            }*/
//
//            // 数字类型 数据的 where 条件处理 ,event 和 user 处理方式
//            // more : >
//            // less : <
//            // region: xxx > = and xxx< =
//           /* private void numWhere_Op(String op, String condition) {
//                switch (op) {
//                    case "event": {
//                        outWhereAction.add(condition); // todo 数组越界
//                        break;
//                    }
//                    case "user": {
//                        outWhereUser.add(String.format("%s IS NOT NULL ", op)).add(condition);// todo 数组越界
//                        break;
//                    }
//                }
//            }*/
//
//        }
//
//        GroupAndSelectFiels groupAndSelectFiels = new GroupAndSelectFiels();
//
//        // 根据分组字段拼接 需要【 查询的字段和分组的字段 】
//        fields.forEach(x -> groupAndSelectFiels.by_field_Op(x.toString()));
//
////        StringJoiner outGroupByUser = new StringJoiner(", ");
//        // 根据主查询条件拼接 parquetTmpTable 和 usersTable 的查询条件
//
//        JSONArray outConditions = filters.getJSONArray("conditions");
//
//        // 根据 主查询条件 filters 拼接 【 主查询的 where 条件 】
////        outConditions.forEach(x -> groupAndSelectFiels.filter_op(String.valueOf(x)));
//        List<Tuple2<StringJoiner, StringJoiner>> outWhere = outConditions.stream().map(x -> groupAndSelectFiels.filter_op(String.valueOf(x))).collect(Collectors.toList());
//
//
////        groupAndSelectFiels.whereFunctionOp();
//
//        char parquetAsName = 'A';
//        int i = 0;
//        actions.forEach(x -> groupAndSelectFiels.actionsOp(String.valueOf(x), parquetAsName, i));
//
//
//        return null;
//    }
//
//    public JSONObject getQueryResult() throws IOException {
//        String jsonMessage = "{ \"action\": [{ \"eventOriginal\": \"$AppStart\", \"eventType\": \"loginUser\", \"childFilterParam\": { \"conditions\": [{ \"type\": \"event.$AppStart.$country\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"中国\", \"美国\"] }, { \"type\": \"event.$AppStart.$wifi\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }, { \"type\": \"user.age\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"11\", \"22\"] }, { \"type\": \"userGroup.fenqun27\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }], \"relation\": \"or\" } }, { \"eventOriginal\": \"RegistFinish\", \"eventType\": \"acc\", \"childFilterParam\": { \"conditions\": [{ \"type\": \"user.$utm_source\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"36kr\", \"今日头条\", \"地推\"] }, { \"type\": \"userGroup.benyueqianzaitouziyonghu\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }], \"relation\": \"and\" } }], \"unit\": \"day\", \"filter\": { \"relation\": \"or\", \"conditions\": [{ \"type\": \"user.channel\", \"function\": \"equal\", \"isRegion\": \"isFalse\", \"params\": [\"Baidu\", \"Offline\"] }, { \"type\": \"userGroup.fenxijieguo8\", \"function\": \"isFalse\", \"isRegion\": \"isFalse\", \"params\": [] }, { \"type\": \"user.Province\", \"function\": \"equal\", \"isRegion\": \"isFalse\", \"params\": [\"北京市\", \"上海市\"] }] }, \"by_fields\": [\"user.Province\", \"userGroup.fenxijieguo8\", \"event.$Anything.$country\"], \"from_date\": \"2019-05-20\", \"to_date\": \"2019-05-26\", \"productId\": \"11001\" }";
//        JSONObject jsonObject = JSONObject.parseObject(jsonMessage);
//
//        String productId = jsonObject.getString(Constants.PRODUCTID);
//        String unit = jsonObject.getString(Constants.UNIT);
//        String fromDate = jsonObject.getString(Constants.FROM_DATE);
//        String toDate = jsonObject.getString(Constants.TO_DATE);
//        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
//        String relation = filters.getString(Constants.RELATION);
//        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
//        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);
//
//
//        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标
//        sqlSplice(actions, filters, fields);
//
////        System.exit(-1);
////        if (conditions.size() <= 1) {
////            relation = Constants.AND;
////        }
////
////
////
////
////
////
////
////
////
////
////
////
////        /*----------------------------------多指标拼接sql开始----------------------------------------------------------*/
////        boolean outIsEvent = false;//外部条件是否含event属性
////        boolean outItsUser = false;//外部条件是否含user属性
////        boolean innerIsEvent = false;//内部条件是否含event属性
////        boolean innerItsUser = false;//内部条件是否含user属性
////        boolean isFieldEvent = false;//分组中是否含event属性
////        boolean isFieldUser = false;//分组条件是否含user属性
////
////        ArrayList<String> propList = new ArrayList<>();
////        ArrayList<String> groupidList = new ArrayList<>();
////
////
////        SQLUtil.handleFieldType(fields, isFieldEvent, isFieldUser);
////        SQLUtil.handleWhereType(conditions, outIsEvent, outItsUser);
////        // 拼要查询的字段的SQL
////        StringBuilder sqlQuery = new StringBuilder();//总查询sql
////        StringBuilder sqlSingle = new StringBuilder();//单指标sql
////
////        for (int i = 0; i < actions.size(); i++) {
////            JSONObject action = actions.getJSONObject(i);
////            JSONObject childParams = action.getJSONObject(Constants.CHILDFILTERPARAM);//单指标筛选条件
////            final String childRelation = jsonObject.getString(Constants.RELATION);
////            //SQLUtil.handleWhereType(childParams, innerIsEvent, innerItsUser);
////
////            if (!isFieldUser && !outItsUser && !innerItsUser) { // 分组、内部条件、外部条件中只有事件属性, 只查询parquetTmpTable表
////
////
////            } else if (Constants.OR.equals(childRelation) && innerIsEvent && innerItsUser
////                    && Constants.OR.equals(relation) && outIsEvent && outItsUser) {//内部条件和外部条件均放在join表外面
////
////            } else if (Constants.OR.equals(relation) && outIsEvent && outItsUser
////                    && (!(Constants.OR.equals(childRelation) && innerIsEvent && innerItsUser))) {//外部条件or连接,并且同时含有事件属性和用户属性，外部条件放在join表外面。内部where条件可以先限制表再join表
////
////            } else if (Constants.OR.equals(childRelation) && innerIsEvent && innerItsUser
////                    && (!(Constants.OR.equals(relation) && outIsEvent && outItsUser))) {//内部条件or连接,并且同时含有事件属性和用户属性，把内部条件放在join表外面。外部where条件先限制表再join表
////
////            } else {//其他情况
////
////            }
////        }
////
////
////
////
////
////
////
////
////
////
////
////        /*----------------------------------多指标拼接sql结束----------------------------------------------------------*/
////
////
////        StringBuilder sqlForEvent = new StringBuilder();//事件属性语句
////        StringBuilder sqlBuilder = new StringBuilder();
////        StringBuilder sqlForUser = new StringBuilder();//用户属性语句
////        StringBuilder outField = new StringBuilder();//用户外部查询字段
////
////        StringBuilder groupid = new StringBuilder();//用户分群
////        StringBuilder prop = new StringBuilder();//用户属性
////
////
////        StringBuilder sqlWhereForEvent = new StringBuilder();//or关系where条件
////        StringBuilder sqlWhereForUser = new StringBuilder();///or关系where条件
////        List<String> whereEventFields = new ArrayList<>();
////        List<String> whereUserFields = new ArrayList<>();
////
////        final int size = conditions.size();
////
////        boolean isEvent = false;//where条件是否是event条件
////        boolean isUser = false;//where条件是否是user条件
////        for (int i = 0; i < size; i++) {
////            JSONObject condition = conditions.getJSONObject(i);
////            String type = condition.getString("type");
////            if (type.startsWith("event.")) {//事件属性
////                isEvent = true;
////            } else if (type.startsWith("user.")) {//用户属性
////                isUser = true;
////            } else if (type.startsWith("userGroup.")) {//用户分群
////                isUser = true;
////            }
////        }
////
////
////        for (int j = 0; j < fields.size(); j++) {
////            if ("all".equals(fields.getString(j))) {
////                continue;
////            }
////            if (fields.getString(j).startsWith("event.")) {//事件属性
////                isFieldEvent = true;
////            } else if (fields.getString(j).startsWith("user.")) {//用户属性
////                isFieldUser = true;
////            } else if (fields.getString(j).startsWith("userGroup.")) {//用户属性
////                isFieldUser = true;
////            }
////        }
////        String taskSql = "";
////        if (!isFieldUser && !isUser) {//只有事件属性
////            SQLUtil.pieceOnlyActionField(sqlBuilder, fields, actions, unit);
////            sqlBuilder.append(" and ").append("productid='").append(productId).append("'");
////
////            // 过滤条件
////            SQLUtil.pieceOnlyActionWhere(sqlBuilder, conditions, relation);
////
////            if (sqlBuilder.toString().trim().endsWith("and")) {
////                sqlBuilder.append("  day >= ").append(fromDate);
////                sqlBuilder.append(" and day <= ").append(toDate);
////            } else {
////                sqlBuilder.append(" and day >= ").append(fromDate);
////                sqlBuilder.append(" and day <= ").append(toDate);
////            }
////
////
////            // 分组
////            SQLUtil.pieceActionGroupBy(sqlBuilder, fields, unit);
////
////            // 查询总计
////            pieceActionTotalGroupSet(unit, fields, sqlBuilder);
////
////            // 排序，如果多个事件会出错
////            for (int i = 0; i < actions.size(); i++) {
////                JSONObject action = actions.getJSONObject(i);
////                String eventType = action.getString(Constants.EVENT_TYPE);
////                switch (eventType) {
////                    case "acc":
////                        sqlBuilder.append(" order by count(action) desc ");
////                        break;
////                    case "userid":
////                        sqlBuilder.append(" order by count(distinct deviceid) desc ");
////                        break;
////                    case "loginUser":
////                        sqlBuilder.append(" order by count(distinct userid) desc ");
////                        break;
////                    default:
////                        break;
////                }
////            }
////
////            taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"{}\",groupid=\"{}\"";
////
////        } else {
////            if ("or".equals(relation) && isEvent && isUser) {
////                SQLUtil.pieceSqlWhereOr(sqlWhereForEvent, sqlWhereForUser, groupidList, propList, conditions, relation, productId, whereEventFields, whereUserFields);
////                pieceSqlOr(sqlBuilder, sqlForEvent, sqlForUser, outField, groupidList, propList, fields, actions, unit, productId, whereEventFields, whereUserFields);
////            } else {
////                pieceSql(sqlBuilder, sqlForEvent, sqlForUser, outField, groupidList, propList, fields, actions, unit, productId);
////            }
////
////            sqlForEvent.append("productid='").append(productId).append("')");
////
////            // 时间范围
////            sqlForEvent.append(" AND  (day >= '").append(fromDate);
////            sqlForEvent.append("' AND  day <= '").append(toDate).append("')");
////
////            sqlForEvent.append(" AND  (category  = '");
////
////            // 如果多个事件要改造，现在只支持单个事件
////            for (int i = 0; i < actions.size(); i++) {
////                JSONObject action = actions.getJSONObject(i);
////                String eventOriginal = action.getString("eventOriginal");
////                String eventType = action.getString("eventType");
////                String category = SQLUtil.getCategory(eventOriginal);
////                sqlForEvent.append(category).append("') AND (action='");
////                sqlForEvent.append(eventOriginal).append("')");
////            }
////
////            //事件属性子查询
////            if (sqlForUser.toString().endsWith(",")) {
////                sqlForUser.delete(sqlForUser.lastIndexOf(","), sqlForUser.length());
////            }
////            sqlForUser.append(" FROM usersTable WHERE (pk >'").append(productId).append("_' AND pk < '").append(productId).append("_a') AND ");
////
////            // 过滤条件
////            if ("or".equals(relation) && isEvent && isUser) {
////                if (sqlForUser.toString().trim().endsWith("and")) {
////                    sqlForUser.delete(sqlForUser.lastIndexOf("and"), sqlForUser.length());
////                }
////                if (sqlForUser.toString().trim().endsWith("AND")) {
////                    sqlForUser.delete(sqlForUser.lastIndexOf("AND"), sqlForUser.length());
////                }
////                if (sqlForEvent.toString().trim().endsWith("and")) {
////                    sqlForEvent.delete(sqlForEvent.lastIndexOf("and"), sqlForEvent.length());
////                }
////
////                sqlBuilder.append(sqlForEvent).append(")e1 JOIN (").append(sqlForUser).append(")e2 ");
////                sqlBuilder.append("ON concat_ws('_', '").append(productId).append("', e1.global_user_id) = e2.pk");
////
////                sqlWhereForEvent.delete(sqlWhereForEvent.lastIndexOf(")"), sqlWhereForEvent.length());
////                sqlWhereForUser.delete(sqlWhereForUser.lastIndexOf(")"), sqlWhereForUser.length());
////
////                if (StringUtils.isNotBlank(sqlWhereForEvent) && StringUtils.isNotBlank(sqlWhereForUser)) {
////                    sqlBuilder.append(" where ").append(sqlWhereForEvent).append(" or ").append(sqlWhereForUser);
////                } else if (StringUtils.isNotBlank(sqlWhereForEvent)) {
////                    sqlBuilder.append(" where ").append(sqlWhereForEvent);
////                } else if (StringUtils.isNotBlank(sqlWhereForUser)) {
////                    sqlBuilder.append(" where ").append(sqlWhereForUser);
////                }
////            } else {
////                SQLUtil.pieceSqlWhere(sqlForEvent, sqlForUser, groupidList, propList, conditions, relation, productId);
////
////                if (sqlForUser.toString().trim().endsWith("and")) {
////                    sqlForUser.delete(sqlForUser.lastIndexOf("and"), sqlForUser.length());
////                }
////                if (sqlForUser.toString().trim().endsWith("AND")) {
////                    sqlForUser.delete(sqlForUser.lastIndexOf("AND"), sqlForUser.length());
////                }
////                if (sqlForEvent.toString().trim().endsWith("and")) {
////                    sqlForEvent.delete(sqlForEvent.lastIndexOf("and"), sqlForEvent.length());
////                }
////
////                sqlBuilder.append(sqlForEvent).append(")e1 JOIN (").append(sqlForUser).append(")e2 ");
////                sqlBuilder.append("ON concat_ws('_', '").append(productId).append("', e1.global_user_id) = e2.pk");
////            }
////
////
////            boolean notAll = false;
////            for (int i = 0; i < fields.size(); i++) {
////                if ("all".equals(fields.getString(i))) {
////                    continue;
////                }
////                notAll = true;
////            }
////            if (notAll) {
////                // 分组
////                sqlBuilder.append(" group by ").append(outField).append(unit);
////
////                // 查询总计
////                sqlBuilder.append(" grouping sets((").append(outField).append(unit).append("),(");
////                if (sqlBuilder.toString().endsWith(",")) {
////                    sqlBuilder.delete(sqlBuilder.lastIndexOf(","), sqlBuilder.length());
////                }
////                if (outField.toString().endsWith(",")) {
////                    outField.delete(outField.lastIndexOf(","), outField.length());
////                }
////                sqlBuilder.append(outField);
////                sqlBuilder.append(")) ");
////            } else {
////                // 分组
////                sqlBuilder.append(" group by ").append(unit);
////                // 查询总计
////                sqlBuilder.append(" grouping sets((").append(unit).append("),())");
////            }
////
////
////            // 排序，如果多个事件会出错
////            for (int i = 0; i < actions.size(); i++) {
////                JSONObject action = actions.getJSONObject(i);
////                String eventType = action.getString(Constants.EVENT_TYPE);
////                switch (eventType) {
////                    case "acc":
////                        sqlBuilder.append(" order by count(action) desc ");
////                        break;
////                    case "userid":
////                        sqlBuilder.append(" order by count(distinct deviceid) desc ");
////                        break;
////                    case "loginUser":
////                        sqlBuilder.append(" order by count(distinct userid) desc ");
////                        break;
////                    default:
////                        break;
////                }
////            }
////
////            prop.append("{");
////            if (propList != null && propList.size() > 0) {
////                for (String str : propList) {
////                    UserMetaType userMetaType = null;// userMetadataService.getUserMetaType(str,Long.valueOf(productId));
////                    if (userMetaType != null) {
////                        prop.append("\\\"");
////                        prop.append(str).append("\\\":\\\"").append(userMetaType.getDatatype()).append("\\\",");
////                    }
////                }
////                prop.delete(prop.lastIndexOf(","), prop.length());
////            }
////
////            prop.append("}");
////
////            if (groupidList != null && groupidList.size() > 0) {
////                groupid.append("{");
////                for (int i = 0; i < groupidList.size(); i++) {
////                    groupid.append(groupidList.get(i)).append(",");
////                }
////                groupid.delete(groupid.lastIndexOf(","), groupid.length());
////                groupid.append("}");
////            } else {
////                groupid.append("{}");
////            }
////            taskSql = "\",namespace=\"" + hbaseNameSpace + "\",prop=\"" + prop + "\",groupid=\"" + groupid + "\"";
////        }
////
////
//////        logger.debug("task's sql: {}", sqlBuilder);
////        // sql查询
////        JSONObject responseResult = querySparkSql(sqlBuilder, "1000", taskSql);
////
////        if (responseResult == null || (responseResult.containsKey("status") && responseResult.getString("status").equalsIgnoreCase("error"))) {
////            return new JSONObject();
////        }
////
////        JSONArray jsonArray = responseResult.getJSONArray("result");
////
////
////        // 返回action集合
////        JSONObject result = new JSONObject();
////        handleActionResult(actions, result);
////
////        // 对于单个分组，直接返回，带总体的多个分组，去除总体
////        String fristFiled = fields.getString(0);
////        if (fristFiled.startsWith("event.")) {
////            fristFiled = fristFiled.substring(6);
////        } else if (fristFiled.startsWith("user.")) {
////            fristFiled = fristFiled.substring(5);
////        } else if (fristFiled.startsWith("userGroup.")) {
////            fristFiled = fristFiled.substring(10);
////        }
////
////        if (fields.size() == 1 && "all".equals(fristFiled)) {
////            result.put(Constants.BY_FIELDS, fields);
////        } else {
////            fields.remove("all");
////
////            JSONArray fieldArray = new JSONArray();
////            for (int i = 0; i < fields.size(); i++) {
////                if (fields.getString(i).startsWith("event.")) {//事件属性
////                    String field = fields.getString(i).substring(6);
////                    fieldArray.add(field);
////                } else if (fields.getString(i).startsWith("user.")) {//用户属性
////                    String field = fields.getString(i).substring(5);
////                    fieldArray.add(field);
////                } else if (fields.getString(i).startsWith("userGroup.")) {//用户属性
////                    String field = fields.getString(i).substring(10);
////                    fieldArray.add(field);
////                }
////            }
////            result.put(Constants.BY_FIELDS, fieldArray);
////        }
//
//        // 查询结果为空
//        /*if (jsonArray.isEmpty()) {
//            handleResultIsEmpty(result);
//        } else {
//
//            // 返回日期集合
//            JSONArray dates = LocalDateTimeUtil.getPeriodDateList(fromDate, toDate);
//            result.put("date", dates);
//
//            // 保存分组累计的值
//            JSONArray totalJsonArray = new JSONArray();
//            // 保存分组每天的值
//            JSONArray dateJsonArray = new JSONArray();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                String json = jsonArray.getString(i);
//                String[] split = json.split(Constants.SEPARATOR);
//                final int length = split.length;
//                final String jsonDate = split[length - 1 - actions.size()];
//                if ("null".equals(jsonDate)) {
//                    totalJsonArray.add(json);
//                } else {
//                    dateJsonArray.add(json);
//                }
//            }
//
//            // 把json数组变成 Map
//            Map<String, Map<String, int[]>> resultMap = handleJsonArrayToMap(fields, actions, dateJsonArray);
//
//            // total的map
//            Map<String, Integer> totalResultMap = handleTotalJsonArrayToMap(fields, totalJsonArray);
//
//            // 把Map转成Json数组
//            JSONArray values = handleMapToJsonArray(fromDate, toDate, actions, dates, resultMap, totalResultMap);
//
//            // 限制返回个数
//            limitReturnResultCount(result, values);
//
//            // 修改页面显示的值
//            modifyOriginalToDisplay(productId, fields, result);
//
//        }*/
//
//        return null;
//    }
//
//    /**
//     * 拼凑子查询字段
//     *
//     * @param sqlForEvent 事件属性语句
//     * @param sqlForUser  用户属性语句
//     * @param outField    用户外部查询字段
//     * @param fields      字段，即分组
//     * @param actions     事件
//     * @param unit        聚合单位
//     */
//    private void pieceSql(StringBuilder sqlBuilder, StringBuilder sqlForEvent, StringBuilder sqlForUser,
//                          StringBuilder outField, List<String> groupidList, List<String> propList, JSONArray fields,
//                          JSONArray actions, String unit, String productId) {
//        // 如果多个事件会出错
//        for (int i = 0; i < actions.size(); i++) {
//            JSONObject action = actions.getJSONObject(i);
//            String eventType = action.getString(Constants.EVENT_TYPE);
//            switch (eventType) {
//                case "acc":
//                    sqlForEvent.append("SELECT global_user_id,");
//                    break;
//                case "userid":
//                    sqlForEvent.append("SELECT deviceid,global_user_id,");
//                    break;
//                case "loginUser":
//                    sqlForEvent.append("SELECT userid,global_user_id,");
//                    break;
//                default:
//                    break;
//            }
//        }
//        sqlForUser.append("SELECT pk ,");
//        sqlBuilder.append("SELECT ");
//        for (int i = 0; i < fields.size(); i++) {
//            if ("all".equals(fields.getString(i))) {
//                continue;
//            }
//            if (fields.getString(i).startsWith("event.")) {//事件属性
//                String event = fields.getString(i).substring(6);
//                MetaType metaType = null;// metaTypeMapper.getMetaTypeDisplay(event);
//                if ("BooleanType".equalsIgnoreCase(metaType.getDatatype())) {
//                    sqlForEvent.append(" CASE ").append(event).append(" WHEN true THEN '真' ELSE '假' END AS ").append(event).append(",");
//                } else {
//                    sqlForEvent.append(event).append(",");
//                }
//                outField.append("e1.").append(event).append(",");
//            } else if (fields.getString(i).startsWith("user.")) {//用户属性
//                String metaType = fields.getString(i).substring(5);
//                UserMetaType userMetaType = new UserMetaType();
//                userMetaType.setType(metaType);
//                userMetaType.setProductId(Long.valueOf(productId));
//                UserMetaType userMetaTypeQuery = null;//userMetaTypeMapper.getUserMetaType(userMetaType);
//
//                if ("boolean".equalsIgnoreCase(userMetaTypeQuery.getDatatype())) {
//                    sqlForUser.append(" CASE ").append(metaType).append(" WHEN true THEN '真' ELSE '假' END AS ").append(metaType).append(",");
//                } else {
//                    sqlForUser.append(metaType).append(",");
//                }
//                outField.append("e2.").append(metaType).append(",");
//                propList.add(metaType);
//            } else if (fields.getString(i).startsWith("userGroup.")) {//用户属性
//                String userGroup = fields.getString(i).substring(10);
//                sqlForUser.append(" CASE ").append(productId).append("_").append(userGroup).append(" WHEN true THEN '真' ELSE '假' END  AS ").append(productId).append("_").append(userGroup).append(",");
//                outField.append("e2.").append(productId).append("_").append(userGroup).append(",");
//                if (!groupidList.contains(productId + "_" + userGroup)) {
//                    groupidList.add(productId + "_" + userGroup);
//                }
//
//            }
//        }
//        sqlBuilder.append(outField);
//
//
//        if (StringUtils.isNotEmpty(unit)) {
//            sqlForEvent.append(unit).append(",action FROM parquetTmpTable WHERE(");
//            sqlBuilder.append(unit);
//        } else {
//            sqlForEvent.append(" action FROM parquetTmpTable WHERE(");
//        }
//
//        if (!outField.toString().endsWith(",")) {
//            outField.append(",");
//        }
//        if (!sqlBuilder.toString().endsWith(",")) {
//            sqlBuilder.append(",");
//        }
//
//
//        // 如果多个事件要改造，现在只支持单个事件
//        for (int i = 0; i < actions.size(); i++) {
//            JSONObject action = actions.getJSONObject(i);
//            String eventOriginal = action.getString("eventOriginal");
//            String eventType = action.getString("eventType");
//
//            // 分别为总次数、触发用户数、登录用户数
//            switch (eventType) {
//                case "acc":
//                    sqlBuilder.append("count(action) FROM (");
//                    break;
//                case "userid":
//                    sqlBuilder.append("count(distinct deviceid) FROM (");
//                    break;
//                case "loginUser":
//                    sqlBuilder.append("count(distinct userid) FROM (");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private void handleActionResult(JSONArray actions, JSONObject result) {
//        JSONArray eventNames = new JSONArray();
//        for (int i = 0; i < actions.size(); i++) {
//            JSONObject actionResult = actions.getJSONObject(i);
//            String eventOriginalResult = actionResult.getString("eventOriginal");
//            String eventTypeResult = actionResult.getString(Constants.EVENT_TYPE);
//            eventNames.add(eventOriginalResult + Constants.JOINER + eventTypeResult);
//        }
//        result.put("eventName", eventNames);
//    }
//
//    /**
//     * 事件分析实时查询
//     *
//     * @param sqlBuilder sql语句
//     * @param maxLine    返回最大记录数
//     * @return 结果
//     * @throws IOException 异常
//     */
//    private JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine, String params) throws IOException {
//        String jobId = "tmp_actionreport_job_" + UUID.randomUUID().toString();
////        CobubJobHistory cobubJobHistory = new CobubJobHistory();
////        cobubJobHistory.setJobId(jobId);
////        cobubJobHistory.setTaskId("");
////        cobubJobHistory.setSqlDetail(sqlBuilder.toString());
////        jobHistoryDao.logJobHistory(cobubJobHistory);
////
////        // 查询
////        HttpEntity entity = CobubHttpClient.getInstance()
////                .doPost(String.format(jobServer.startJobUrl(), "cobub-session-context"),
////                        "jobId=" + jobId + ",maxLine=" + maxLine + ",sql = \" " + sqlBuilder.toString() +params+ " "); //一开始别加refreshtable=true, 文件数量大的情况比较耗时
////        String httpR = EntityUtils.toString(entity, Consts.UTF_8);
//
//        JSONObject responseResult = null;
//
////        try {
////            responseResult = JSONObject.parseObject(httpR);
////        } catch (Exception e) {
////            logger.error("some exception", e);
////        }
////
////        if (responseResult != null && responseResult.containsKey("status") && "ERROR".equalsIgnoreCase(responseResult.getString("status"))   //FileNotFoundException说明parquetTmpTable表的文件被合并，找不到了，需要刷新表
////                && responseResult.getJSONObject("result").getString("message").contains("java.io.FileNotFoundException")) {
////            logger.info(".... need to refresh table ");
////            entity = CobubHttpClient.getInstance()
////                    .doPost(String.format(jobServer.startJobUrl(), "cobub-session-context"),
////                            "refreshtable=true,maxLine=" + maxLine + ",jobId=" + jobId + ",sql = \" " + sqlBuilder.toString() + " \"");
////            httpR = EntityUtils.toString(entity, Consts.UTF_8);
////            try {
////                responseResult = JSONObject.parseObject(httpR);
////            } catch (Exception e) {
////                logger.error("some exception", e);
////            }
////        }
//        return responseResult;
//    }
//
//    private void pieceSqlOr(StringBuilder sqlBuilder, StringBuilder sqlForEvent, StringBuilder sqlForUser,
//                            StringBuilder outField, List<String> groupidList, List<String> propList, JSONArray fields,
//                            JSONArray actions, String unit, String productId, List<String> whereEventFields, List<String> whereUserFields) {
//        sqlForEvent.append("SELECT ");
//
//
//        // 如果多个事件会出错
//        for (int i = 0; i < actions.size(); i++) {
//            JSONObject action = actions.getJSONObject(i);
//            String eventType = action.getString(Constants.EVENT_TYPE);
//            switch (eventType) {
//                case "acc":
//                    sqlForEvent.append("global_user_id,");
//                    break;
//                case "userid":
//                    sqlForEvent.append("deviceid,global_user_id,");
//                    break;
//                case "loginUser":
//                    sqlForEvent.append("userid,global_user_id,");
//                    break;
//                default:
//                    break;
//            }
//        }
//        sqlForUser.append("SELECT pk ,");
//        sqlBuilder.append("SELECT ");
//
//        for (int i = 0; i < fields.size(); i++) {
//            if ("all".equals(fields.getString(i))) {
//                continue;
//            }
//            if (fields.getString(i).startsWith("event.")) {//事件属性
//                String event = fields.getString(i).substring(6);
//                if (whereEventFields.contains(event)) {
//                    whereEventFields.remove(event);
//                }
//
//                MetaType metaType = null;//metaTypeMapper.getMetaTypeDisplay(event);
//                if ("BooleanType".equalsIgnoreCase(metaType.getDatatype())) {
//                    sqlForEvent.append(" CASE ").append(event).append(" WHEN true THEN '真' ELSE '假' END AS ").append(event).append(",");
//                } else {
//                    sqlForEvent.append(event).append(",");
//                }
//                outField.append("e1.").append(event).append(",");
//            } else if (fields.getString(i).startsWith("user.")) {//用户属性
//                String metaType = fields.getString(i).substring(5);
//                if (whereUserFields.contains(productId + "_" + metaType)) {
//                    whereUserFields.remove(productId + "_" + metaType);
//                }
//                if (whereUserFields.contains(metaType)) {
//                    whereUserFields.remove(metaType);
//                }
//
//                UserMetaType userMetaType = new UserMetaType();
//                userMetaType.setType(metaType);
//                userMetaType.setProductId(Long.valueOf(productId));
//                UserMetaType userMetaTypeQuery = null;// userMetaTypeMapper.getUserMetaType(userMetaType);
//
//                if ("boolean".equalsIgnoreCase(userMetaTypeQuery.getDatatype())) {
//                    sqlForUser.append(" CASE ").append(metaType).append(" WHEN true THEN '真' ELSE '假' END AS ").append(metaType).append(",");
//                } else {
//                    sqlForUser.append(metaType).append(",");
//                }
//                outField.append("e2.").append(metaType).append(",");
//
//                if (!propList.contains(metaType)) {
//                    propList.add(metaType);
//                }
//            } else if (fields.getString(i).startsWith("userGroup.")) {//用户属性
//                String userGroup = fields.getString(i).substring(10);
//                if (whereUserFields.contains(productId + "_" + userGroup)) {
//                    whereUserFields.remove(productId + "_" + userGroup);
//                }
//                sqlForUser.append(" CASE ").append(productId).append("_").append(userGroup).append(" WHEN true THEN '真' ELSE '假' END  AS ").append(productId).append("_").append(userGroup).append(",");
//                outField.append("e2.").append(productId).append("_").append(userGroup).append(",");
//                if (!groupidList.contains(productId + "_" + userGroup)) {
//                    groupidList.add(productId + "_" + userGroup);
//                }
//
//            }
//        }
//
//        if (whereEventFields != null && !whereEventFields.isEmpty()) {
//            for (String str : whereEventFields) {
//                sqlForEvent.append(str).append(",");
//            }
//        }
//
//        if (whereUserFields != null && !whereUserFields.isEmpty()) {
//            for (String str : whereUserFields) {
//                sqlForUser.append(str).append(",");
//            }
//        }
//
//        sqlBuilder.append(outField);
//
//
//        if (StringUtils.isNotEmpty(unit)) {
//            sqlForEvent.append(unit).append(",action FROM parquetTmpTable WHERE(");
//            sqlBuilder.append(unit);
//        } else {
//            sqlForEvent.append(unit).append(" action FROM parquetTmpTable WHERE(");
//        }
//
//        if (!outField.toString().endsWith(",")) {
//            outField.append(",");
//        }
//        if (!sqlBuilder.toString().endsWith(",")) {
//            sqlBuilder.append(",");
//        }
//
//
//        // 如果多个事件要改造，现在只支持单个事件
//        for (int i = 0; i < actions.size(); i++) {
//            JSONObject action = actions.getJSONObject(i);
//            String eventOriginal = action.getString("eventOriginal");
//            String eventType = action.getString("eventType");
//
//            // 分别为总次数、触发用户数、登录用户数
//            switch (eventType) {
//                case "acc":
//                    sqlBuilder.append("count(action) FROM (");
//                    break;
//                case "userid":
//                    sqlBuilder.append("count(distinct deviceid) FROM (");
//                    break;
//                case "loginUser":
//                    sqlBuilder.append("count(distinct userid) FROM (");
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//
//    private void modifyOriginalToDisplay(String productId, JSONArray fields, JSONObject result) {
//        // 把每个分组的original和display组成map
//        List<Map<String, String>> mapList = new ArrayList<>();
//        for (int i = 0; i < fields.size(); i++) {
//            if (fields.getString(i).startsWith("event.") || "all".equals(fields.getString(i))) {
//                Metadata metadata = new Metadata();
//                metadata.setProductid(productId);
//                if (!"all".equals(fields.getString(i))) {
//                    metadata.setType(fields.getString(i).substring(6));
//                } else {
//                    metadata.setType(fields.getString(i));
//                }
//                List<Metadata> metadataList = null;// metadataMapper.getActiveMetadataValue(metadata);
//
//                Map<String, String> map = new HashMap<>();
//                for (Metadata aMetadataList : metadataList) {
//                    map.put(aMetadataList.getOriginal(), aMetadataList.getDisplay());
//                }
//                mapList.add(map);
//            } else if (fields.getString(i).startsWith("user.")) {//用户属性
//                UserMetadata metadata = new UserMetadata();
//                metadata.setProductid(productId);
//                metadata.setType(fields.getString(i).substring(5));
//                List<UserMetadata> metadataList = null;//userMtadataMapper.getActiveMetadataValue(metadata);
//                Map<String, String> map = new HashMap<>();
//                for (UserMetadata aMetadataList : metadataList) {
//                    map.put(aMetadataList.getOriginal(), aMetadataList.getDisplay());
//                }
//                mapList.add(map);
//            } else if (fields.getString(i).startsWith("userGroup.")) {//用户属性
//                Map<String, String> map = new HashMap<>();
//                map.put("ture", "真");
//                map.put("false", "假");
//                mapList.add(map);
//            }
//        }
//
//        Map<String, int[][]> countMap = jsonArrayToMap(result, mapList);
//        Map<String, Integer> totalCountMap = totalJsonArrayToMap(result, mapList);
//        totalCountMap = CommonUtil.mapIntValueSort(totalCountMap);
//
//        JSONArray displayValue = new JSONArray();
//        for (Map.Entry<String, Integer> totalCount : totalCountMap.entrySet()) {
//            JSONObject display = new JSONObject();
//            display.put(Constants.BY_VALUES, totalCount.getKey().split(Constants.ACTION_RESULT_SEPARATOR));
//            display.put(Constants.COUNT, countMap.get(totalCount.getKey()));
//            display.put("totalCount", totalCount.getValue());
//            displayValue.add(display);
//        }
//        result.put("value", displayValue);
//    }
//
//    private void pieceActionTotalGroupSet(String unit, JSONArray fields, StringBuilder sqlBuilder) {
//        sqlBuilder.append(" grouping sets((");
//        for (int i = 0; i < fields.size(); i++) {
//            if ("all".equals(fields.getString(i))) {
//                continue;
//            }
//            sqlBuilder.append(fields.getString(i).substring(6)).append(",");
//        }
//        sqlBuilder.append(unit).append(")");
//
//        sqlBuilder.append(",(");
//        if (fields.size() != 1 || !"all".equals(fields.getString(0))) {
//            for (int i = 0; i < fields.size(); i++) {
//                if ("all".equals(fields.getString(i))) {
//                    continue;
//                }
//                sqlBuilder.append(fields.getString(i).substring(6)).append(",");
//            }
//            sqlBuilder.delete(sqlBuilder.lastIndexOf(","), sqlBuilder.length());
//        }
//
//        sqlBuilder.append(")) ");
//    }
//
//    private Map<String, int[][]> jsonArrayToMap(JSONObject result, List<Map<String, String>> mapList) {
//        // 修改返回值，根据map修改分组的值
//        JSONArray array = result.getJSONArray(Constants.ACTION_REPORT_VALUE);
//        result.put("originalValue", array);
//        Map<String, int[][]> countMap = new HashMap<>();
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject original = array.getJSONObject(i);
//            JSONArray byValues = original.getJSONArray(Constants.BY_VALUES);
//            for (int j = 0; j < byValues.size(); j++) {
//                final String valuesString = byValues.getString(j);
//                if ("null".equals(valuesString)) {
//                    byValues.set(j, "其他");
//                } else {
//                    final String element = mapList.get(j).get(valuesString);
//                    if (element != null) {
//                        byValues.set(j, element);
//                    }
//                }
//            }
//
//            String join = String.join(Constants.ACTION_RESULT_SEPARATOR, byValues.toJavaList(String.class));
//            int[][] oldCount = original.getObject(Constants.COUNT, int[][].class);
//
//            // 取消同一个引用
//            int[][] temp = oldCount.clone();
//            for (int j = 0; j < temp.length; j++) {
//                temp[j] = temp[j].clone();
//            }
//
//            if (countMap.containsKey(join)) {
//                int[][] newCount = countMap.get(join);
//                CommonUtil.arrayCorrespondingValueAdd(temp, newCount);
//            } else {
//                countMap.put(join, temp);
//            }
//        }
//        return countMap;
//    }
//
//    private Map<String, Integer> totalJsonArrayToMap(JSONObject result, List<Map<String, String>> mapList) {
//        // 修改返回值，根据map修改分组的值
//        JSONArray array = result.getJSONArray(Constants.ACTION_REPORT_VALUE);
//        Map<String, Integer> totalCountMap = new HashMap<>();
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject original = array.getJSONObject(i);
//            JSONArray byValues = original.getJSONArray(Constants.BY_VALUES);
//            for (int j = 0; j < byValues.size(); j++) {
//                final String valuesString = byValues.getString(j);
//                if ("null".equals(valuesString)) {
//                    byValues.set(j, "其他");
//                } else {
//                    final String element = mapList.get(j).get(valuesString);
//                    if (element != null) {
//                        byValues.set(j, element);
//                    }
//                }
//            }
//            String join = String.join(Constants.ACTION_RESULT_SEPARATOR, byValues.toJavaList(String.class));
//            Integer oldCount = original.getInteger("totalCount");
//
//            if (totalCountMap.containsKey(join)) {
//                Integer newCount = totalCountMap.get(join);
//                totalCountMap.put(join, oldCount + newCount);
//            } else {
//                totalCountMap.put(join, oldCount);
//            }
//        }
//        return totalCountMap;
//    }
//
//}
//
//
//
//











/*

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import scala.Tuple2;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

class SQL1{
    */
/**
     * @param actions 多指标 ，
     *                每个指标的筛选条件:子查询语句where ， 可能有 event,user,userGroup 类型
     *                条件间的关系: and / or
     *                指标类型 :(count(action), count(userid),count(distinct(userid)))
     * @param filters : 主查询语句的筛选条件： where (可能有 event,user,userGroup 类型
     * @return
     *//*

    private List<String> sqlSplice(JSONArray actions, JSONObject filters) {


        // filter 过滤条件
        String outRelation = filters.getString("relation"); // 主查询 and / or // todo or 处理 判断 outWhereUser 是否是空 (outRelation == "or" and !outWhereUser.toString().isEmpty()) 不能下推
        StringJoiner outWhereAction = new StringJoiner(String.format(" %s ", outRelation)); // action where 条件
        StringJoiner outWhereUser = new StringJoiner(String.format(" %s ", outRelation));   // user where 条件


        // select 字段来源于 by_field  action 的 eventOriginal 及eventType
        // group by 字段来源于 by_field
        // where 条件字段来源于 ：action 的childFilterParam 和filter
        class GroupAndSelectFiels {


            */
/**
             * String :
             * equal: in parquetTmpTable
             * notEqual： not in
             * contain： like
             * notContain : xxx is not null  and xxx not like
             *
             * Number:
             * equal : usersTable ->      (duration is not null ) AND (duration = 200 or duration = 500 )
             * notEqual: usersTable ->    (duration is not null ) AND (duration <> 100 and duration <> 200 and duration <> 300 )
             * more : (duration is not null ) AND (duration>100)
             * less :  (duration is not null ) AND (duration<100)
             * region :(duration is not null ) AND ( duration>=100) AND(duration<=200)
             *
             * @param filter
             * @return Tuple2(eventWhere, userWhere)
             *//*

            Tuple2<StringJoiner, StringJoiner> filter_op(String filter) {
                JSONObject jo = JSONObject.parseObject(filter);
                String type = jo.getString("type");
                String function = jo.getString("function");
                JSONArray params = jo.getJSONArray("params");
                Tuple2<StringJoiner, StringJoiner> outWhere = whereFunctionOp(type, function, params, outWhereAction, outWhereUser);
                return outWhere;
            }

            private StringJoiner eventWhereFunctionOp(String type, String function, JSONArray params, StringJoiner action) {
                String[] split = type.split("\\.", 2);
                String condition;
                StringJoiner eventWhere = action;
                return eventWhere;
            }


            private StringJoiner userWhereFunctionOp(String type, String function, JSONArray params, StringJoiner whereJoin) {
                String[] split = type.split("\\.", 2);
                String condition;
                StringJoiner joiner1 = whereJoin;
                return joiner1;
            }

            // where

            */
/**
             *
             * @param type
             * @param function
             * @param params
             * @param actionWhereJoin
             * @param userWhereJoin
             * @return 二元组 :第一个元素  eventWhere, 第一个元素  userWhere
             *//*

            private Tuple2<StringJoiner, StringJoiner> whereFunctionOp(String type, String function, JSONArray params, StringJoiner actionWhereJoin, StringJoiner userWhereJoin) {
                String[] split = type.split("\\.", 2); //event.country
                StringJoiner eventWhere = actionWhereJoin;
                StringJoiner userWhere = userWhereJoin;

                switch (split[0]) {
                    case "event": {
                        eventWhere = eventWhereFunctionOp(type, function, params, eventWhere);
                        break;
                    }
                    case "user": {
                        userWhere = userWhereFunctionOp(type, function, params, userWhere);
                        break;
                    }
                    case "userGroup": {
                        String param = "true";//params.getString(0);
                        userWhere = userWhere.add(String.format("(%s IS NOT NULL ) AND (%s = %s) ", split[1], split[1], param));// todo true or false op
                        break;
                    }
                }
                return new Tuple2<>(eventWhere, userWhere);

            }


        }

        GroupAndSelectFiels groupAndSelectFiels = new GroupAndSelectFiels();


        // 根据主查询条件拼接 parquetTmpTable 和 usersTable 的查询条件

        JSONArray outConditions = filters.getJSONArray("conditions");

        // 根据 主查询条件 filters 拼接 【 主查询的 where 条件 】
//        outConditions.forEach(x -> groupAndSelectFiels.filter_op(String.valueOf(x)));
        List<Tuple2<StringJoiner, StringJoiner>> outWhere = outConditions.stream().map(x -> groupAndSelectFiels.filter_op(String.valueOf(x))).collect(Collectors.toList());


//        groupAndSelectFiels.whereFunctionOp();

        char parquetAsName = 'A';
        int i = 0;
//        actions.forEach(x -> groupAndSelectFiels.actionsOp(String.valueOf(x), parquetAsName, i));


        return null;
    }
}*/
