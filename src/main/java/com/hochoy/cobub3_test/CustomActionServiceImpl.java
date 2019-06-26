package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.cobub.analytics.web.entity.*;
//import com.cobub.analytics.web.mapper.*;
//import com.cobub.analytics.web.service.CustomActionService;
//import com.cobub.analytics.web.service.UserMetadataService;
//import com.cobub.analytics.web.util.*;
//import com.cobub.analytics.web.util.http.CobubHttpClient;
//import com.cobub.analytics.web.util.http.JobServer;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;
import scala.Tuple6;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义事件（事件分析）
 */
//@Service
public class CustomActionServiceImpl   {

    private static final Logger logger = LoggerFactory.getLogger(CustomActionServiceImpl.class);
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String EQUAL = " = ";
    public static final String BOOLEANTYPE = "BooleanType";
    public static final String EQUAL_TRUE = " = true ";
    public static final String EQUAL_FALSE = " = false ";
    public static final String LEFT_BRACKET = "(";
    public static final String UNDER_LINE = "_";
    public static final String SINGLE_QUOTE = "'";
    public static final String EQUAL_SINGLE_QUOTE = " = '";
    public static final String DB_PARQUET = " e1.";//表parquetTmpTable别名
    public static final String DB_USER = " e2.";//表usersTable别名

//    @Autowired
//    private JobServer jobServer;

//    @Value("${spark.query.maxLine}")
    private String maxLine = "1000";

//    @Value("${spark.export.query.maxLine}")
    private String exportMaxLine;

//    @Value("${hbasenamespace}")
    private String hbaseNameSpace = "cobub3";

//    @Autowired
//    private UserMetadataService userMetadataService;

//    @Autowired
//    private ActionReportMapper actionReportMapper;

//    @Autowired
//    private ReportIndividMapper reportIndividMapper;
//
//    @Autowired
//    private MetadataMapper metadataMapper;
//
//    @Autowired
//    private MetaTypeMapper metaTypeMapper;
//
//    @Autowired
//    private UserMetadataMapper userMtadataMapper;
//
//
//    @Autowired
//    private UserMetaTypeMapper userMetaTypeMapper;
//
//    @Autowired
//    private JobHistoryDao jobHistoryDao;

//    @Override
    public JSONObject getQueryResult(JSONObject jsonObject) throws IOException {


        String productId = jsonObject.getString(Constants.PRODUCTID);
        String from = jsonObject.getString(Constants.FROM_DATE);
        String to = jsonObject.getString(Constants.TO_DATE);
        String unit = jsonObject.getString(Constants.UNIT);

        StringJoiner commWhere = new StringJoiner(" AND ");
//
        String dateCon = String.format("( productid = '%s' AND day >= '%s' AND  day <= '%s' )", productId, from, to);
        commWhere.add(dateCon);


        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);


        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
        String relation = filters.getString("relation"); // 主查询条件 逻辑关系 and / or


        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标

        // 根据分组字段 by_fields 拼接 需要 【select 的字段和 group by 字段 】
        /**
         *  返回 分组 + 查询 字段
         *  return (outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction)
         */

        Tuple6 selectAndGroupBy = byFieldOp(fields,productId);
//        Tuple4 selectAndGroupBy = new Tuple4(by._1(), by._2(), by._3(), by._4());


        /**
         * 外部查询 where 条件
         * return ( userWhere,actionWhere)
         */
        Tuple5 filter = queryConditionOp(conditions, relation);
//        Tuple2 filter = new Tuple2(queryCondition._1(), queryCondition._2());


        Map map = new HashMap();
        map.put("relation", relation);
        map.put("commWhere", commWhere);
        map.put("productId", productId);

        /**
         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
         */
        Tuple2<String,String> sqlOps = actionToSingleIndicatorSQL(actions, selectAndGroupBy, filter, map);


        String res0 = "";
        JSONObject responseResult = JSONObject.parseObject(res0);//querySparkSql(new StringBuilder(sqlOps._1), maxLine,sqlOps._2);

        if (responseResult == null || (responseResult.containsKey("status") && responseResult.getString("status").equalsIgnoreCase("error"))) {
            return new JSONObject();
        }
        System.out.println("responseResult.....................\n"+responseResult);

        JSONArray jsonArray = responseResult.getJSONArray("result");
        System.out.println("jsonArray........................    \n"+jsonArray);

        Tuple3<JSONArray, JSONArray, JSONArray> commReturn = commRetrunOp(jsonObject);

        JSONObject result = resultOp(responseResult, commReturn);



        return result;
    }

    private Tuple3<JSONArray,JSONArray,JSONArray> commRetrunOp(JSONObject jsonObject ) {
        JSONArray idxs = new JSONArray();

        JSONArray action = jsonObject.getJSONArray("action");
        action.forEach(x->{
            JSONObject jo = JSONObject.parseObject(x.toString());
            String eventOriginal = jo.getString("eventOriginal");
            String eventType = jo.getString("eventType");
            idxs.add(eventOriginal + separator+ eventType);
        });


        JSONArray series = new JSONArray();
        String from = jsonObject.getString("from_date");
        String to = jsonObject.getString("to_date");
        List<String> dayOfRange = DateUtil.getDayOfRange(from, to);
        dayOfRange.forEach(x-> series.add(x));

        JSONArray by_fields = new JSONArray();

        jsonObject.getJSONArray("by_fields").forEach(x->{
            by_fields.add(x.toString());
        });
        return new Tuple3<>(idxs,series,by_fields);
    }


    String separator = "@@@@@@@";
    private JSONObject resultOp(JSONObject jo, Tuple3<JSONArray,JSONArray,JSONArray> commReturn) {

//        JSONArray idxs = commReturn._1();

        JSONArray series = commReturn._2();

        JSONArray by_fields = commReturn._3();


        Set idxs__ = new LinkedHashSet();
        JSONArray result = jo.getJSONArray("result");

        Map m = new HashMap();
        result.forEach(x->{
            String[] split = x.toString().split(Constants.SEPARATOR_U0001);
            int len = split.length;
            String flag = split[len-3];
            String indicator = split[len-4];
            String action = split[len-5];
            m.putIfAbsent(flag,String.join(separator,action,indicator));
//            idxs__.add(String.join(separator,action,indicator,flag));
        });
        Map<String,String> ms = new LinkedHashMap<>();
        m.entrySet().stream().sorted(Map.Entry.<String,String>comparingByKey()).forEachOrdered(e -> {Map.Entry em = (Map.Entry)e; ms.putIfAbsent((String)em.getKey(), (String)em.getValue());});
        ms.forEach((k,v)-> idxs__.add(String.join(separator,v,k)));


        LinkedList idxs = new LinkedList();
        idxs__.forEach(x-> idxs.add(x.toString()));
        Map<String, Map<String, Map<String, String>>> result1 = new HashMap<>();
        //  分组        event_idx    date      num
        //  分组        date        event_idx  num

        result.forEach(x -> {
            String[] split = x.toString().split(Constants.SEPARATOR_U0001);
            int len = split.length;
            String num = split[len - 1]; //
            String date = split[len - 2];
            String flag = split[len-3];
            String indicator = split[len-4];
            String action = split[len-5];

            if ("null".equals(date)){
                date = "cnt";
            }
            Set bySet = new LinkedHashSet();
            for (int i = 0; i < len - 5; i++) {
                if(!"null".equals(split[i])){
                    bySet.add(split[i]);
                }else {
                    bySet.add("unknown");
                }
            }
            String by;
            if(bySet.isEmpty() ||  (by_fields.size()==1 && "all".equalsIgnoreCase(by_fields.getString(0)))){
                by = "all";
            }else{
                by = String.join(separator,bySet);
            }
            Map<String,Map<String,String>> dateIdxNum = result1.get(by); // date:<idx,num>
            String idx = String.join(separator,action,indicator,flag);

            Map<String,String> idxNum;
            if (dateIdxNum == null) {
                dateIdxNum = new HashMap<>();
                idxNum = new HashMap<>();
                idxNum.put(idx,num);
                dateIdxNum.put(date, idxNum);
            } else {
//                eventIdxMap.put(eventIdx, date_num);
                Map<String, String> idx_Num = dateIdxNum.get(date);
                if (null == idx_Num){

                    idxNum = new HashMap<>();
                    idxNum.put(idx,num);
                    dateIdxNum.put(date, idxNum);
                }else {
                    idx_Num.put(idx,num);
                    dateIdxNum.put(date,idx_Num);
                }
            }
            result1.put(by, dateIdxNum);
        });
        System.out.println(result1);


        LinkedList eventIndicatorList = new LinkedList();
        idxs.forEach(v ->{
            String[] split = v.toString().split(separator, 3);
            eventIndicatorList.add(split[0]+"->"+split[1]);
        });
        JSONArray indicator = new JSONArray(eventIndicatorList);

        JSONArray detailRows = new JSONArray();
        JSONArray rollupRows = new JSONArray();
        result1.forEach((groupBy, dateIdxNum) -> {

            Map<String, Map<String, String>> event = dateIdxNum;
            Map<String, String> cnt = event.get("cnt"); JSONArray cntNums = new JSONArray();
            idxs.forEach(idx ->{
                String num = cnt.get(idx.toString());
                if (null != num){
                    cntNums.add(num);
                }else {
                    cntNums.add("0");
                }

            });
            JSONArray rollupValues = new JSONArray();
            rollupValues.add(cntNums);

            JSONObject rollupRowChild = new JSONObject();
            JSONObject detailRowChild = new JSONObject();
            JSONArray values = new JSONArray();
            series.forEach(x->{
                String date = x.toString();
                Map<String, String> idxNum = event.get(date);
                JSONArray everyDayNums = new JSONArray();
                if (null != idxNum){
                    idxs.forEach(idx -> {
                        String index = idx.toString();
                        String num = idxNum.get(index);
                        if (null != num){
                            everyDayNums.add(num);
                        }else {
                            everyDayNums.add("0");
                        }
                    });
                }else {
                    idxs.forEach(idx-> {
                        everyDayNums.add("0");
                    });
                }
                values.add(everyDayNums);
                //values.add(indexArr);
            });

            JSONArray byValues = new JSONArray();
            Arrays.asList(groupBy.split(separator)).forEach(x-> byValues.add(x));

            detailRowChild.put("values",values);
            detailRowChild.put("by_values",byValues);
            detailRowChild.put("event_indicator",indicator);
            detailRows.add(detailRowChild);


            rollupRowChild.put("values",rollupValues);
            rollupRowChild.put("by_values",byValues);
            rollupRowChild.put("event_indicator",indicator);
            rollupRows.add(rollupRowChild);



        });


        JSONObject baseJo = new JSONObject();
        baseJo.put("by_fields",by_fields);
        baseJo.put("series",series);

        JSONObject detailResult = new JSONObject();
        JSONObject rollupResult = new JSONObject();
        detailResult.put("rows",detailRows);
        detailResult.put("num_rows",detailRows.size());
        detailResult.put("total_rows",detailRows.size());
        detailResult.putAll(baseJo);


        rollupResult.put("rows",rollupRows);
        rollupResult.put("num_rows",rollupRows.size());
        rollupResult.put("total_rows",rollupRows.size());
        rollupResult.putAll(baseJo);

        JSONObject  res0 = new JSONObject();
        res0.put("detail_result",detailResult);
        res0.put("rollup_result",rollupResult);
        System.out.println("res0............"+res0);


        return res0 ;


    }



    /**
     * @param actions    actions
     * @param byFields   Tuple6(
     *                   StringJoiner：outGroupByUser,
     *                   StringJoiner：outGroupByAction,
     *                   HashSet:outSelectFieldUser,
     *                   HashSet:outSelectFieldAction
     *                   HashMap:
     *                   )
     * @param outFilters Tuple2 ( outUserWhere,outActionWhere)
     * @param map
     */
    private Tuple2<String,String> actionToSingleIndicatorSQL(JSONArray actions,
                                              Tuple6 byFields,
                                              Tuple5 outFilters,
                                              Map map) {
        String parquetSQL = "SELECT  %s  FROM parquetTmpTable WHERE %s  GROUP BY %s";
        String joinSQL = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE   %s  ) tu " +
                "ON concat_ws('_', '%s', ta.global_user_id) = tu.pk where %s ";
        String partialAggSQLFormat = "select %s from %s group by %s"; // (groupBy + action + indicatorType) , joinSQL ,groupBy
        String joinSqlNoWhere = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE %s ) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk ";


        HashSet<String> groupId = new HashSet();
        JSONObject props = new JSONObject();
        List<String> sqlList = new ArrayList<>();


        final StringJoiner outGroupByUser = (StringJoiner) byFields._1();
        final StringJoiner outGroupByAction = (StringJoiner) byFields._2();
        final HashSet outSelectFieldUser = (HashSet) byFields._3();//out 根据分组字段 by_fields 筛出来的 user 查询字段
        final HashSet outSelectFieldAction = (HashSet) byFields._4();//out 根据分组字段 by_fields 筛出来的 acton 查询字段
        HashMap<String, String> groupByUserProp2Type = (HashMap<String, String>) byFields._5();//根据分组字段筛出来的 用户属性和属性数据类型
        HashSet<String> userGroupIds = (HashSet<String>) byFields._6(); // 根据分组字段筛出来的 用户分群ID集合

        //filter: ( new Tuple5<>(userWhere, actionWhere, groupId,actionFields, userProps);
        // _1:user 的where条件，
        // _2:action 的where条件，
        // _3:用户分群id的set集合，
        // _4:查询条件中action属性列名set集合，
        // _5:用户属性名及其类型的map
        // )
        final StringJoiner outUserWhere = (StringJoiner) outFilters._1(); //根据外部条件 filter 处理的 user 过滤条件
        final StringJoiner outActionWhere = (StringJoiner) outFilters._2();  //根据外部条件 filter 处理的 action 过滤条件

        HashSet<String> groupIdOut = (HashSet<String>) outFilters._3();
        HashSet<String> actionFieldsOut = (HashSet<String>) outFilters._4(); // //根据外部条件 filter 处理的 action 过滤涉及的 列
        HashMap<String, String> userProp2TypeOut = (HashMap<String, String>) outFilters._5();
        props.putAll(userProp2TypeOut);

        StringJoiner groupByJoiner = new StringJoiner(",");//
        if (!outGroupByAction.toString().isEmpty()) {
            groupByJoiner.add(outGroupByAction.toString());
        }
        if (!outGroupByUser.toString().isEmpty()) {
            groupByJoiner.add(outGroupByUser.toString());
        }

        String partialAggGroupBy ;
        if (!groupByJoiner.toString().isEmpty()){
            partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action", "day");
        }else {
            partialAggGroupBy = String.join(", ", "action", "day");
        }


        String relation = (String) map.get("relation");//主查询条件 and / or
        final String commWhere = ((StringJoiner) map.get("commWhere")).toString();
        Boolean outOr = Constants.OR.equalsIgnoreCase(relation) && (!outActionWhere.toString().isEmpty()) && (!outUserWhere.toString().isEmpty());
        final String productId = (String) map.get("productId");
        String userCommWhere = String.format("(pk > '%s_' AND pk < '%s_a')", productId, productId);
        AtomicInteger i = new AtomicInteger('A');


        actions.forEach(v -> {

            JSONObject action = JSONObject.parseObject(String.valueOf(v));
            StringJoiner commWhere1 = new StringJoiner(" AND ");

            String eventOriginal = action.getString("eventOriginal");

            String eventType = action.getString("eventType");

            String category = SQLUtil.getCategory(eventOriginal);
            String indicatorType = SQLUtil.getIndicatorType(eventType)._1;
            String eventCol = SQLUtil.getIndicatorType(eventType)._2;
//            String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action");
            commWhere1.add(commWhere).add(String.format("(category = '%s')", category)).add(String.format("( action = '%s' )",eventOriginal));
//            System.out.println("commWhere1----------------------------------:               " + commWhere1.toString());
            JSONObject childFilterParam = action.getJSONObject(Constants.CHILDFILTERPARAM);
            String relate = childFilterParam.getString("relation");
            JSONArray conditions = childFilterParam.getJSONArray("conditions");
            /**
             //filter: ( new Tuple5<>(userWhere, actionWhere, groupId,actionFields, userProps);
             // _1:user 的where条件，
             // _2:action 的where条件，
             // _3:用户分群id的set集合，
             // _4:查询条件中action属性列名set集合，
             // _5:用户属性名及其类型的map
             // )
             */
            Tuple5 queryCondition = queryConditionOp(conditions, relate);
//            Tuple2<StringJoiner, StringJoiner> inCondition = new Tuple2(queryCondition._1(), queryCondition._2());
            StringJoiner inUserWhere = (StringJoiner) queryCondition._1();
            StringJoiner inActionWhere = (StringJoiner) queryCondition._2();
            //用户分群id的set集合
            HashSet<String> groupIdIn = (HashSet<String>) queryCondition._3();
            groupIdIn.forEach(vv -> groupId.add(vv));

            HashSet<String> actionFieldsIn = (HashSet<String>) queryCondition._4();//查询条件中action属性列名set集合
            HashMap<String, String> userProp2TypeIn = (HashMap<String, String>) queryCondition._5();
            userGroupIds.addAll(groupIdIn);
            props.putAll(userProp2TypeIn);
            userProp2TypeIn.forEach((key, value) -> groupByUserProp2Type.merge(key, value, (vF, vB) -> vF));


            Boolean inOr = Constants.OR.equalsIgnoreCase(relate) && (!inActionWhere.toString().isEmpty()) && (!inUserWhere.toString().isEmpty());
//            Boolean outOr = !(outActionWhere.toString().isEmpty() || outUserWhere.toString().isEmpty());


            HashSet<String> actionSelectSet = new HashSet<>();
            actionSelectSet.addAll(Arrays.asList("global_user_id", "action", "day"));
            actionSelectSet.addAll(outSelectFieldAction);
            actionSelectSet.add(eventCol);

            HashSet<String> userSelectSet = new HashSet<>();
            userSelectSet.add("pk");
            userSelectSet.addAll(outSelectFieldUser);

            // actionSelect : global_user_id, 分组字段，action，day

//            String actionSelect = String.join(", ", actionSelectSet);// actionSelect 基本筛选条件
            String userSelect = String.join(", ", userSelectSet);   //userSelect 基本筛选条件 : pk, 分组字段


            //joinselect : 分组字段，action，day, 指标 , 'A' AS an
            String joinSelect ;
            if (!groupByJoiner.toString().isEmpty()){
                joinSelect = String.join(", ", groupByJoiner.toString(), "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", (char) (i.getAndAdd(1))), indicatorType);
            }else {
                joinSelect = String.join(", ",  "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", (char) (i.getAndAdd(1))), indicatorType);
            }
//            String.join(",", joinSelect, indicatorType);
//            vvv= vvv+1;
            //String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action","day");

            if (outGroupByUser.toString().isEmpty() && inUserWhere.toString().isEmpty() && outUserWhere.toString().isEmpty()) {
                //分组、内部条件、外部条件中只有事件属性，只查 parquet 表
//                String select = String.join(", ", "global_user_id", "action", outSelectFieldAction.toString());
//                String groupBy = String.join(", ", "action", outGroupByAction.toString()); //
                StringJoiner actionWhere = getAllActionWhere(commWhere1, outActionWhere, inActionWhere);
                String singleSQL;
                singleSQL = String.format(parquetSQL, joinSelect, actionWhere.toString(), partialAggGroupBy);
                sqlList.add(singleSQL);
            } else {
                // ( Constants.OR.equalsIgnoreCase(relate) && inOr)
                if (inOr || outOr) {
                    // 在or 条件下，且查询条件同时含有action 和 user属性，则过滤条件不能下推到最底层的
                    // usersTable 和 parquetTmpTable 中过滤，需要放在 usersTable join parquetTmpTable
                    //  后的条件中
                    String actionWhere = String.join(Constants.AND, commWhere1.toString());
                    String actionWhere1 = actionWhere;
                    String userWhere = String.join(Constants.AND, userCommWhere);
                    String userWhere1 = userWhere;

                    HashSet<String> actionSelectSetOr = (HashSet<String>) actionSelectSet.clone();

                    HashSet<String> userSelectSetOr = (HashSet<String>) userSelectSet.clone();
                    // or


                    if (inOr && outOr) {

                        //outActionWhere,outUserWhere ,inActionWhere,inUserWhere 都不为空，且inOr,outOr 都是 OR
                        // (outActionWhere OR outUserWhere)  and (inActionWhere OR inUserWhere) 放到join where 后
                        // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
                        //  parquetTmpTable 的select列中需要包含 or 条件中包含的列（包含out 和 in)
                        //  usersTable 的select列中需要包含 or 条件中包含的列（包含out 和 in)

                        actionSelectSetOr.addAll(actionFieldsOut);
                        actionSelectSetOr.addAll(actionFieldsIn);
                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(", ", String.format("'%s' AS INDICATORTYPE",eventType));
                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
                        userSelectSetOr.addAll(groupIdIn);
                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);


                        String outWhere = new StringJoiner(Constants.OR, "(", ")").add(outActionWhere.toString()).add(outUserWhere.toString()).toString();
                        String inWhere = new StringJoiner(Constants.OR, "(", ")").add(inActionWhere.toString()).add(inUserWhere.toString()).toString();
                        String joinWhere = new StringJoiner(Constants.AND).add(outWhere).add(inWhere).toString();

                        userWhere1 = userCommWhere;
                        actionWhere1 = commWhere1.toString();
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
                        sqlList.add(partialAggSQL);
                    } else if (inOr && (!outOr)) {
                        // inWhere 条件   放到  usersTable join parquetTmpTable 后,
                        // outWhere 放到 对应 usersTable 和 parquetTmpTable 表 的where 后
//                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));

                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ in)
                        //   usersTable 的select列中需要包含 or 条件中包含的列（ in)

                        actionSelectSetOr.addAll(actionFieldsIn);
                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(",", String.format("'%s' AS INDICATORTYPE",eventType));
                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
                        userSelectSetOr.addAll(groupIdIn);
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);
                        String joinWhere = String.join(Constants.OR, inActionWhere.toString(), inUserWhere.toString());
//                        String actionWhere = String.join(Constants.AND, commWhere1.toString());
                        if (!outActionWhere.toString().isEmpty()) {
                            actionWhere1 = actionWhere.join(relate, outActionWhere.toString());
                        }
//                        String userWhere = String.join(Constants.AND, userCommWhere);
                        if (!outUserWhere.toString().isEmpty()) {
                            userWhere1 = userWhere.join(Constants.AND, outUserWhere.toString());
                        }
//                        String joinWhere
                        //actionSelect ,actionWhere , userSelect , userWhere  productId,joinWhere, joinGroupBy(action ... )
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
//                        String partialAggSQLFormat = "select %s from %s group by %s";

                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
                        sqlList.add(partialAggSQL);
//
                    } else if (!inOr && outOr) {
                        //1. outActionWhere  outUserWhere  都是非空 ，且 or 关系，放到join where 后
                        //2. inActionWhere inUserWhere 可能为空 或 为 and 关系
                        // outWhere 条件   放到  usersTable join parquetTmpTable 后
                        // ,inWhere 放到 对应的 usersTable 和 parquetTmpTable 表 的where 后
//                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));
                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ out ，把 out 条件中的 action 列加到parquet表的 select 字段中)
                        //   usersTable 的select列中需要包含 or 条件中包含的列（ out 把 out 条件中的 users列加到parquet表的 select 字段中))

                        actionSelectSetOr.addAll(actionFieldsOut);
                        String actionSelectOr = String.join(", ", actionSelectSetOr).join(",", String.format("'%s' AS INDICATORTYPE",eventType));
                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);

                        String joinWhere = String.join(Constants.OR, outActionWhere.toString(), outUserWhere.toString());
//                        String actionWhere = String.join(Constants.AND, commWhere1.toString());
                        if (!inActionWhere.toString().isEmpty()) {
                            actionWhere1 = actionWhere.join(relate, inActionWhere.toString());
                        }
//                        String userWhere = String.join(Constants.AND, userCommWhere);
                        if (!inUserWhere.toString().isEmpty()) {
                            userWhere1 = userWhere.join(relate, inUserWhere.toString());
                        }
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
                        sqlList.add(partialAggSQL);

                    }
//                    String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
//                    String partialAggSQL = String.format(partialAggSQLFormat, String.join(",", joinSelect, indicatorType), joinSQL1, groupBy);
//                    System.out.println("partialAggSQL........................." + partialAggSQL);

                } else {
                    // 所有条件均分别下推到 userTable 和 parquetTmpTable 表的where  后
                    // join 后没有where
                    // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
                    // actionWhere: outActionWhere and inActionWhere ->
                    // userWhere ：outUserWhere and inUserWhere -->
                    //
                    StringJoiner actionWhere = getAllActionWhere(commWhere1, outActionWhere, inActionWhere);
//                    new StringJoiner(Constants.AND);
//                    actionWhere.add(commWhere1.toString());
//                    if (!outActionWhere.toString().isEmpty()) {
//                        actionWhere.add(outActionWhere.toString());
//                    }
//                    if (!inActionWhere.toString().isEmpty()) {
//                        actionWhere.add(inActionWhere.toString());
//                    }
                    String actionSelect = String.join(", ", actionSelectSet).join(",",String.format("'%s' AS INDICATORTYPE",eventType));
                    StringJoiner userWhere = new StringJoiner(Constants.AND);
                    userWhere.add(userCommWhere);
                    if (!outUserWhere.toString().isEmpty()) {
                        userWhere.add(outUserWhere.toString());
                    }
                    if (!inUserWhere.toString().isEmpty()) {
                        userWhere.add(inUserWhere.toString());
                    }
                    actionWhere.toString();
                    String joinSQL1 = String.format(joinSqlNoWhere, actionSelect, actionWhere.toString(), userSelect, userWhere.toString(), productId);
                    String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
                    sqlList.add(partialAggSQL);

                }


            }


        });

        String SQL = "";

        SQL = String.join(" UNION ", sqlList);
        String allSelect ;
        if (!groupByJoiner.toString().isEmpty()){
            allSelect = String.join(", ", groupByJoiner.toString(), "action","INDICATORTYPE","an", "day", "SUM(ct)");
        }else{
            allSelect = String.join(", ", "action","INDICATORTYPE","an", "day", "SUM(ct)");
        }
//        String allSelect = String.join(", ", partialAggGroupBy,"INDICATORTYPE", "SUM(ct)");

        String allGroupBy = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an");

        String grouping;
        if(!groupByJoiner.toString().isEmpty()){
            grouping = String.join(",", groupByJoiner.toString(), "action", "INDICATORTYPE","an");
        }else {
            grouping = String.join(",", "action", "INDICATORTYPE","an");
        }
        String groupingSet = String.format("GROUPING SETS(( %s ),(%s) ) ORDER BY %s", String.join(",",partialAggGroupBy,"INDICATORTYPE","an"), grouping,partialAggGroupBy); // groupby + action

        String group = String.join(" ", allGroupBy, groupingSet);
        String allSQL = String.format("select %s from (%s) group by %s ", allSelect, SQL, group);


        userGroupIds.forEach(v -> groupId.add(v));
        groupIdOut.forEach(v -> groupId.add(v));


        props.putAll(groupByUserProp2Type);

        StringJoiner prop = new StringJoiner(",","{","}");
        props.forEach((userPropName,userPropDataType) ->{
            prop.add(String.format("\\\"%s\\\":\\\"%s\\\"",userPropName,userPropDataType));
        });

        StringJoiner groupIds = new StringJoiner(",", "{", "}");
        groupId.forEach(xx->groupIds.add(xx));
        String taskSql = "\",namespace=\""+hbaseNameSpace+"\",prop=\""+prop.toString()+"\",groupid=\""+groupIds.toString()+"\"";

        System.out.println("props:                   \"" + prop.toString() + "\"");
        System.out.println("groupIds:                   \"" + groupIds.toString() + "\"");

        System.out.println("taskSql:                   " + taskSql   );
        System.out.println("allSQL:                    " + allSQL     );


        return new Tuple2<>(allSQL,taskSql);
    }




    /**
     * 根据 每一组的 查询条件 和 条件关系(adn/or) 拼接 每一组 action 表和user表的where 条件
     *
     * @param conditions 查询条件集合
     * @param relation   条件间的逻辑关系 ：and / or
     * @return 五元组：(
     * action的where条件，user的where条件，用户分群id的set集合，查询条件中action属性列名set集合，用户属性名及其类型的map)
     */
   private Tuple5<
            StringJoiner, StringJoiner,
            Set<String>, Set<String>,
            Map<String, String>
            > queryConditionOp(JSONArray conditions, String relation) {

        StringJoiner actionWhere = new StringJoiner(String.format(" %s ", relation)); // action where 条件
        StringJoiner userWhere = new StringJoiner(String.format(" %s ", relation));
        Map<String, String> userProps = new HashMap<>();
        Set<String> groupId = new HashSet<>();
        Set<String> actionFields = new HashSet<>();

        conditions.forEach(v -> {
            JSONObject condition = JSONObject.parseObject(String.valueOf(v));
            String type = condition.getString("type");  //event.country, user.age, userGroup.benyueqianzaitouziyonghu
            String function = condition.getString("function");
            JSONArray params = condition.getJSONArray("params");
            String isNumber = condition.getString("isNumber");
            String[] split = type.split("\\.", 2);
            String column = split[1];
            String con;
            switch (split[0]) {
                case "event": {
                    actionFields.add(column);// eventWhere = eventWhereFunctionOp(type, function, params, eventWhere);
                    switch (function) {
                        case "equal": { // string  and  Number
                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
                            if ("isTrue".equals(isNumber)) {
                                params.forEach(x -> joiner.add(String.format("'%s'", x)));
                            } else {
                                params.forEach(x -> joiner.add(String.format("'%s'", x)));
                            }

                            con = String.format("(%s IN  %s)", column, joiner.toString());
                            actionWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  Number
                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
                            params.forEach(x -> joiner.add(String.format("'%s'", x)));
                            con = String.format("(%s NOT IN  %s)", column, joiner.toString());
                            actionWhere.add(con);// IN  v.s NOT IN
                            break;
                        }
                        case "contain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", column, column, param));// LIKE  v.s    LIKE
                            break;
                        }
                        case "isTrue":
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", column, column));
                            break;
                        case "isFalse":
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", column, column));
                            break;
                        case "notContain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", column, column, param));// LIKE  v.s NOT LIKE
                            break;
                        }
                        case "more": {  // Number >
                            String param = params.getString(0);
                            con = String.format("(%s  >  %s)", column, param);
                            actionWhere.add(con);
                            break;
                        }
                        case "less": { // Number <
                            String param = params.getString(0);
                            con = String.format("(%s  <  %s)", column, param);
                            actionWhere.add(con);
                            break;
                        }
                        case "region": {  // Number  between ：  >= and <=
                            String param1 = condition.getString("param1");
                            String param2 = condition.getString("param2");
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", column, param1, column, param2);
                            actionWhere.add(con);
                            break;
                        }
                    }


                    break;
                }
                case "user": {
                    switch (function) {
                        // String:
                        case "equal": { // string  and  Number
                            StringJoiner joiner = new StringJoiner(" or ", "(", ")");
                            if ("isTrue".equals(isNumber)) {
                                params.forEach(x -> joiner.add(String.format("  %s = %s", column, x)));
                                userProps.put(column, "int");
                            } else {
                                params.forEach(x -> joiner.add(String.format("  %s = '%s'", column, x)));
                                userProps.put(column, "string");
                            }
                            con = String.format("( %s IS NOT NULL  AND  %s )", column, joiner.toString());
                            userWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  Number
                            StringJoiner joiner = new StringJoiner(" AND ", "(", ")");  // and v.s and  or
                            if ("isTrue".equals(isNumber)) {
                                params.forEach(x -> joiner.add(String.format("  %s <> %s", column, x)));
                                userProps.put(column, "int");
                            } else {
                                params.forEach(x -> joiner.add(String.format("  %s <> '%s'", column, x)));
                                userProps.put(column, "string");
                            }
                            userWhere.add(String.format("((%s IS NOT NULL ) AND  %s )", column, joiner.toString()));
                            break;
                        }
                        case "contain": { //string like
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", column, column, param);
                            userWhere.add(con);// LIKE  v.s    LIKE
                            userProps.put(column, "string");
                            break;
                        }
                        case "notContain": { //string
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", column, column, param);
                            userWhere.add(con);//NOT LIKE    v.s NOT LIKE
                            userProps.put(column, "string");
                            break;
                        }
                        case "isTrue":
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", column, column));
                            userProps.put(column, "boolean");
                            break;
                        case "isFalse":
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", column, column));
                            userProps.put(column, "boolean");
                            break;
                        case "more": {  // Number >
                            String param = params.getString(0);
                            con = String.format("(%s  >  %s)", column, param);
                            userProps.put(column, "int");
                            userWhere.add(con);
                            break;
                        }
                        case "less": { // Number <
                            String param = params.getString(0);
                            con = String.format("(%s  <  %s)", column, param);
                            userProps.put(column, "int");
                            userWhere.add(con);
                            break;
                        }
                        case "region": {  // Number  between ：  >= and <=
                            String param1 = condition.getString("param1");
                            String param2 = condition.getString("param2");
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", column, param1, column, param2);
                            userProps.put(column, "int");
                            userWhere.add(con);
                            break;
                        }


                    }
                    break;
                }
                case "userGroup": {
                    String param = "false";
                    if ("isTrue".equals(function)) {
                        param = "true";
                    }
                    userWhere.add(String.format("((%s IS NOT NULL ) AND (%s = %s) )", column, column, param));
                    groupId.add(column);
                    break;
                }
            }


        });
        return new Tuple5<>(userWhere, actionWhere, groupId, actionFields, userProps);

    }




    /**
     * 根据分组字段获取 usersTable 和 parquetTmpTable 的 group by 和 select 字段
     * todo 获取usersTable 的 schema
     *
     * @param by_field
     * @return
     */
    private Tuple6<StringJoiner, StringJoiner, HashSet, HashSet,
            HashMap<String, String>,
            HashSet<String>
            > byFieldOp(JSONArray by_field,String productId) {
        Map<String, String> userPropertiesMap = getUserPropertiesAndTypes(productId);
        StringJoiner outGroupByUser = new StringJoiner(", "); // user group by 字段
        StringJoiner outGroupByAction = new StringJoiner(", "); //  action group by 字段

        HashSet outSelectFieldUser = new HashSet(); //  user select  字段
        HashSet outSelectFieldAction = new HashSet(); // action select  字段

        Map<String, String> userProps = new HashMap<>();
        HashSet<String> groupId = new HashSet<>();

        by_field.stream().filter(x->!"all".equalsIgnoreCase(x.toString())).forEach(field -> {
            String[] split = field.toString().split("\\.", 2);
            String type = split[0];
            String column = split[1];
            if ("event".equals(type)) {
                //parquetTmpTable 表
                outGroupByAction.add(column);//  group by 字段拼接
                outSelectFieldAction.add(column); // select 字段拼接

            } else if ("user".equals(type)) {
                //usersTable  表的用户属性
                outGroupByUser.add(column);// group by 字段拼接
                userProps.put(column, userPropertiesMap.get(column));
                outSelectFieldUser.add(column);// select 字段拼接

            } else if ("userGroup".equals(type)) {
                //usersTable  表的用户分群属性处理
                outGroupByUser.add(column);// group by 字段拼接
                groupId.add(column);
                outSelectFieldUser.add(column);// select 字段拼接
            }

        });
        return new Tuple6(outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction, userProps, groupId);
    }

    //todo 用户属性及其类型
    private Map<String, String> getUserPropertiesAndTypes(String productId) {
        Map<String, String> map = new HashMap<>();
//        UserMetaType  userMetaType2  = new UserMetaType();
//        userMetaType2.setProductId(Long.parseLong(productId));
//        userMetaTypeMapper.getAllActiveMetaTypeListForFilter(userMetaType2).forEach(domain->map.put(domain.getType(),domain.getDatatype()) );
        return map;
    }


    private StringJoiner getAllActionWhere(StringJoiner commWhere1, StringJoiner outActionWhere, StringJoiner inActionWhere) {

        StringJoiner actionWhere = new StringJoiner(Constants.AND);
        actionWhere.add(commWhere1.toString());
        if (!outActionWhere.toString().isEmpty()) {
            actionWhere.add(outActionWhere.toString());
        }
        if (!inActionWhere.toString().isEmpty()) {
            actionWhere.add(inActionWhere.toString());
        }
        return actionWhere;

    }



    private void saveDataToHbase(JSONArray actions, Integer reportId, String tableName, JSONObject data) throws IOException {
        JSONArray values = data.getJSONArray("originalValue");

        if (values != null && !values.isEmpty()) {
            JSONArray dates = data.getJSONArray("date");
            JSONObject action = actions.getJSONObject(0);
            String eventType = action.getString(Constants.EVENT_TYPE);

            // 分别为总次数、触发用户数、登录用户数
            String qualifier = "";
            switch (eventType) {
                case "acc":
                    qualifier = "count(action)";
                    break;
                case "userid":
                    qualifier = "count(distinct deviceid)";
                    break;
                case "loginUser":
                    qualifier = "count(distinct userid)";
                    break;
                default:
                    break;
            }

            Table table = Connection2hbase.getTable(tableName);
            List<Put> puts = new ArrayList<>();
            final int size = values.size();
            int count = 0;
            for (int i = 0; i < size; i++) {
                JSONObject value = values.getJSONObject(i);
                JSONArray byValues = value.getJSONArray(Constants.BY_VALUES);
                JSONArray counts = value.getJSONArray(Constants.COUNT);

                // 针对单个总体
                if (!byValues.isEmpty() && byValues.size() == 1 && "总体".equals(byValues.getString(0))){
                    for (int j = 0; j < counts.size(); j++) {
                        String rowKey = String.valueOf(reportId) + Constants.SEPARATOR +
                                dates.getString(j).replaceAll("-", "") + Constants.SEPARATOR;
                        Put put = new Put(Bytes.toBytes(rowKey));
                        String stringValue = String.valueOf(counts.getJSONArray(j).getInteger(0));
                        put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(qualifier), Bytes.toBytes(stringValue));
                        puts.add(put);
                        count++;
                        if (count % 200 == 0) {
                            table.put(puts);
                            puts.clear();
                        }
                    }
                }else {
                    for (int j = 0; j < counts.size(); j++) {
                        String rowKey = String.valueOf(reportId) + Constants.SEPARATOR + dates.getString(j).replaceAll("-", "") +
                                Constants.SEPARATOR + Joiner.on(Constants.SEPARATOR).join(byValues).trim();
                        Put put = new Put(Bytes.toBytes(rowKey));
                        String stringValue = String.valueOf(counts.getJSONArray(j).getInteger(0));
                        put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(qualifier), Bytes.toBytes(stringValue));
                        puts.add(put);
                        count++;
                        if (count % 200 == 0) {
                            table.put(puts);
                            puts.clear();
                        }
                    }
                }
            }

            if (!puts.isEmpty()) {
                table.put(puts);
            }

            table.close();
        }
    }


    public static void main(String[] args)  throws  IOException{

//        Map m = new LinkedHashMap();
//        m.put("C","ccc");
//        m.put("D","ccc");
//        m.put("A","aaa");
//        m.put("B","bbb");
//
//        Map<String,String> ms = Maps.newLinkedHashMap();
//        m.entrySet().stream().sorted(Map.Entry.<String,String>comparingByKey()).forEachOrdered(e -> {Map.Entry em = (Map.Entry)e; ms.put((String)em.getKey(), (String)em.getValue());});

       /* String text;
        text = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\",\"event.city\"],\"to_date\":\"20190614\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[]}}]}";

        JSONObject jo = JSONObject.parseObject(text);
        JSONArray action = jo.getJSONArray("action");
        Integer reportId  = 10001;
        String tableName = "razor_100011";

        String dataStr;
        dataStr = "{\"rollup_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\",\"20190614\"],\"total_rows\":4,\"by_fields\":[\"event.country\",\"event.region\",\"event.city\"],\"num_rows\":4,\"rows\":[{\"values\":[[\"356\",\"356\"]],\"by_values\":[\"中国\",\"江苏\",\"unknown\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"80\",\"80\"]],\"by_values\":[\"中国\",\"江苏\",\"南通\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"33\",\"33\"]],\"by_values\":[\"中国\",\"江苏\",\"宿迁\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"104\",\"104\"]],\"by_values\":[\"中国\",\"江苏\",\"南京\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]}]},\"detail_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\",\"20190614\"],\"total_rows\":4,\"by_fields\":[\"event.country\",\"event.region\",\"event.city\"],\"num_rows\":4,\"rows\":[{\"values\":[[\"73\",\"73\"],[\"91\",\"91\"],[\"91\",\"91\"],[\"101\",\"101\"]],\"by_values\":[\"中国\",\"江苏\",\"unknown\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"16\",\"16\"],[\"27\",\"27\"],[\"16\",\"16\"],[\"21\",\"21\"]],\"by_values\":[\"中国\",\"江苏\",\"南通\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"10\",\"10\"],[\"11\",\"11\"],[\"7\",\"7\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"江苏\",\"宿迁\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]},{\"values\":[[\"35\",\"35\"],[\"28\",\"28\"],[\"23\",\"23\"],[\"18\",\"18\"]],\"by_values\":[\"中国\",\"江苏\",\"南京\"],\"event_indicator\":[\"axzh_sz->acc\",\"axzh_sz->acc\"]}]}}";
        JSONObject data = JSONObject.parseObject(dataStr);

        CustomActionServiceImpl impl = new CustomActionServiceImpl();


        impl.saveDataToHbase(action,reportId,tableName,data);*/
        resultOpTest();


    }




    public static void resultOpTest() throws  IOException{
        CustomActionServiceImpl impl = new CustomActionServiceImpl();


        String text;
        // all group by
        text = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"all\"],\"to_date\":\"20190615\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"cf_aefzt_ccxq\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\"}],\"relation\":\"and\"}},{\"eventType\":\"userid\",\"eventOriginal\":\"cf_lc_lccp_gm\",\"childFilterParam\":{\"conditions\":[]}}]}";
        text = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190615\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"userid\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"cf_aefzt\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"acc\",\"eventOriginal\":\"cf_aefzt\",\"childFilterParam\":{\"conditions\":[]}}]}\n";
        text = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\",\"event.city\"],\"to_date\":\"20190613\",\"productId\":\"10940\",\"action\":[{\"eventType\":\"userid\",\"eventOriginal\":\"dbyq_cf\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"dbyq_sh\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"acc\",\"eventOriginal\":\"dbyq_wd\",\"childFilterParam\":{\"conditions\":[]}}]}\n";
        //多指标的事件相同
        text = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\",\"event.city\"],\"to_date\":\"20190614\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[]}},{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[]}}]}";
        JSONObject jo = JSONObject.parseObject(text);
        impl.getQueryResult(jo);


        String res0 = "{\"jobId\":\"91b67898-6c91-4d58-b368-22214139df6a\",\"result\":[\"cf_aefzt_ccxq\\u0001acc\\u0001null\\u000171\",\"cf_aefzt_ccxq\\u0001acc\\u000120190611\\u000119\",\"cf_aefzt_ccxq\\u0001acc\\u000120190612\\u000116\",\"cf_aefzt_ccxq\\u0001acc\\u000120190613\\u00011\",\"cf_aefzt_ccxq\\u0001acc\\u000120190614\\u000120\",\"cf_aefzt_ccxq\\u0001acc\\u000120190615\\u000115\",\"cf_lc_lccp_gm\\u0001userid\\u0001null\\u00018315\",\"cf_lc_lccp_gm\\u0001userid\\u000120190611\\u00012333\",\"cf_lc_lccp_gm\\u0001userid\\u000120190612\\u00012041\",\"cf_lc_lccp_gm\\u0001userid\\u000120190613\\u00011875\",\"cf_lc_lccp_gm\\u0001userid\\u000120190614\\u00011669\",\"cf_lc_lccp_gm\\u0001userid\\u000120190615\\u0001397\"]}";

        res0 = "{\"jobId\":\"1b8a3a31-b619-44e0-a4ea-8e1c55f26cb2\",\"result\":[\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u0001null\\u000136\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u000120190611\\u000110\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u000120190612\\u000111\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u000120190613\\u000115\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u0001null\\u000118\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u000120190611\\u00019\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u000120190612\\u00014\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u000120190613\\u00015\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u0001null\\u0001276\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u000120190611\\u000175\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u000120190612\\u000186\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u000120190613\\u0001115\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u0001null\\u000126\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u000120190611\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u000120190612\\u00018\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u000120190613\\u00012\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u0001null\\u000114\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u000120190611\\u00015\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u000120190612\\u00015\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u000120190613\\u00014\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u0001null\\u0001407\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u000120190611\\u0001181\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u000120190612\\u0001149\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u000120190613\\u000177\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u0001null\\u00019\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u000120190611\\u00013\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u000120190612\\u00014\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u000120190613\\u00012\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u0001null\\u00012\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u000120190612\\u00011\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u000120190613\\u00011\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u0001null\\u000151\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u000120190611\\u000117\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u000120190612\\u000119\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u000120190613\\u000115\"]}";
        res0 = "{\"jobId\":\"ccc8431d-4c0b-4dde-a06f-c66c3ed7d6ea\",\"result\":[\"中国\\u0001江苏\\u0001null\\u0001dbyq_cf\\u0001userid\\u0001A\\u0001null\\u000162\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190611\\u000123\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190612\\u000118\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190613\\u000121\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u0001null\\u000129\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190611\\u000115\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190612\\u00016\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190613\\u00018\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_wd\\u0001acc\\u0001C\\u0001null\\u0001632\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190611\\u0001219\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190612\\u0001200\",\"中国\\u0001江苏\\u0001null\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190613\\u0001213\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u0001A\\u0001null\\u000136\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190611\\u000110\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190612\\u000111\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190613\\u000115\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u0001null\\u000118\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190611\\u00019\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190612\\u00014\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190613\\u00015\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u0001C\\u0001null\\u0001276\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190611\\u000175\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190612\\u000186\",\"中国\\u0001江苏\\u0001南京\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190613\\u0001115\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u0001A\\u0001null\\u000126\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190611\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190612\\u00018\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190613\\u00012\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u0001null\\u000114\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190611\\u00015\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190612\\u00015\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190613\\u00014\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u0001C\\u0001null\\u0001407\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190611\\u0001181\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190612\\u0001149\",\"中国\\u0001江苏\\u0001南通\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190613\\u000177\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u0001A\\u0001null\\u00019\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190611\\u00013\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190612\\u00014\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_cf\\u0001userid\\u0001A\\u000120190613\\u00012\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u0001null\\u00012\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190612\\u00011\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_sh\\u0001loginUser\\u0001B\\u000120190613\\u00011\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u0001C\\u0001null\\u000151\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190611\\u000117\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190612\\u000119\",\"中国\\u0001江苏\\u0001宿迁\\u0001dbyq_wd\\u0001acc\\u0001C\\u000120190613\\u000115\",]}";
        res0 = "{\"jobId\":\"982ad3bb-165f-4bb1-9632-191683b8a43e\",\"result\":[\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001A\\u0001null\\u0001356\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001B\\u0001null\\u0001356\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190611\\u000173\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190611\\u000173\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190612\\u000191\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190612\\u000191\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190613\\u000191\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190613\\u000191\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190614\\u0001101\",\"中国\\u0001江苏\\u0001null\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190614\\u0001101\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001B\\u0001null\\u0001104\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001A\\u0001null\\u0001104\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190611\\u000135\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190611\\u000135\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190612\\u000128\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190612\\u000128\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190613\\u000123\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190613\\u000123\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190614\\u000118\",\"中国\\u0001江苏\\u0001南京\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190614\\u000118\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001B\\u0001null\\u000180\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001A\\u0001null\\u000180\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190611\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190611\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190612\\u000127\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190612\\u000127\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190613\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190613\\u000116\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190614\\u000121\",\"中国\\u0001江苏\\u0001南通\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190614\\u000121\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001B\\u0001null\\u000133\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001A\\u0001null\\u000133\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190611\\u000110\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190611\\u000110\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190612\\u000111\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190612\\u000111\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001A\\u000120190613\\u00017\",\"中国\\u0001江苏\\u0001宿迁\\u0001axzh_sz\\u0001acc\\u0001B\\u000120190613\\u00017\",]}";
        JSONObject jo1 =JSONObject.parseObject(res0);
        Tuple3<JSONArray, JSONArray, JSONArray> commReturn = impl.commRetrunOp(jo);
        JSONObject res = impl.resultOp(jo1, commReturn);
        System.out.println(res);


    }




}
