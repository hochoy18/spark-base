//package com.hochoy.cobub3_test;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
////import com.cobub.analytics.web.entity.*;
////import com.cobub.analytics.web.mapper.*;
////import com.cobub.analytics.web.service.CustomActionService;
////import com.cobub.analytics.web.service.UserMetadataService;
////import com.cobub.analytics.web.util.*;
////import com.cobub.analytics.web.util.http.CobubHttpClient;
////import com.cobub.analytics.web.util.http.JobServer;
//import com.google.common.base.Joiner;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.Table;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.http.Consts;
//import org.apache.http.HttpEntity;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
//import scala.Tuple2;
//import scala.Tuple3;
//import scala.Tuple5;
//import scala.Tuple6;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 自定义事件（事件分析）
// */
////@Service
//public class CustomActionServiceImpl   {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomActionServiceImpl.class);
//    public static final String AND = " and ";
//    public static final String OR = " or ";
//    public static final String EQUAL = " = ";
//    public static final String BOOLEANTYPE = "BooleanType";
//    public static final String EQUAL_TRUE = " = true ";
//    public static final String EQUAL_FALSE = " = false ";
//    public static final String LEFT_BRACKET = "(";
//    public static final String UNDER_LINE = "_";
//    public static final String SINGLE_QUOTE = "'";
//    public static final String EQUAL_SINGLE_QUOTE = " = '";
//    public static final String DB_PARQUET = " e1.";//表parquetTmpTable别名
//    public static final String DB_USER = " e2.";//表usersTable别名
//    String separator = "@@@@@@@";
////    @Autowired
////    private JobServer jobServer;
////
////    @Value("${spark.query.maxLine}")
//    private String maxLine = "1000";
//
////    @Value("${spark.export.query.maxLine}")
//    private String exportMaxLine;
//
////    @Value("${hbasenamespace}")
//    private String hbaseNameSpace = "cobub3";
//
////    @Autowired
////    private UserMetadataService userMetadataService;
////
////    @Autowired
////    private ActionReportMapper actionReportMapper;
////
////    @Autowired
////    private ReportIndividMapper reportIndividMapper;
////
////    @Autowired
////    private MetadataMapper metadataMapper;
////
////    @Autowired
////    private MetaTypeMapper metaTypeMapper;
////
////    @Autowired
////    private UserMetadataMapper userMtadataMapper;
////
////
////    @Autowired
////    private UserMetaTypeMapper userMetaTypeMapper;
////
////    @Autowired
////    private JobHistoryDao jobHistoryDao;
////
////    @Override
//    public JSONObject getQueryResult(JSONObject jsonObject) throws IOException {
//        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
//        String relation = filters.getString("relation"); // 主查询条件 逻辑关系 and / or
//        String unit = jsonObject.getString(Constants.UNIT);
//        String from = jsonObject.getString(Constants.FROM_DATE);
//        String to = jsonObject.getString(Constants.TO_DATE);
//        String productId = jsonObject.getString(Constants.PRODUCTID);
//        if (!"day".equalsIgnoreCase(unit)){
//            return new JSONObject();
//        }
//
//        StringJoiner commWhere = new StringJoiner(" AND ");
////
//        String dateCon = String.format("( productid = '%s' AND day >= '%s' AND  day <= '%s' )", productId, from, to);
//        commWhere.add(dateCon);
//        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标
//
//
////        Tuple2 filter = new Tuple2(queryCondition._1(), queryCondition._2());
//
//
//        Map map = new HashMap();
//        map.put("relation", relation);
//        map.put("commWhere", commWhere);
//        map.put("productId", productId);
//
//        Tuple2 queryOrSaveOp = queryOrSaveOp(jsonObject);
//        /**
//         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
//         */
//        Tuple3<String,String,String> sqlOps = actionToSingleIndicatorSQL(actions, (Tuple6)queryOrSaveOp._1, (Tuple5) queryOrSaveOp._2, map,true);
//
//        JSONObject responseResult = new JSONObject();//querySparkSql(new StringBuilder(sqlOps._1()), maxLine,sqlOps._2());
//
//        if (responseResult.isEmpty()||responseResult == null || (responseResult.containsKey("status") && responseResult.getString("status").equalsIgnoreCase("error"))) {
//            return new JSONObject();
//        }
//        System.out.println("responseResult.....................\n"+responseResult);
//
//        JSONArray jsonArray = responseResult.getJSONArray("result");
//        System.out.println("jsonArray........................    \n"+jsonArray);
//
//        Tuple3<JSONArray, JSONArray, JSONArray> commReturn = commRetrunOp(jsonObject);
//
//        JSONObject result = resultOp(responseResult, commReturn);
//
//
//
//        return result;
//    }
//
//    private Tuple2<Tuple6,Tuple5>  queryOrSaveOp(JSONObject jsonObject){
//        String productId = jsonObject.getString(Constants.PRODUCTID);
//
//        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
//        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);
//
//        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
//        String relation = filters.getString("relation"); // 主查询条件 逻辑关系 and / or
//
//
//        // 根据分组字段 by_fields 拼接 需要 【select 的字段和 group by 字段 】
//        /**
//         *  返回 分组 + 查询 字段
//         *  return (outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction)
//         */
//
//        Tuple6 selectAndGroupBy = byFieldOp(fields,productId);
////        Tuple4 selectAndGroupBy = new Tuple4(by._1(), by._2(), by._3(), by._4());
//
//        /**
//         * 外部查询 where 条件
//         * return ( userWhere,actionWhere)
//         */
//        Tuple5 filter = queryConditionOp(conditions, relation,productId);
//
//        return new  Tuple2(selectAndGroupBy,filter);
//    }
//
//
//
//
//
//
//    private Tuple3<JSONArray,JSONArray,JSONArray> commRetrunOp(JSONObject jsonObject ) {
//        JSONArray idxs = new JSONArray();
//
//        JSONArray action = jsonObject.getJSONArray("action");
//        action.forEach(x->{
//            JSONObject jo = JSONObject.parseObject(x.toString());
//            String eventOriginal = jo.getString("eventOriginal");
//            String eventType = jo.getString("eventType");
//            idxs.add(eventOriginal + separator+ eventType);
//        });
//
//
//        JSONArray series = new JSONArray();
//        String from = jsonObject.getString("from_date");
//        String to = jsonObject.getString("to_date");
//        List<String> dayOfRange = DateUtil.getDayOfRange(from, to);
//        dayOfRange.forEach(x-> series.add(x));
//
//        JSONArray by_fields = new JSONArray();
//
//        jsonObject.getJSONArray("by_fields").forEach(x->{
//            by_fields.add(x.toString());
//        });
//        return new Tuple3<>(idxs,series,by_fields);
//    }
//
//
//
//    private JSONObject resultOp(JSONObject jo, Tuple3<JSONArray,JSONArray,JSONArray> commReturn) {
//
////        JSONArray idxs = commReturn._1();
//
//        JSONArray series = commReturn._2();
//
//        JSONArray by_fields = commReturn._3();
//
//
//        LinkedHashSet idxs__ = new LinkedHashSet();
//        JSONArray result = jo.getJSONArray("result");
//
//        Map m = new HashMap();
//        result.forEach(x->{
//            String[] split = x.toString().split(Constants.SEPARATOR_U0001);
//            int len = split.length;
//            String flag = split[len-3];
//            String indicator = split[len-4];
//            String action = split[len-5];
//            m.putIfAbsent(flag,String.join(separator,action,indicator));
////            idxs__.add(String.join(separator,action,indicator,flag));
//        });
//        Map<String,String> ms = new LinkedHashMap<>();
//        m.entrySet().stream().sorted(Map.Entry.<String,String>comparingByKey()).forEachOrdered(e -> {Map.Entry em = (Map.Entry)e; ms.putIfAbsent((String)em.getKey(), (String)em.getValue());});
//        ms.forEach((k,v)-> idxs__.add(String.join(separator,v,k)));
//
//
//        LinkedList idxs = new LinkedList();
//        idxs__.forEach(x-> idxs.add(x.toString()));
//        Map<String, Map<String, Map<String, String>>> result1 = new HashMap<>();
//        //  分组        event_idx    date      num
//        //  分组        date        event_idx  num
//
//        result.forEach(x -> {
//            String[] split = x.toString().split(Constants.SEPARATOR_U0001);
//            int len = split.length;
//            String num = split[len - 1]; //
//            String date = split[len - 2];
//            String flag = split[len-3];
//            String indicator = split[len-4];
//            String action = split[len-5];
//
//            if ("null".equals(date)){
//                date = "cnt";
//            }
//            Set bySet = new LinkedHashSet();
//            for (int i = 0; i < len - 5; i++) {
//                if(!"null".equals(split[i])){
//                    bySet.add(split[i]);
//                }else {
//                    bySet.add("unknown");
//                }
//            }
//            String by;
//            if(bySet.isEmpty() ||  (by_fields.size()==1 && "all".equalsIgnoreCase(by_fields.getString(0)))){
//                by = "all";
//            }else{
//                by = String.join(separator,bySet);
//            }
//            Map<String,Map<String,String>> dateIdxNum = result1.get(by); // date:<idx,num>
//            String idx = String.join(separator,action,indicator,flag);
//
//            Map<String,String> idxNum;
//            if (dateIdxNum == null) {
//                dateIdxNum = new HashMap<>();
//                idxNum = new HashMap<>();
//                idxNum.put(idx,num);
//                dateIdxNum.put(date, idxNum);
//            } else {
////                eventIdxMap.put(eventIdx, date_num);
//                Map<String, String> idx_Num = dateIdxNum.get(date);
//                if (null == idx_Num){
//
//                    idxNum = new HashMap<>();
//                    idxNum.put(idx,num);
//                    dateIdxNum.put(date, idxNum);
//                }else {
//                    idx_Num.put(idx,num);
//                    dateIdxNum.put(date,idx_Num);
//                }
//            }
//            result1.put(by, dateIdxNum);
//        });
//        System.out.println(result1);
//
//
//        LinkedList eventIndicatorList = new LinkedList();
//        idxs.forEach(v ->{
//            String[] split = v.toString().split(separator, 3);
//            eventIndicatorList.add(String.join(Constants.SEPARATOR,split));
//        });
//        JSONArray indicator = new JSONArray(eventIndicatorList);
//
//        JSONArray detailRows = new JSONArray();
//        JSONArray rollupRows = new JSONArray();
//        result1.forEach((groupBy, dateIdxNum) -> {
//
//            Map<String, Map<String, String>> event = dateIdxNum;
//            Map<String, String> cnt = event.get("cnt"); JSONArray cntNums = new JSONArray();
//            idxs.forEach(idx ->{
//                String num = cnt.get(idx.toString());
//                if (null != num){
//                    cntNums.add(num);
//                }else {
//                    cntNums.add("0");
//                }
//
//            });
//            JSONArray rollupValues = new JSONArray();
//            rollupValues.add(cntNums);
//
//            JSONObject rollupRowChild = new JSONObject();
//            JSONObject detailRowChild = new JSONObject();
//            JSONArray values = new JSONArray();
//            series.forEach(x->{
//                String date = x.toString();
//                Map<String, String> idxNum = event.get(date);
//                JSONArray everyDayNums = new JSONArray();
//                if (null != idxNum){
//                    idxs.forEach(idx -> {
//                        String index = idx.toString();
//                        String num = idxNum.get(index);
//                        if (null != num){
//                            everyDayNums.add(num);
//                        }else {
//                            everyDayNums.add("0");
//                        }
//                    });
//                }else {
//                    idxs.forEach(idx-> {
//                        everyDayNums.add("0");
//                    });
//                }
//                values.add(everyDayNums);
//                //values.add(indexArr);
//            });
//
//            JSONArray byValues = new JSONArray();
//            Arrays.asList(groupBy.split(separator)).forEach(x-> byValues.add(x));
//
//            detailRowChild.put("values",values);
//            detailRowChild.put("by_values",byValues);
//            detailRowChild.put("event_indicator",indicator);
//            detailRows.add(detailRowChild);
//
//
//            rollupRowChild.put("values",rollupValues);
//            rollupRowChild.put("by_values",byValues);
//            rollupRowChild.put("event_indicator",indicator);
//            rollupRows.add(rollupRowChild);
//
//
//
//        });
//
//
//        JSONObject baseJo = new JSONObject();
//        baseJo.put("by_fields",by_fields);
//        baseJo.put("series",series);
//
//        JSONObject detailResult = new JSONObject();
//        JSONObject rollupResult = new JSONObject();
//        detailResult.put("rows",detailRows);
//        detailResult.put("num_rows",detailRows.size());
//        detailResult.put("total_rows",detailRows.size());
//        detailResult.putAll(baseJo);
//
//
//        rollupResult.put("rows",rollupRows);
//        rollupResult.put("num_rows",rollupRows.size());
//        rollupResult.put("total_rows",rollupRows.size());
//        rollupResult.putAll(baseJo);
//
//        JSONObject  res0 = new JSONObject();
//        res0.put("detail_result",detailResult);
//        res0.put("rollup_result",rollupResult);
//        System.out.println("res0............"+res0);
//
//
//        return res0 ;
//
//
//    }
//
//
//
//    /**
//     * @param actions    actions
//     * @param byFields   Tuple6(
//     *                   StringJoiner：outGroupByUser,
//     *                   StringJoiner：outGroupByAction,
//     *                   HashSet:outSelectFieldUser,
//     *                   HashSet:outSelectFieldAction
//     *                   HashMap:
//     *                   )
//     * @param outFilters Tuple2 ( outUserWhere,outActionWhere)
//     * @param map
//     * @param isQuery 是否是查询，true :查询 false 保存
//     */
//    private Tuple3<String,String,String> actionToSingleIndicatorSQL(JSONArray actions,
//                                                             Tuple6 byFields,
//                                                             Tuple5 outFilters,
//                                                             Map map,boolean isQuery) {
//        String parquetSQL = "SELECT  %s  FROM parquetTmpTable WHERE %s  GROUP BY %s";
//        String joinSQL = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE   %s  ) tu " +
//                "ON concat_ws('_', '%s', ta.global_user_id) = tu.pk where %s ";
//        String partialAggSQLFormat = "select %s from %s group by %s"; // (groupBy + action + indicatorType) , joinSQL ,groupBy
//        String joinSqlNoWhere = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE %s ) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk ";
//
//
//        HashSet<String> groupId = new HashSet();
//        JSONObject props = new JSONObject();
//        List<String> sqlList = new ArrayList<>();
//
//
//        final StringJoiner outGroupByUser = (StringJoiner) byFields._1();
//        final StringJoiner outGroupByAction = (StringJoiner) byFields._2();
//        final HashSet outSelectFieldUser = (HashSet) byFields._3();//out 根据分组字段 by_fields 筛出来的 user 查询字段
//        final HashSet outSelectFieldAction = (HashSet) byFields._4();//out 根据分组字段 by_fields 筛出来的 acton 查询字段
//        HashMap<String, String> groupByUserProp2Type = (HashMap<String, String>) byFields._5();//根据分组字段筛出来的 用户属性和属性数据类型
//        HashSet<String> userGroupIds = (HashSet<String>) byFields._6(); // 根据分组字段筛出来的 用户分群ID集合
//
//        //filter: ( new Tuple5<>(userWhere, actionWhere, groupId,actionFields, userProps);
//        // _1:user 的where条件，
//        // _2:action 的where条件，
//        // _3:用户分群id的set集合，
//        // _4:查询条件中action属性列名set集合，
//        // _5:用户属性名及其类型的map
//        // )
//        final StringJoiner outUserWhere = (StringJoiner) outFilters._1(); //根据外部条件 filter 处理的 user 过滤条件
//        final StringJoiner outActionWhere = (StringJoiner) outFilters._2();  //根据外部条件 filter 处理的 action 过滤条件
//
//        HashSet<String> groupIdOut = (HashSet<String>) outFilters._3();
//        HashSet<String> actionFieldsOut = (HashSet<String>) outFilters._4(); // //根据外部条件 filter 处理的 action 过滤涉及的 列
//        HashMap<String, String> userProp2TypeOut = (HashMap<String, String>) outFilters._5();
//        props.putAll(userProp2TypeOut);
//
//        StringJoiner groupByJoiner = new StringJoiner(",");//
//        if (!outGroupByAction.toString().isEmpty()) {
//            groupByJoiner.add(outGroupByAction.toString());
//        }
//        if (!outGroupByUser.toString().isEmpty()) {
//            groupByJoiner.add(outGroupByUser.toString());
//        }
//
//        String partialAggGroupBy ;
//        if (!groupByJoiner.toString().isEmpty()){
//            partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action", "day");
//        }else {
//            partialAggGroupBy = String.join(", ", "action", "day");
//        }
//
//
//        String relation = (String) map.get("relation");//主查询条件 and / or
//        final String commWhere = ((StringJoiner) map.get("commWhere")).toString();
//        Boolean outOr = Constants.OR.equalsIgnoreCase(relation) && (!outActionWhere.toString().isEmpty()) && (!outUserWhere.toString().isEmpty());
//        final String productId = (String) map.get("productId");
//        String userCommWhere = String.format("(pk > '%s_' AND pk < '%s_a')", productId, productId);
//        AtomicInteger i = new AtomicInteger('A');
//
//
//        actions.forEach(v -> {
//
//            JSONObject action = JSONObject.parseObject(String.valueOf(v));
//            StringJoiner commWhere1 = new StringJoiner(" AND ");
//
//            String eventOriginal = action.getString("eventOriginal");
//
//            String eventType = action.getString("eventType");
//
//            String category = SQLUtil.getCategory(eventOriginal);
//            String indicatorType = SQLUtil.getIndicatorType(eventType)._1;
//            String eventCol = SQLUtil.getIndicatorType(eventType)._2;
////            String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action");
//            commWhere1.add(commWhere).add(String.format("(category = '%s')", category)).add(String.format("( action = '%s' )",eventOriginal));
////            System.out.println("commWhere1----------------------------------:               " + commWhere1.toString());
//            JSONObject childFilterParam = action.getJSONObject(Constants.CHILDFILTERPARAM);
//            String relate = childFilterParam.getString("relation");
//            JSONArray conditions = childFilterParam.getJSONArray("conditions");
//            /**
//             //filter: ( new Tuple5<>(userWhere, actionWhere, groupId,actionFields, userProps);
//             // _1:user 的where条件，
//             // _2:action 的where条件，
//             // _3:用户分群id的set集合，
//             // _4:查询条件中action属性列名set集合，
//             // _5:用户属性名及其类型的map
//             // )
//             */
//            Tuple5 queryCondition = queryConditionOp(conditions, relate,productId);
////            Tuple2<StringJoiner, StringJoiner> inCondition = new Tuple2(queryCondition._1(), queryCondition._2());
//            StringJoiner inUserWhere = (StringJoiner) queryCondition._1();
//            StringJoiner inActionWhere = (StringJoiner) queryCondition._2();
//            //用户分群id的set集合
//            HashSet<String> groupIdIn = (HashSet<String>) queryCondition._3();
//            groupIdIn.forEach(vv -> groupId.add(vv));
//
//            HashSet<String> actionFieldsIn = (HashSet<String>) queryCondition._4();//查询条件中action属性列名set集合
//            HashMap<String, String> userProp2TypeIn = (HashMap<String, String>) queryCondition._5();
//            userGroupIds.addAll(groupIdIn);
//            props.putAll(userProp2TypeIn);
//            userProp2TypeIn.forEach((key, value) -> groupByUserProp2Type.merge(key, value, (vF, vB) -> vF));
//
//
//            Boolean inOr = Constants.OR.equalsIgnoreCase(relate) && (!inActionWhere.toString().isEmpty()) && (!inUserWhere.toString().isEmpty());
////            Boolean outOr = !(outActionWhere.toString().isEmpty() || outUserWhere.toString().isEmpty());
//
//
//            HashSet<String> actionSelectSet = new HashSet<>();
//            actionSelectSet.addAll(Arrays.asList("global_user_id", "action", "day"));
//            actionSelectSet.addAll(outSelectFieldAction);
//            actionSelectSet.add(eventCol);
//
//            HashSet<String> userSelectSet = new HashSet<>();
//            userSelectSet.add("pk");
//            userSelectSet.addAll(outSelectFieldUser);
//
//            // actionSelect : global_user_id, 分组字段，action，day
//
////            String actionSelect = String.join(", ", actionSelectSet);// actionSelect 基本筛选条件
//            String userSelect = String.join(", ", userSelectSet);   //userSelect 基本筛选条件 : pk, 分组字段
//
//
//            //joinselect : 分组字段，action，day, 指标 , 'A' AS an
//            String joinSelect ;
//            if (!groupByJoiner.toString().isEmpty()){
//                joinSelect = String.join(", ", groupByJoiner.toString(), "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", (char) (i.getAndAdd(1))), indicatorType);
//            }else {
//                joinSelect = String.join(", ",  "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", (char) (i.getAndAdd(1))), indicatorType);
//            }
////            String.join(",", joinSelect, indicatorType);
////            vvv= vvv+1;
//            //String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action","day");
//
//            if (outGroupByUser.toString().isEmpty() && inUserWhere.toString().isEmpty() && outUserWhere.toString().isEmpty()) {
//                //分组、内部条件、外部条件中只有事件属性，只查 parquet 表
////                String select = String.join(", ", "global_user_id", "action", outSelectFieldAction.toString());
////                String groupBy = String.join(", ", "action", outGroupByAction.toString()); //
//                StringJoiner actionWhere = getAllActionWhere(commWhere1, outActionWhere, inActionWhere);
//                String singleSQL;
//                singleSQL = String.format(parquetSQL, joinSelect, actionWhere.toString(), partialAggGroupBy);
//                sqlList.add(singleSQL);
//            } else {
//                // ( Constants.OR.equalsIgnoreCase(relate) && inOr)
//                if (inOr || outOr) {
//                    // 在or 条件下，且查询条件同时含有action 和 user属性，则过滤条件不能下推到最底层的
//                    // usersTable 和 parquetTmpTable 中过滤，需要放在 usersTable join parquetTmpTable
//                    //  后的条件中
//                    String actionWhere = String.join(Constants.AND, commWhere1.toString());
//                    String actionWhere1 = actionWhere;
//                    String userWhere = String.join(Constants.AND, userCommWhere);
//                    String userWhere1 = userWhere;
//
//                    HashSet<String> actionSelectSetOr = (HashSet<String>) actionSelectSet.clone();
//
//                    HashSet<String> userSelectSetOr = (HashSet<String>) userSelectSet.clone();
//                    // or
//
//
//                    if (inOr && outOr) {
//
//                        //outActionWhere,outUserWhere ,inActionWhere,inUserWhere 都不为空，且inOr,outOr 都是 OR
//                        // (outActionWhere OR outUserWhere)  and (inActionWhere OR inUserWhere) 放到join where 后
//                        // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
//                        //  parquetTmpTable 的select列中需要包含 or 条件中包含的列（包含out 和 in)
//                        //  usersTable 的select列中需要包含 or 条件中包含的列（包含out 和 in)
//
//                        actionSelectSetOr.addAll(actionFieldsOut);
//                        actionSelectSetOr.addAll(actionFieldsIn);
//                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(", ", String.format("'%s' AS INDICATORTYPE",eventType));
//                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
//                        userSelectSetOr.addAll(groupIdIn);
//                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
//                        userSelectSetOr.addAll(groupIdOut);
//                        String userSelectOr = String.join(", ", userSelectSetOr);
//
//
//                        String outWhere = new StringJoiner(Constants.OR, "(", ")").add(outActionWhere.toString()).add(outUserWhere.toString()).toString();
//                        String inWhere = new StringJoiner(Constants.OR, "(", ")").add(inActionWhere.toString()).add(inUserWhere.toString()).toString();
//                        String joinWhere = new StringJoiner(Constants.AND).add(outWhere).add(inWhere).toString();
//
//                        userWhere1 = userCommWhere;
//                        actionWhere1 = commWhere1.toString();
//                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
//                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
//                        sqlList.add(partialAggSQL);
//                    } else if (inOr && (!outOr)) {
//                        // inWhere 条件   放到  usersTable join parquetTmpTable 后,
//                        // outWhere 放到 对应 usersTable 和 parquetTmpTable 表 的where 后
////                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));
//
//                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ in)
//                        //   usersTable 的select列中需要包含 or 条件中包含的列（ in)
//
//                        actionSelectSetOr.addAll(actionFieldsIn);
//                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(",", String.format("'%s' AS INDICATORTYPE",eventType));
//                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
//                        userSelectSetOr.addAll(groupIdIn);
//                        userSelectSetOr.addAll(groupIdOut);
//                        String userSelectOr = String.join(", ", userSelectSetOr);
//                        String joinWhere = String.join(Constants.OR, inActionWhere.toString(), inUserWhere.toString());
////                        String actionWhere = String.join(Constants.AND, commWhere1.toString());
//                        if (!outActionWhere.toString().isEmpty()) {
//                            actionWhere1 = actionWhere.join(relate, outActionWhere.toString());
//                        }
////                        String userWhere = String.join(Constants.AND, userCommWhere);
//                        if (!outUserWhere.toString().isEmpty()) {
//                            userWhere1 = userWhere.join(Constants.AND, outUserWhere.toString());
//                        }
////                        String joinWhere
//                        //actionSelect ,actionWhere , userSelect , userWhere  productId,joinWhere, joinGroupBy(action ... )
//                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
////                        String partialAggSQLFormat = "select %s from %s group by %s";
//
//                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
//                        sqlList.add(partialAggSQL);
////
//                    } else if (!inOr && outOr) {
//                        //1. outActionWhere  outUserWhere  都是非空 ，且 or 关系，放到join where 后
//                        //2. inActionWhere inUserWhere 可能为空 或 为 and 关系
//                        // outWhere 条件   放到  usersTable join parquetTmpTable 后
//                        // ,inWhere 放到 对应的 usersTable 和 parquetTmpTable 表 的where 后
////                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));
//                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ out ，把 out 条件中的 action 列加到parquet表的 select 字段中)
//                        //   usersTable 的select列中需要包含 or 条件中包含的列（ out 把 out 条件中的 users列加到parquet表的 select 字段中))
//
//                        actionSelectSetOr.addAll(actionFieldsOut);
//                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(",", String.format("'%s' AS INDICATORTYPE",eventType));
//                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
//                        userSelectSetOr.addAll(groupIdOut);
//                        String userSelectOr = String.join(", ", userSelectSetOr);
//
//                        String joinWhere = String.join(Constants.OR, outActionWhere.toString(), outUserWhere.toString());
////                        String actionWhere = String.join(Constants.AND, commWhere1.toString());
//                        if (!inActionWhere.toString().isEmpty()) {
//                            actionWhere1 = actionWhere.join(relate, inActionWhere.toString());
//                        }
////                        String userWhere = String.join(Constants.AND, userCommWhere);
//                        if (!inUserWhere.toString().isEmpty()) {
//                            userWhere1 = userWhere.join(relate, inUserWhere.toString());
//                        }
//                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
//                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
//                        sqlList.add(partialAggSQL);
//
//                    }
////                    String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
////                    String partialAggSQL = String.format(partialAggSQLFormat, String.join(",", joinSelect, indicatorType), joinSQL1, groupBy);
////                    System.out.println("partialAggSQL........................." + partialAggSQL);
//
//                } else {
//                    // 所有条件均分别下推到 userTable 和 parquetTmpTable 表的where  后
//                    // join 后没有where
//                    // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
//                    // actionWhere: outActionWhere and inActionWhere ->
//                    // userWhere ：outUserWhere and inUserWhere -->
//                    //
//                    StringJoiner actionWhere = getAllActionWhere(commWhere1, outActionWhere, inActionWhere);
////                    new StringJoiner(Constants.AND);
////                    actionWhere.add(commWhere1.toString());
////                    if (!outActionWhere.toString().isEmpty()) {
////                        actionWhere.add(outActionWhere.toString());
////                    }
////                    if (!inActionWhere.toString().isEmpty()) {
////                        actionWhere.add(inActionWhere.toString());
////                    }
//                    String actionSelect = String.join(",",String.join(", ", actionSelectSet),String.format("'%s' AS INDICATORTYPE",eventType));
//                    StringJoiner userWhere = new StringJoiner(Constants.AND);
//                    userWhere.add(userCommWhere);
//                    if (!outUserWhere.toString().isEmpty()) {
//                        userWhere.add(outUserWhere.toString());
//                    }
//                    if (!inUserWhere.toString().isEmpty()) {
//                        userWhere.add(inUserWhere.toString());
//                    }
//                    actionWhere.toString();
//                    String joinSQL1 = String.format(joinSqlNoWhere, actionSelect, actionWhere.toString(), userSelect, userWhere.toString(), productId);
//                    String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
//                    sqlList.add(partialAggSQL);
//
//                }
//
//
//            }
//
//
//        });
//
//        String SQL = "";
//
//        SQL = String.join(" UNION ", sqlList);
//
//
//        String allGroupBy = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an");
//
//        String grouping;
//        if(!groupByJoiner.toString().isEmpty()){
//            grouping = String.join(",", groupByJoiner.toString(), "action", "INDICATORTYPE","an");
//        }else {
//            grouping = String.join(",", "action", "INDICATORTYPE","an");
//        }
//
//
//        String group;
//        String allSelect ;
//        if( isQuery ){
//            String groupingSet = String.format("GROUPING SETS(( %s ),(%s) ) ORDER BY %s", String.join(",",partialAggGroupBy,"INDICATORTYPE","an"), grouping,partialAggGroupBy); // groupby + action
//            group = String.join(" ", allGroupBy, groupingSet);
//            if (!groupByJoiner.toString().isEmpty()){
//                allSelect = String.join(", ", groupByJoiner.toString(), "action","INDICATORTYPE","an", "day", "SUM(ct)");
//            }else{
//                allSelect = String.join(", ", "action","INDICATORTYPE","an", "day", "SUM(ct)");
//            }
//
//
//        }else {
//            group = String.join(" ", allGroupBy);
//
//            if (!groupByJoiner.toString().isEmpty()){
//                allSelect = String.join(", ","action","INDICATORTYPE", groupByJoiner.toString(), "an",  "SUM(ct) as num");
//            }else{
//                allSelect = String.join(", ", "action","INDICATORTYPE","'all'","an", "SUM(ct) as num");
//            }
//
//        }
//        String allSQL = String.format("select %s from (%s) group by %s ", allSelect, SQL, group);
//
//
//        userGroupIds.forEach(v -> groupId.add(v));
//        groupIdOut.forEach(v -> groupId.add(v));
//
//
//        props.putAll(groupByUserProp2Type);
//
//        StringJoiner prop = new StringJoiner(",","{","}");
//        props.forEach((userPropName,userPropDataType) ->{
//            prop.add(String.format("\\\"%s\\\":\\\"%s\\\"",userPropName,userPropDataType));
//        });
//
//        StringJoiner groupIds = new StringJoiner(",", "{", "}");
//        groupId.forEach(xx->groupIds.add(xx));
//        String taskSql = "\",namespace=\""+hbaseNameSpace+"\",prop=\""+prop.toString()+"\",groupid=\""+groupIds.toString()+"\"";
//
//        System.out.println("props:                   \"" + prop.toString() + "\"");
//        System.out.println("groupIds:                   \"" + groupIds.toString() + "\"");
//
//        System.out.println("taskSql:                   " + taskSql   );
//        System.out.println("allSQL:                    " + allSQL     );
//
//
//        if (isQuery){
//            return new Tuple3<>(allSQL,taskSql,"");
//        }else {
//            return new Tuple3<>(allSQL,prop.toString(),groupIds.toString());
//        }
//    }
//
//
//
//
//    /**
//     * 根据 每一组的 查询条件 和 条件关系(adn/or) 拼接 每一组 action 表和user表的where 条件
//     *
//     * @param conditions 查询条件集合
//     * @param relation   条件间的逻辑关系 ：and / or
//     * @return 五元组：(
//     * action的where条件，user的where条件，用户分群id的set集合，查询条件中action属性列名set集合，用户属性名及其类型的map)
//     */
//    private Tuple5<
//            StringJoiner, StringJoiner,
//            Set<String>, Set<String>,
//            Map<String, String>
//            > queryConditionOp(JSONArray conditions, String relation,String productId) {
//
//        StringJoiner actionWhere = new StringJoiner(String.format(" %s ", relation)); // action where 条件
//        StringJoiner userWhere = new StringJoiner(String.format(" %s ", relation));
//        Map<String, String> userProps = new HashMap<>();
//        Set<String> groupId = new HashSet<>();
//        Set<String> actionFields = new HashSet<>();
//
//        conditions.forEach(v -> {
//            JSONObject condition = JSONObject.parseObject(String.valueOf(v));
//            String type = condition.getString("type");  //event.country, user.age, userGroup.benyueqianzaitouziyonghu
//            String function = condition.getString("function");
//            JSONArray params = condition.getJSONArray("params");
//            String isNumber = condition.getString("isNumber");
//            String[] split = type.split("\\.", 2);
//            String column = split[1];
//            String con;
//            switch (split[0]) {
//                case "event": {
//                    actionFields.add(column);// eventWhere = eventWhereFunctionOp(type, function, params, eventWhere);
//                    switch (function) {
//                        case "equal": { // string  and  Number
//                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
//                            if ("isTrue".equals(isNumber)) {
//                                params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                            } else {
//                                params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                            }
//
//                            con = String.format("(%s IN  %s)", column, joiner.toString());
//                            actionWhere.add(con);
//                            break;
//                        }
//                        case "notEqual": {  // string  and  Number
//                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
//                            params.forEach(x -> joiner.add(String.format("'%s'", x)));
//                            con = String.format("(%s NOT IN  %s)", column, joiner.toString());
//                            actionWhere.add(con);// IN  v.s NOT IN
//                            break;
//                        }
//                        case "contain": { //string
//                            String param = params.getString(0);
//                            actionWhere.add(String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", column, column, param));// LIKE  v.s    LIKE
//                            break;
//                        }
//                        case "isTrue":
//                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", column, column));
//                            break;
//                        case "isFalse":
//                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", column, column));
//                            break;
//                        case "notContain": { //string
//                            String param = params.getString(0);
//                            actionWhere.add(String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", column, column, param));// LIKE  v.s NOT LIKE
//                            break;
//                        }
//                        case "more": {  // Number >
//                            String param = params.getString(0);
//                            con = String.format("(%s  >  %s)", column, param);
//                            actionWhere.add(con);
//                            break;
//                        }
//                        case "less": { // Number <
//                            String param = params.getString(0);
//                            con = String.format("(%s  <  %s)", column, param);
//                            actionWhere.add(con);
//                            break;
//                        }
//                        case "region": {  // Number  between ：  >= and <=
//                            String param1 = condition.getString("param1");
//                            String param2 = condition.getString("param2");
//                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", column, param1, column, param2);
//                            actionWhere.add(con);
//                            break;
//                        }
//                    }
//
//
//                    break;
//                }
//                case "user": {
//                    switch (function) {
//                        // String:
//                        case "equal": { // string  and  Number
//                            StringJoiner joiner = new StringJoiner(" or ", "(", ")");
//                            if ("isTrue".equals(isNumber)) {
//                                params.forEach(x -> joiner.add(String.format("  %s = %s", column, x)));
//                                userProps.put(column, "int");
//                            } else {
//                                params.forEach(x -> joiner.add(String.format("  %s = '%s'", column, x)));
//                                userProps.put(column, "string");
//                            }
//                            con = String.format("( %s IS NOT NULL  AND  %s )", column, joiner.toString());
//                            userWhere.add(con);
//                            break;
//                        }
//                        case "notEqual": {  // string  and  Number
//                            StringJoiner joiner = new StringJoiner(" AND ", "(", ")");  // and v.s and  or
//                            if ("isTrue".equals(isNumber)) {
//                                params.forEach(x -> joiner.add(String.format("  %s <> %s", column, x)));
//                                userProps.put(column, "int");
//                            } else {
//                                params.forEach(x -> joiner.add(String.format("  %s <> '%s'", column, x)));
//                                userProps.put(column, "string");
//                            }
//                            userWhere.add(String.format("((%s IS NOT NULL ) AND  %s )", column, joiner.toString()));
//                            break;
//                        }
//                        case "contain": { //string like
//                            String param = params.getString(0);
//                            con = String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", column, column, param);
//                            userWhere.add(con);// LIKE  v.s    LIKE
//                            userProps.put(column, "string");
//                            break;
//                        }
//                        case "notContain": { //string
//                            String param = params.getString(0);
//                            con = String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", column, column, param);
//                            userWhere.add(con);//NOT LIKE    v.s NOT LIKE
//                            userProps.put(column, "string");
//                            break;
//                        }
//                        case "isTrue":
//                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", column, column));
//                            userProps.put(column, "boolean");
//                            break;
//                        case "isFalse":
//                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", column, column));
//                            userProps.put(column, "boolean");
//                            break;
//                        case "more": {  // Number >
//                            String param = params.getString(0);
//                            con = String.format("(%s  >  %s)", column, param);
//                            userProps.put(column, "int");
//                            userWhere.add(con);
//                            break;
//                        }
//                        case "less": { // Number <
//                            String param = params.getString(0);
//                            con = String.format("(%s  <  %s)", column, param);
//                            userProps.put(column, "int");
//                            userWhere.add(con);
//                            break;
//                        }
//                        case "region": {  // Number  between ：  >= and <=
//                            String param1 = condition.getString("param1");
//                            String param2 = condition.getString("param2");
//                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", column, param1, column, param2);
//                            userProps.put(column, "int");
//                            userWhere.add(con);
//                            break;
//                        }
//
//
//                    }
//                    break;
//                }
//                case "userGroup": {
//                    String param = "false";
//                    if ("isTrue".equals(function)) {
//                        param = "true";
//                    }
//                    String groupIdInHBase = productId+"_"+column;
//                    userWhere.add(String.format("((%s IS NOT NULL ) AND (%s = %s) )", groupIdInHBase, groupIdInHBase, param));
//                    groupId.add(groupIdInHBase);
//                    break;
//                }
//            }
//
//
//        });
//        return new Tuple5<>(userWhere, actionWhere, groupId, actionFields, userProps);
//
//    }
//
//
//
//
//    /**
//     * 根据分组字段获取 usersTable 和 parquetTmpTable 的 group by 和 select 字段
//     * todo 获取usersTable 的 schema
//     *
//     * @param by_field
//     * @return
//     */
//    private Tuple6<StringJoiner, StringJoiner, HashSet, HashSet,
//            HashMap<String, String>,
//            HashSet<String>
//            > byFieldOp(JSONArray by_field,String productId) {
//        Map<String, String> userPropertiesMap = getUserPropertiesAndTypes(productId);
//        StringJoiner outGroupByUser = new StringJoiner(", "); // user group by 字段
//        StringJoiner outGroupByAction = new StringJoiner(", "); //  action group by 字段
//
//        HashSet outSelectFieldUser = new HashSet(); //  user select  字段
//        HashSet outSelectFieldAction = new HashSet(); // action select  字段
//
//        Map<String, String> userProps = new HashMap<>();
//        HashSet<String> groupId = new HashSet<>();
//
//        by_field.stream().filter(x->!"all".equalsIgnoreCase(x.toString())).forEach(field -> {
//            String[] split = field.toString().split("\\.", 2);
//            String type = split[0];
//            String column = split[1];
//            if ("event".equals(type)) {
//                //parquetTmpTable 表
//                outGroupByAction.add(column);//  group by 字段拼接
//                outSelectFieldAction.add(column); // select 字段拼接
//
//            } else if ("user".equals(type)) {
//                //usersTable  表的用户属性
//                outGroupByUser.add(column);// group by 字段拼接
//                userProps.put(column, userPropertiesMap.get(column));
//                outSelectFieldUser.add(column);// select 字段拼接
//
//            } else if ("userGroup".equals(type)) {
//                //usersTable  表的用户分群属性处理
//                String groupIdInHBase = productId+"_"+column;
//                outGroupByUser.add(groupIdInHBase);// group by 字段拼接
//                groupId.add(groupIdInHBase);
//                outSelectFieldUser.add(groupIdInHBase);// select 字段拼接
//            }
//
//        });
//        return new Tuple6(outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction, userProps, groupId);
//    }
//
//    //todo 用户属性及其类型
//    private Map<String, String> getUserPropertiesAndTypes(String productId) {
//        Map<String, String> map = new HashMap<>();
//        UserMetaType  userMetaType2  = new UserMetaType();
//        userMetaType2.setProductId(Long.parseLong(productId));
////        userMetaTypeMapper.getAllActiveMetaTypeListForFilter(userMetaType2).forEach(domain->map.put(domain.getType(),domain.getDatatype()) );
//        return map;
//    }
//
//
//    private StringJoiner getAllActionWhere(StringJoiner commWhere1, StringJoiner outActionWhere, StringJoiner inActionWhere) {
//
//        StringJoiner actionWhere = new StringJoiner(Constants.AND);
//        actionWhere.add(commWhere1.toString());
//        if (!outActionWhere.toString().isEmpty()) {
//            actionWhere.add(outActionWhere.toString());
//        }
//        if (!inActionWhere.toString().isEmpty()) {
//            actionWhere.add(inActionWhere.toString());
//        }
//        return actionWhere;
//
//    }
//
//
//
//
//    /**
//     * 用于查询总计
//     *
//     * @param unit       day
//     * @param fields     分组
//     * @param sqlBuilder sql
//     */
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
//    /**
//     * 事件分析实时查询
//     *
//     * @param sqlBuilder sql语句
//     * @param maxLine 返回最大记录数
//     * @return 结果
//     * @throws IOException 异常
//     */
////    private JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine,String params) throws IOException {
////        String jobId = "tmp_actionreport_job_" + UUID.randomUUID().toString();
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
////
////        JSONObject responseResult = null;
////
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
////        return responseResult;
////    }
//
//    /**
//     * 把图表显示的original修改为display值
//     *
//     * @param productId 产品
//     * @param fields    字段（分组）
//     * @param result    结果
//     */
////    private void modifyOriginalToDisplay(String productId, JSONArray fields, JSONObject result) {
////        // 把每个分组的original和display组成map
////        List<Map<String, String>> mapList = new ArrayList<>();
////        for (int i = 0; i < fields.size(); i++) {
////            if(fields.getString(i).startsWith("event.")|| "all".equals(fields.getString(i))){
////                Metadata metadata = new Metadata();
////                metadata.setProductid(productId);
////                if(!"all".equals(fields.getString(i))){
////                    metadata.setType( fields.getString(i).substring(6));
////                }else{
////                    metadata.setType(fields.getString(i));
////                }
////                List<Metadata> metadataList = metadataMapper.getActiveMetadataValue(metadata);
////
////                Map<String, String> map = new HashMap<>();
////                for (Metadata aMetadataList : metadataList) {
////                    map.put(aMetadataList.getOriginal(), aMetadataList.getDisplay());
////                }
////                mapList.add(map);
////            }else if(fields.getString(i).startsWith("user.")){//用户属性
////                UserMetadata metadata = new UserMetadata();
////                metadata.setProductid(productId);
////                metadata.setType( fields.getString(i).substring(5));
////                List<UserMetadata> metadataList = userMtadataMapper.getActiveMetadataValue(metadata);
////                Map<String, String> map = new HashMap<>();
////                for (UserMetadata aMetadataList : metadataList) {
////                    map.put(aMetadataList.getOriginal(), aMetadataList.getDisplay());
////                }
////                mapList.add(map);
////            }else if(fields.getString(i).startsWith("userGroup.")){//用户属性
////                Map<String, String> map = new HashMap<>();
////                    map.put("ture", "真");
////                    map.put("false", "假");
////                mapList.add(map);
////            }
////        }
////
////        Map<String, int[][]> countMap = jsonArrayToMap(result, mapList);
////        Map<String, Integer> totalCountMap = totalJsonArrayToMap(result, mapList);
////        totalCountMap = CommonUtil.mapIntValueSort(totalCountMap);
////
////        JSONArray displayValue = new JSONArray();
////        for (Map.Entry<String, Integer> totalCount : totalCountMap.entrySet()) {
////            JSONObject display = new JSONObject();
////            display.put(Constants.BY_VALUES, totalCount.getKey().split(Constants.ACTION_RESULT_SEPARATOR));
////            display.put(Constants.COUNT, countMap.get(totalCount.getKey()));
////            display.put("totalCount", totalCount.getValue());
////            displayValue.add(display);
////        }
////        result.put("value", displayValue);
////    }
//
//    /**
//     * 把json结果的value转成map格式，以便于重新处理数据
//     *
//     * @param result  json结果
//     * @param mapList original和display的对应map
//     * @return map结果
//     */
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
//                if ("null".equals(valuesString)){
//                    byValues.set(j, "其他");
//                }else {
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
//    /**
//     * 用于获取每个分组的total，并且转换为map
//     *
//     * @param result  json结果
//     * @param mapList 分组连接为key，total为值
//     * @return map结果
//     */
//    private Map<String, Integer> totalJsonArrayToMap(JSONObject result, List<Map<String, String>> mapList) {
//        // 修改返回值，根据map修改分组的值
//        JSONArray array = result.getJSONArray(Constants.ACTION_REPORT_VALUE);
//        Map<String, Integer> totalCountMap = new HashMap<>();
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject original = array.getJSONObject(i);
//            JSONArray byValues = original.getJSONArray(Constants.BY_VALUES);
//            for (int j = 0; j < byValues.size(); j++) {
//                final String valuesString = byValues.getString(j);
//                if ("null".equals(valuesString)){
//                    byValues.set(j, "其他");
//                }else {
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
//    /**
//     * 限制返回个数
//     *
//     * @param result 总结果
//     * @param values 返回结果
//     */
//    public static void limitReturnResultCount(JSONObject result, JSONArray values) {
//        // 限制个数
//        if (values.size() > Constants.FIELD_GROUP_COUNT) {
//            JSONArray newValues = new JSONArray();
//            newValues.addAll(values.subList(0, Constants.FIELD_GROUP_COUNT));
//            result.put(Constants.ACTION_REPORT_VALUE, newValues);
//
//            // 数据个数
//            result.put(Constants.TOTAL_ROWS, values.size());
//            result.put(Constants.NUM_ROWS, newValues.size());
//        } else {
//            result.put(Constants.ACTION_REPORT_VALUE, values);
//
//            // 数据个数
//            result.put(Constants.TOTAL_ROWS, values.size());
//            result.put(Constants.NUM_ROWS, values.size());
//        }
//    }
//
//    /**
//     * 处理结果为空的返回
//     *
//     * @param result 结果
//     */
//    public static void handleResultIsEmpty(JSONObject result) {
//        // 返回日期集合
//        result.put("date", new JSONArray());
//
//        // 数据
//        JSONArray values = new JSONArray();
//        result.put(Constants.ACTION_REPORT_VALUE, values);
//
//        // 数据个数
//        result.put(Constants.TOTAL_ROWS, values.size());
//        result.put(Constants.NUM_ROWS, values.size());
//    }
//
//    /**
//     * 把MAP转成JSON数组
//     *
//     * @param fromDate       开始日期
//     * @param toDate         结束日期
//     * @param actions        事件
//     * @param dates          日期集合
//     * @param resultMap      中间值
//     * @param totalResultMap total中间值
//     * @return 结果
//     */
//    private JSONArray handleMapToJsonArray(String fromDate, String toDate, JSONArray actions, JSONArray dates, Map<String, Map<String, int[]>> resultMap, Map<String, Integer> totalResultMap) {
//        JSONArray values = new JSONArray();
//        for (Map.Entry<String, Map<String, int[]>> m : resultMap.entrySet()) {
//            JSONObject value = new JSONObject();
//            final String[] split = m.getKey().split(Constants.CUSTOM_SEPARATOR);
//            value.put(Constants.BY_VALUES, split);
//
//            Map<String, int[]> co = m.getValue();
//            int[][] counts = new int[dates.size()][actions.size()];
//            for (Map.Entry<String, int[]> coMap : co.entrySet()) {
//                counts[LocalDateTimeUtil.getDateIndex(fromDate, toDate, coMap.getKey())] = coMap.getValue();
//            }
//            value.put(Constants.COUNT, counts);
//            value.put("totalCount", totalResultMap.get(m.getKey()));
//            values.add(value);
//        }
//        return values;
//    }
//
//    /**
//     * 查询结果转成MAP
//     * 保存形式：key为分组字段，value为 日期和数量的map
//     *
//     * @param fields    字段
//     * @param actions   事件
//     * @param jsonArray 中间值
//     * @return 结果
//     */
//    private Map<String, Map<String, int[]>> handleJsonArrayToMap(JSONArray fields, JSONArray actions, JSONArray jsonArray) {
//        Map<String, Map<String, int[]>> resultMap = new HashMap<>();
//        final int size = jsonArray.size();
//        // 针对单个总体分组
//        if (fields.size() == 1 && "all".equals(fields.getString(0))) {
//            String key = "总体";
//            Map<String, int[]> stringMap = new HashMap<>();
//            for (int i = 0; i < size; i++) {
//                String json = jsonArray.getString(i);
//                String[] split = json.split(Constants.SEPARATOR);
//                final int length = split.length;
//                final String jsonDate = split[length - 1 - actions.size()];
//                int[] counts = new int[actions.size()];
//                counts[actions.size() - 1] = Integer.parseInt(split[length - 1]);
//                stringMap.put(jsonDate, counts);
//            }
//            resultMap.put(key, stringMap);
//        } else {
//            for (int i = 0; i < size; i++) {
//                String json = jsonArray.getString(i);
//                String[] split = json.split(Constants.SEPARATOR);
//                StringBuilder keyBuilder = new StringBuilder();
//
//                // 连接分组值
//                for (int j = 0; j < fields.size(); j++) {
//                    keyBuilder.append(split[j]).append(Constants.JOINER);
//                }
//                String key = keyBuilder.delete(keyBuilder.lastIndexOf(Constants.JOINER), keyBuilder.length()).toString();
//                final int length = split.length;
//                final String jsonDate = split[length - 1 - actions.size()];
//                if (resultMap.containsKey(key)) {
//                    Map<String, int[]> stringMap = resultMap.get(key);
//                    int[] counts = new int[actions.size()];
//                    counts[actions.size() - 1] = Integer.parseInt(split[length - 1]);
//                    stringMap.put(jsonDate, counts);
//                } else {
//                    Map<String, int[]> stringMap = new HashMap<>();
//                    int[] counts = new int[actions.size()];
//                    counts[actions.size() - 1] = Integer.parseInt(split[length - 1]);
//                    stringMap.put(jsonDate, counts);
//                    resultMap.put(key, stringMap);
//                }
//            }
//        }
//        return resultMap;
//    }
//
//    /**
//     * 查询total结果转成MAP
//     * 保存形式：key为分组字段，value为 数量
//     *
//     * @param fields         字段
//     * @param totalJsonArray total中间值
//     * @return 结果
//     */
//    private Map<String, Integer> handleTotalJsonArrayToMap(JSONArray fields, JSONArray totalJsonArray) {
//        Map<String, Integer> resultMap = new HashMap<>();
//        final int size = totalJsonArray.size();
//        // 针对单个总体分组
//        if (fields.size() == 1 && "all".equals(fields.getString(0))) {
//            for (int i = 0; i < size; i++) {
//                String json = totalJsonArray.getString(i);
//                String[] split = json.split(Constants.SEPARATOR);
//                final int length = split.length;
//                resultMap.put("总体", Integer.parseInt(split[length - 1]));
//            }
//        } else {
//            for (int i = 0; i < size; i++) {
//                String json = totalJsonArray.getString(i);
//                String[] split = json.split(Constants.SEPARATOR);
//                StringBuilder keyBuilder = new StringBuilder();
//
//                // 连接分组值
//                for (int j = 0; j < fields.size(); j++) {
//                    keyBuilder.append(split[j]).append(Constants.JOINER);
//                }
//                String key = keyBuilder.delete(keyBuilder.lastIndexOf(Constants.JOINER), keyBuilder.length()).toString();
//                final int length = split.length;
//                resultMap.put(key, Integer.parseInt(split[length - 1]));
//            }
//        }
//        return resultMap;
//    }
//
//    /**
//     * 多个事件
//     *
//     * @param actions 事件
//     * @param result  结果
//     */
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
////    @Override
////    @Transactional
//    public int saveQueryTask(JSONObject jsonObject) throws IOException {
//        // 保存自定义查询任务
//        String unit = jsonObject.getString(Constants.UNIT);
//        String reportName = jsonObject.getString(Constants.REPORT_NAME);
//        String description = jsonObject.getString("description");
//        String productId = jsonObject.getString(Constants.PRODUCTID);
//        String fromDate = jsonObject.getString(Constants.FROM_DATE);
//        String toDate = jsonObject.getString(Constants.TO_DATE);
//
//        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);
//        String relation = filters.getString(Constants.RELATION);
//        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
//        if(conditions.size()<= 1){
//            relation = Constants.AND;
//        }
//        StringJoiner commWhere = new StringJoiner(" AND ");
////
//        String dateCon = String.format("( productid = '%s' )", productId);
//        commWhere.add(dateCon);
//        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标
//
//
////        Tuple2 filter = new Tuple2(queryCondition._1(), queryCondition._2());
//
//
//        Map map = new HashMap();
//        map.put("relation", relation);
//        map.put("commWhere", commWhere);
//        map.put("productId", productId);
//
//        Tuple2 queryOrSaveOp = queryOrSaveOp(jsonObject);
//        /**
//         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
//         */
//        Tuple3<String,String,String> sqlOps = actionToSingleIndicatorSQL(actions, (Tuple6)queryOrSaveOp._1, (Tuple5) queryOrSaveOp._2, map,false);
//
//        String taskSQL = sqlOps._1();
//        String prop = sqlOps._2();
//        String groupid = sqlOps._3();
//
//
//        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);
////        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);
//
//        // 数据
//        JSONObject data = jsonObject.getJSONObject("data");
////
//
//        // 保存事件任务
//        ActionReport actionReport = new ActionReport();
//        actionReport.setReportName(reportName);
//        final String dateRange = LocalDateTimeUtil.getDateTimePeriodDay(fromDate) + Constants.TASK_DATE_RANGE_JOINER + LocalDateTimeUtil.getDateTimePeriodDay(toDate);
//        actionReport.setDateRange(dateRange);
//        actionReport.setTaskSql(taskSQL);
//        actionReport.setDescription(null == description ? "" : description);
//        actionReport.setUnit(unit);
//        actionReport.setProductId(Integer.parseInt(productId));
//
//        // 保存条件时删除前端传过来的图表数据
//        jsonObject.remove("data");
//        actionReport.setTaskConditions(jsonObject.toString());
//        actionReport.setTableName("temp");
//
//        actionReport.setUserProp(prop);
//        actionReport.setUserGroupid(groupid);
//        actionReport.setCreateTime(new Date());
////        actionReportMapper.insert(actionReport);
//
//        // 更新表名
//        ActionReport updateHbaseTableName = new ActionReport();
//        final Integer reportId = 10010;//actionReport.getReportId();
//        updateHbaseTableName.setReportId(reportId);
//        final String tableName = hbaseNameSpace + Constants.TASK_ACTION_TABLE_NAME + reportId;
//        updateHbaseTableName.setTableName(tableName);
////        actionReportMapper.updateByPrimaryKeySelective(updateHbaseTableName);
//
//        // 创建hbase表
////        Connection2hbase.createTable(tableName);
//
//        // 保存数据到hbase
//        saveDataToHbase( reportId, tableName, data);
//
//        logger.debug("task's sql: {}", "sql...................................................");
//        return reportId;
//    }
//
//    /**
//     * 把事件分析实时查询出的数据保存到hbase
//     *
//     * @param reportId  报表ID
//     * @param tableName hbase表名
//     * @param data      数据
//     * @throws IOException 异常
//     */
//    private void saveDataToHbase( Integer reportId, String tableName, JSONObject data)  {
//        String qualifier = "num";
//        int PUT_NUM_PER_BATCH = 200;
////        Table table = Connection2hbase.getTable(tableName);
//        JSONObject detailResult = data.getJSONObject("detail_result");
//        JSONArray rows = detailResult.getJSONArray("rows");
//        JSONArray series = detailResult.getJSONArray("series");
//        List<Put> puts = new ArrayList<>();
//        int count = 0;
//
//        for (int i = 0; i < rows.size(); i++) {
//            String day = (String)series.get(i);
//
//            JSONObject jo = rows.getJSONObject(i);
//            JSONArray joValues = jo.getJSONArray("values");
//            JSONArray byValues = jo.getJSONArray("by_values");
//            JSONArray eventIndicator = jo.getJSONArray("event_indicator");
//            StringJoiner byJoiner = new StringJoiner(Constants.SEPARATOR);
//            byValues.forEach(x-> byJoiner.add(x.toString()));
//            for (int d = 0; d < joValues.size(); d++) {
//                JSONArray eachDatas = joValues.getJSONArray(d);
//
//                for (int x = 0; x < eachDatas.size(); x++) {
//                    String actionIndicator =  eventIndicator.getString(x);
//                    String eachData = eachDatas.getString(x);
//                    // row
//                    String rowKey = String.join (Constants.SEPARATOR,
//                            Integer.toString(reportId),
//                            day,
//                            actionIndicator,
//                            byJoiner.toString());
//                    Put put = new Put(Bytes.toBytes(rowKey));
//                    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(qualifier), Bytes.toBytes(eachData));
//                    puts.add(put);
//                    if (count % PUT_NUM_PER_BATCH == 0) {
////                        try {
////                            //table.put(puts);
////                            puts.clear();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                }
//            }
////            if (!puts.isEmpty()) {
////                try {
////                    table.put(puts);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
//    }
////        rows.forEach(
////        });
////        try {
////            table.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//    }
//
//
//    public static void main(String[] args) throws  Exception{
//        CustomActionServiceImpl impl = new CustomActionServiceImpl();
//        String jostr = "{\"filter\":{\"conditions\":[{\"type\":\"event.language\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"ar\",\"en\",\"en-CC\",\"en-CO\",\"en-DE\",\"en-FR\",\"en-GB\",\"en-HK\",\"en-ID\",\"en-IN\",\"en-IT\",\"en-JP\",\"en-KR\",\"en-MX\",\"nl\",\"zh-Hans-AC\",\"zh-Hans-AD\",\"zh-Hans-AE\",\"zh-Hans-AF\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190613\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.channelid\",\"function\":\"equal\",\"params\":[\"001\",\"002\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"userid\",\"eventOriginal\":\"cf_aefzt\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"cf_aefzt_ccxq\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.element_id\",\"function\":\"equal\",\"params\":[\"gd_gdzx_jer_ljsq\",\"gd_gdzx_wddk_edjl\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}}],\"data\":{\"rollup_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\"],\"total_rows\":2,\"by_fields\":[\"event.country\",\"event.region\"],\"num_rows\":2,\"rows\":[{\"values\":[[\"2\"]],\"by_values\":[\"中国\",\"北京\"],\"event_indicator\":[\"axzh_sz\\001acc\"]},{\"values\":[[\"1\"]],\"by_values\":[\"中国\",\"江苏\"],\"event_indicator\":[\"axzh_sz\\001acc\"]}]},\"detail_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\"],\"total_rows\":2,\"by_fields\":[\"event.country\",\"event.region\"],\"num_rows\":2,\"rows\":[{\"values\":[[\"2\"],[\"0\"],[\"0\"]],\"by_values\":[\"中国\",\"北京\"],\"event_indicator\":[\"axzh_sz\\001acc\"]},{\"values\":[[\"0\"],[\"0\"],[\"1\"]],\"by_values\":[\"中国\",\"江苏\"],\"event_indicator\":[\"axzh_sz\\001acc\"]}]}},\"report_name\":\"事件分析保存报表\",\"description\":\"\"}";
//        JSONObject jo;
//        jo = JSONObject.parseObject(jostr);
////        impl.saveQueryTask(jo);
//
//
//        String con;
////        con = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"all\"],\"to_date\":\"20190613\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"C105\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}}]}";
////        JSONObject j = JSONObject.parseObject(con);
////        impl.getQueryResult(j);
////        con = "{\"jobId\":\"f77be63b-c697-4802-8ff3-ebef23b3fb86\",\"result\":[\"$appClick\\u0001acc\\u0001B\\u0001null\\u0001810044\",\"$appClick\\u0001acc\\u0001A\\u0001null\\u00012323405\",\"$appClick\\u0001acc\\u0001C\\u0001null\\u00015\",\"$appClick\\u0001acc\\u0001A\\u000120190611\\u0001783011\",\"$appClick\\u0001acc\\u0001B\\u000120190611\\u0001279643\",\"$appClick\\u0001acc\\u0001C\\u000120190612\\u00015\",\"$appClick\\u0001acc\\u0001A\\u000120190612\\u0001767536\",\"$appClick\\u0001acc\\u0001B\\u000120190612\\u0001267069\",\"$appClick\\u0001acc\\u0001B\\u000120190613\\u0001263332\",\"$appClick\\u0001acc\\u0001A\\u000120190613\\u0001772858\"]}";
////
////        jo = JSONObject.parseObject(con);
////        Tuple3<JSONArray, JSONArray, JSONArray> commReturn = impl.commRetrunOp(j);
////        impl.resultOp(jo,commReturn);
//
//        con = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"all\"],\"to_date\":\"20190613\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"4G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.model\",\"function\":\"equal\",\"params\":[\"C105\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}}],\"data\":{\"rollup_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\"],\"total_rows\":1,\"by_fields\":[\"all\"],\"num_rows\":1,\"rows\":[{\"values\":[[\"2323405\",\"810044\",\"5\"]],\"by_values\":[\"all\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"$appClick\\u0001acc\\u0001B\",\"$appClick\\u0001acc\\u0001C\"]}]},\"detail_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\"],\"total_rows\":1,\"by_fields\":[\"all\"],\"num_rows\":1,\"rows\":[{\"values\":[[\"783011\",\"279643\",\"0\"],[\"767536\",\"267069\",\"5\"],[\"772858\",\"263332\",\"0\"]],\"by_values\":[\"all\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"$appClick\\u0001acc\\u0001B\",\"$appClick\\u0001acc\\u0001C\"]}]}},\"report_name\":\"事件分析保存报表\",\"description\":\"\"}";
//        jo = JSONObject.parseObject(con);
//        impl.saveQueryTask(jo);
//        String taskSQL = "select action, INDICATORTYPE, 'all', an, SUM(ct) as num from (SELECT  action, day, 'acc' AS INDICATORTYPE, 'A' AS an, COUNT(1) AS ct  FROM parquetTmpTable WHERE ( productid = '11148' ) AND (category = 'event') AND ( action = '$appClick' )and(network IN  ('4G', 'WIFI'))  GROUP BY action, day UNION SELECT  action, day, 'acc' AS INDICATORTYPE, 'B' AS an, COUNT(1) AS ct  FROM parquetTmpTable WHERE ( productid = '11148' ) AND (category = 'event') AND ( action = '$appClick' )and(platform IN  ('iOS'))  GROUP BY action, day UNION SELECT  action, day, 'acc' AS INDICATORTYPE, 'C' AS an, COUNT(1) AS ct  FROM parquetTmpTable WHERE ( productid = '11148' ) AND (category = 'event') AND ( action = '$appClick' )and(model IN  ('C105'))  GROUP BY action, day) group by action, day,INDICATORTYPE,an";
//    }
//
//
//
//}
