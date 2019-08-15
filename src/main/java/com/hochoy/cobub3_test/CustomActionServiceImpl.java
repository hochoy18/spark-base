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
//import org.apache.commons.lang3.StringUtils;
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
import scala.*;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.Long;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义事件（事件分析）
 */
//@Service
public class CustomActionServiceImpl  {

    private static final Logger logger = LoggerFactory.getLogger(CustomActionServiceImpl.class);
    String separator = "@@@@@@@";
//    @Autowired
//    private JobServer jobServer;

    //@Value("${spark.query.maxLine}")
    private String maxLine = "1000";

    //@Value("${spark.export.query.maxLine}")
    private String exportMaxLine ="100";

    //@Value("${hbasenamespace}")
    private String hbaseNameSpace = "cobub3";

//    @Autowired
//    private UserMetadataService userMetadataService;
//
//    @Autowired
//    private ActionReportMapper actionReportMapper;
//
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

    //@Override
    public JSONObject getQueryResult(JSONObject jsonObject) throws IOException {
        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
        String relation = filters.getString(Constants.RELATION); // 主查询条件 逻辑关系 and / or
        String unit = jsonObject.getString(Constants.UNIT);
        if (!"day".equalsIgnoreCase(unit) && !"hour".equalsIgnoreCase(unit)){
            return new JSONObject();
        }

        StringJoiner commWhere = getActionCommWhere(jsonObject);
        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标

        String productId = jsonObject.getString(Constants.PRODUCTID);
        Map map = new HashMap();
        map.put(Constants.RELATION, relation);
        map.put("commWhere", commWhere);
        map.put("productId", productId);
        map.put("unit", unit);

        Tuple2 queryOrSaveOp = queryOrSaveOp(jsonObject);
        /**
         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
         */
        Tuple3<String,String,Object> sqlOps = generateMultipleIndicatorsSQL(actions, (Tuple7)queryOrSaveOp._1, (Tuple5) queryOrSaveOp._2, map,true);

        JSONObject responseResult = querySparkSql(new StringBuilder(sqlOps._1()), maxLine,sqlOps._2(),null,null,"tmp_actionreport_job_");

        if (null == responseResult || !responseResult.containsKey("result") ||responseResult.isEmpty() ||responseResult == null ||
                (responseResult.containsKey("status") && responseResult.getString("status").equalsIgnoreCase("error"))) {
            return new JSONObject();
        }
        logger.debug("responseResult : {}"+responseResult);

        Tuple3<Map<String,Map<String,String>>, JSONArray, JSONArray> commReturn = commReturnOp(jsonObject);

        JSONObject result = null;//resultOp(responseResult, commReturn,(LinkedList)sqlOps._3());

        return result;
    }

    private Tuple2<Tuple7,Tuple5>  queryOrSaveOp(JSONObject jsonObject){
        String productId = jsonObject.getString(Constants.PRODUCTID);

        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);

        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
        String relation = conditions.size() < 2 ? Constants.AND : filters.getString(Constants.RELATION); // 主查询条件 逻辑关系 and / or


        // 根据分组字段 by_fields 拼接 需要 【select 的字段和 group by 字段 】
        /**
         *  返回 分组 + 查询 字段
         *  return (outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction)
         */
        Map<String, String> userPropertiesMap = getUserPropertiesAndTypes(productId);
        Tuple7 selectAndGroupBy = SQLUtil.byFieldOp(fields,productId,userPropertiesMap);

        /**
         * 外部查询 where 条件
         * return ( userWhere,actionWhere)
         */
        Tuple5 filter = SQLUtil.queryConditionOp(conditions, relation,productId);

        return new  Tuple2(selectAndGroupBy,filter);
    }






    private Tuple3<Map<String,Map<String,String>>,JSONArray,JSONArray> commReturnOp(JSONObject jsonObject ) {

        Map<String, Map<String, String>> id2Alias = getId2Alias(jsonObject);


        JSONArray series = getDateSeries(jsonObject);

        JSONArray by_fields = new JSONArray();

        jsonObject.getJSONArray("by_fields").forEach(x->{
            by_fields.add(x.toString());
        });
        return new Tuple3<>(id2Alias,series,by_fields);
    }



    private JSONObject resultOp(JSONObject jo, Tuple3<Map<String,Map<String,String>>,JSONArray,JSONArray> commReturn,LinkedList idxs) {

        Map<String,Map<String,String>> map = commReturn._1();

        JSONArray series = commReturn._2();
        JSONArray by_fields = commReturn._3();


        if (!jo.containsKey("result") || jo.getJSONArray("result").isEmpty()){
            return new JSONObject();
        }
        JSONArray result = jo.getJSONArray("result");
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

            if (Constants.NULL.equals(date)){
                date = "cnt";
            }
            List bySet = new LinkedList();
            for (int i = 0; i < len - 5; i++) {
                if(!Constants.NULL.equals(split[i])){
                   String by0 = split[i];
                    String byF = by_fields.getString(i);
                    String byres = getByres(map, by0, byF);
                    bySet.add(byres);
                }else {
                    bySet.add(Constants.UNKNOWN);
                }
            }
            String by;
            if(bySet.isEmpty() ||  (by_fields.size()==1 && Constants.ALL.equalsIgnoreCase(by_fields.getString(0)))){
                by = Constants.ALL;
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
                Map<String, String> idx_Num = dateIdxNum.get(date);
                if (null == idx_Num){

                    idxNum = new HashMap<>();
                    idxNum.put(idx,num);
                    dateIdxNum.put(date, idxNum);
                }else {
                    idx_Num.put(idx,String.valueOf( Long.parseLong(num) +  Long.parseLong( idx_Num.containsKey(idx)? idx_Num.get(idx) : "0") ));
                    dateIdxNum.put(date,idx_Num);
                }
            }
            result1.put(by, dateIdxNum);
        });
        logger.debug("result1: {}",result1.toString());


        LinkedList eventIndicatorList = new LinkedList();
        idxs.forEach(v ->{
            String[] split = v.toString().split(separator, 3);
            eventIndicatorList.add(String.join(Constants.SEPARATOR,split));
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
        logger.debug("res0: {}",res0);


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
     * @param isQuery 是否是查询，true :查询 false 保存
     */
    private Tuple3<String,String,Object> generateMultipleIndicatorsSQL(JSONArray actions,
                                                                       Tuple7 byFields,
                                                                       Tuple5 outFilters,
                                                                       Map map, boolean isQuery) {
        String parquetSQL = "SELECT  %s  FROM parquetTmpTable WHERE %s  GROUP BY %s";
        String joinSQL = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE   %s  ) tu  ON concat_ws('_', '%s', ta.global_user_id) = tu.pk where %s ";
        String partialAggSQLFormat = "select %s from %s group by %s"; // (groupBy + action + indicatorType) , joinSQL ,groupBy
        String joinSqlNoWhere = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE %s ) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk ";
        String hourDayConcat = "CONCAT ( SUBSTRING(day, 0, 4) ,'-' ,SUBSTRING(day, 5, 2) ,'-' ,SUBSTRING(day, 7, 2) ,' ' ,hour ,':00:00')";

        HashSet<String> groupId = new HashSet();
        JSONObject props = new JSONObject();
        List<String> sqlList = new ArrayList<>();
        LinkedList idxs = new LinkedList();

        final StringJoiner outGroupByUser = (StringJoiner) byFields._1();
        final StringJoiner outGroupByAction = (StringJoiner) byFields._2();
        final HashSet outSelectFieldUser = (HashSet) byFields._3();//out 根据分组字段 by_fields 筛出来的 user 查询字段
        final HashSet outSelectFieldAction = (HashSet) byFields._4();//out 根据分组字段 by_fields 筛出来的 acton 查询字段
        HashMap<String, String> groupByUserProp2Type = (HashMap<String, String>) byFields._5();//根据分组字段筛出来的 用户属性和属性数据类型
        HashSet<String> userGroupIds = (HashSet<String>) byFields._6(); // 根据分组字段筛出来的 用户分群ID集合
        StringJoiner allSelectJoiner = (StringJoiner)byFields._7();
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
            partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action" );
        }else {
            partialAggGroupBy = String.join(", ", "action" );
        }

        String groupAndGroupingBy = " %s GROUPING SETS(( %s, day ),( %s ) )";// todo


        String commonGroupBy;
        if(isQuery) {
            commonGroupBy = String.format(
                    groupAndGroupingBy,
                    String.join(",", partialAggGroupBy, "day"),//todo day
                    partialAggGroupBy, partialAggGroupBy);
        }else {
            commonGroupBy = String.join(",", partialAggGroupBy) ;
        }

        String relation = (String) map.get(Constants.RELATION);//主查询条件 and / or
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
            commWhere1.add(commWhere).add(String.format("(category = '%s')", category)).add(String.format("( action = '%s' )",eventOriginal));
            JSONObject childFilterParam = action.getJSONObject(Constants.CHILDFILTERPARAM);
            JSONArray conditions = childFilterParam.getJSONArray("conditions");
            String relate = conditions.size() >= 2 ? (childFilterParam.getString(Constants.RELATION)):(Constants.AND);
            /**
             //filter: ( new Tuple5<>(userWhere, actionWhere, groupId,actionFields, userProps);
             // _1:user 的where条件，
             // _2:action 的where条件，
             // _3:用户分群id的set集合，
             // _4:查询条件中action属性列名set集合，
             // _5:用户属性名及其类型的map
             // )
             */
            Tuple5 queryCondition = SQLUtil.queryConditionOp(conditions, relate,productId);
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


            HashSet<String> actionSelectSet = new HashSet<>();
            actionSelectSet.addAll(Arrays.asList("global_user_id", "action"));
            actionSelectSet.addAll(outSelectFieldAction);
            actionSelectSet.add(eventCol);


            HashSet<String> userSelectSet = new HashSet<>();
            userSelectSet.add("pk");
            userSelectSet.addAll(outSelectFieldUser);

            // actionSelect : global_user_id, 分组字段，action，day

            String userSelect = String.join(", ", userSelectSet);   //userSelect 基本筛选条件 : pk, 分组字段


            //joinselect : 分组字段，action，day, 指标 , 'A' AS an
            String joinSelect0 ;
            char c = (char) (i.getAndAdd(1));
            idxs.add(String.join(separator,eventOriginal,eventType,String.valueOf(c)));
            if (!groupByJoiner.toString().isEmpty()){
                joinSelect0 = String.join(", ", groupByJoiner.toString(), "action", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", c), indicatorType);
            }else {
                joinSelect0 = String.join(", ",  "action", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", c), indicatorType);
            }
            String joinSelect ;
            if(isQuery){
                joinSelect = String.join(",",joinSelect0, "day");
//                actionSelectSet.add("day");
            }else{
                joinSelect = joinSelect0;
            }


            String singleGroupBy = commonGroupBy;
            if (outGroupByUser.toString().isEmpty() && inUserWhere.toString().isEmpty() && outUserWhere.toString().isEmpty()) {
                //分组、内部条件、外部条件中只有事件属性，只查 parquet 表
                StringJoiner actionWhere = getWhereOfUserOrAction(commWhere1, outActionWhere, inActionWhere);
                String singleSQL;
                if(isQuery && !"hour".equalsIgnoreCase((String) map.get("unit"))){
//                    joinSelect = String.join(",",joinSelect0, "day");
                    actionSelectSet.add("day");
                }else if(isQuery && "hour".equalsIgnoreCase((String) map.get("unit"))){
                    joinSelect =  String.join(",",joinSelect0, hourDayConcat +" AS day");
                    singleGroupBy = commonGroupBy.replaceAll("day",hourDayConcat);
                    actionSelectSet.add("day");
                }

                singleSQL = String.format(parquetSQL, joinSelect, actionWhere.toString(), singleGroupBy);//todo joinSelect commonGroupBy
                sqlList.add(singleSQL);
            } else {
                if(isQuery && !"hour".equalsIgnoreCase((String) map.get("unit"))){
                    actionSelectSet.add("day");
                }else if(isQuery && "hour".equalsIgnoreCase((String) map.get("unit"))){
                    actionSelectSet.add(hourDayConcat + "AS day");
                }
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
                        String actionSelectOr = String.join(", ",String.join(", ", actionSelectSetOr),String.join(", ", String.format("'%s' AS INDICATORTYPE",eventType)));
                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
                        userSelectSetOr.addAll(groupIdIn);
                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);


                        String outWhere = new StringJoiner(Constants.OR, "(", ")")
                                .add(String.format("( %s )",outActionWhere.toString()))
                                .add(String.format("( %s )",outUserWhere.toString())).toString();
                        String inWhere = new StringJoiner(Constants.OR, "(", ")")
                                .add(String.format("( %s )",inActionWhere.toString()))
                                .add(String.format("( %s )",inUserWhere.toString())).toString();
                        String joinWhere = new StringJoiner(Constants.AND," (",") ").add(outWhere).add(inWhere).toString();

                        userWhere1 = userCommWhere;
                        actionWhere1 = commWhere1.toString();
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, commonGroupBy);
                        sqlList.add(partialAggSQL);
                    } else if (inOr && (!outOr)) {
                        // inWhere 条件   放到  usersTable join parquetTmpTable 后,
                        // outWhere 放到 对应 usersTable 和 parquetTmpTable 表 的where 后

                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ in)
                        //   usersTable 的select列中需要包含 or 条件中包含的列（ in)

                        actionSelectSetOr.addAll(actionFieldsIn);
                        String actionSelectOr = String.join(", ",String.join(", ", actionSelectSetOr),String.join(",", String.format("'%s' AS INDICATORTYPE",eventType)));
                        userSelectSetOr.addAll(userProp2TypeIn.keySet());
                        userSelectSetOr.addAll(groupIdIn);
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);
                        String joinWhere = String.join(Constants.OR, inActionWhere.toString(), inUserWhere.toString());

                        if (!outActionWhere.toString().isEmpty()) {
                            actionWhere1 = (new StringJoiner(Constants.AND,"(",")").add(actionWhere).add(outActionWhere.toString())).toString();//String.join(relate);
                        }

                        if (!outUserWhere.toString().isEmpty()) {
                            userWhere1 = (new StringJoiner(Constants.AND,"(",")")
                                    .add(String.format("( %s )",userWhere))
                                    .add(String.format("( %s )", outUserWhere.toString()))).toString();
                        }
//                        String joinWhere
                        //actionSelect ,actionWhere , userSelect , userWhere  productId,joinWhere, joinGroupBy(action ... )
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);

                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, commonGroupBy);
                        sqlList.add(partialAggSQL);
//
                    } else if (!inOr && outOr) {
                        //1. outActionWhere  outUserWhere  都是非空 ，且 or 关系，放到join where 后
                        //2. inActionWhere inUserWhere 可能为空 或 为 and 关系
                        // outWhere 条件   放到  usersTable join parquetTmpTable 后
                        // ,inWhere 放到 对应的 usersTable 和 parquetTmpTable 表 的where 后
                        //   parquetTmpTable 的select列中需要包含 or 条件中包含的列（ out ，把 out 条件中的 action 列加到parquet表的 select 字段中)
                        //   usersTable 的select列中需要包含 or 条件中包含的列（ out 把 out 条件中的 users列加到parquet表的 select 字段中))

                        actionSelectSetOr.addAll(actionFieldsOut);
                        String actionSelectOr = String.join(", ", String.join(", ", actionSelectSetOr),String.join(",", String.format("'%s' AS INDICATORTYPE",eventType)));
                        userSelectSetOr.addAll(userProp2TypeOut.keySet());
                        userSelectSetOr.addAll(groupIdOut);
                        String userSelectOr = String.join(", ", userSelectSetOr);

                        String joinWhere = String.join(Constants.OR, outActionWhere.toString(), outUserWhere.toString());
                        if (!inActionWhere.toString().isEmpty()) {
                            actionWhere1 = new StringJoiner(Constants.AND,"(",")")
                                    .add(String.format("( %s )",actionWhere))
                                    .add(String.format("( %s )",inActionWhere.toString())).toString();
                        }
                        if (!inUserWhere.toString().isEmpty()) {
                            userWhere1 = (new StringJoiner(Constants.AND,"(",")").add(userWhere).add(inUserWhere.toString())).toString() ;
                        }
                        String joinSQL1 = String.format(joinSQL, actionSelectOr, actionWhere1, userSelectOr, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, commonGroupBy);
                        sqlList.add(partialAggSQL);

                    }

                } else {
                    // 所有条件均分别下推到 userTable 和 parquetTmpTable 表的where  后
                    // join 后没有where
                    // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
                    // actionWhere: outActionWhere and inActionWhere ->
                    // userWhere ：outUserWhere and inUserWhere -->
                    //
                    StringJoiner actionWhere = getWhereOfUserOrAction(commWhere1, outActionWhere, inActionWhere);


                    String actionSelect = String.join(",",String.join(", ", actionSelectSet),String.format("'%s' AS INDICATORTYPE",eventType));
                    StringJoiner userWhere = new StringJoiner(Constants.AND,"(",")");
                    userWhere.add(userCommWhere);
                    if (!outUserWhere.toString().isEmpty()) {
                        userWhere.add(String.format("( %s )",outUserWhere.toString()));
                    }
                    if (!inUserWhere.toString().isEmpty()) {
                        userWhere.add(String.format("( %s )", inUserWhere.toString()));
                    }
                    actionWhere.toString();
                    String joinSQL1 = String.format(joinSqlNoWhere, actionSelect, actionWhere.toString(), userSelect, userWhere.toString(), productId);
                    String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, commonGroupBy);
                    sqlList.add(partialAggSQL);

                }


            }


        });

        String SQL = "";

        SQL = String.join(" UNION ", sqlList);


//        String allGroupBy = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an");

//        String grouping;
//        if(!groupByJoiner.toString().isEmpty()){
//            grouping = String.join(",", groupByJoiner.toString(), "action", "INDICATORTYPE","an");
//        }else {
//            grouping = String.join(",", "action", "INDICATORTYPE","an");
//        }


        String group ;//= String.join(",", partialAggGroupBy,"INDICATORTYPE", "an","day");
        String allSelect ;
        if( isQuery ){
//            String groupingSet = String.format("GROUPING SETS(( %s ),(%s) ) ORDER BY %s", String.join(",",partialAggGroupBy,"INDICATORTYPE","an"), grouping,partialAggGroupBy); // groupby + action
            if (!allSelectJoiner.toString().isEmpty()){
                allSelect = String.join(", ", allSelectJoiner.toString(), "action","INDICATORTYPE","an", "day", "SUM(ct)");
            }else{
                allSelect = String.join(", ", "action","INDICATORTYPE","an", "day", "SUM(ct)");
            }
            group = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an","day");
        }else {

            if (!allSelectJoiner.toString().isEmpty()){
                allSelect = String.join(", ","action","INDICATORTYPE", allSelectJoiner.toString(), "an",  "SUM(ct) as num");
            }else{
                allSelect = String.join(", ", "action", "INDICATORTYPE", "'all'","an", "SUM(ct) as num");
            }
            group = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an");
        }
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

        logger.debug("props: {}" , prop.toString() );
        logger.debug("groupIds: {}" , groupIds.toString() );

//        logger.debug("taskSql: {}", taskSql );
//        logger.debug("allSQL: {}", allSQL);
//        System.out.println("taskSql: {}"+ taskSql );;
//        System.out.println("allSQL: {} " + allSQL);

        if (isQuery){
            return new Tuple3<>(allSQL,taskSql,idxs);
        }else {
            return new Tuple3<>(allSQL,prop.toString(),groupIds.toString());
        }
    }









    //todo 用户属性及其类型
    private Map<String, String> getUserPropertiesAndTypes(String productId) {
        Map<String, String> map = new HashMap<>();
        UserMetaType  userMetaType2  = new UserMetaType();
        userMetaType2.setProductId(Long.parseLong(productId));
//        userMetaTypeMapper.getAllActiveMetaTypeListForFilter(userMetaType2).forEach(domain->map.put(domain.getType(),domain.getDatatype()) );
        return map;
    }


    private StringJoiner getWhereOfUserOrAction(StringJoiner commWhere1, StringJoiner outWhere, StringJoiner inWhere) {

        StringJoiner actionWhere = new StringJoiner(Constants.AND," (",") ");
        actionWhere.add(commWhere1.toString());
        if (!outWhere.toString().isEmpty()) {
            actionWhere.add(String.format("( %s )",outWhere.toString()));
        }
        if (!inWhere.toString().isEmpty()) {
            actionWhere.add(String.format("( %s )",inWhere.toString()));
        }
        return actionWhere;

    }





    public int saveQueryTask(JSONObject jsonObject) throws IOException {
        // 保存自定义查询任务
        String unit = jsonObject.getString(Constants.UNIT);
        String reportName = jsonObject.getString(Constants.REPORT_NAME);
        String description = jsonObject.getString("description");
        String productId = jsonObject.getString(Constants.PRODUCTID);
        String fromDate = jsonObject.getString(Constants.FROM_DATE);
        String toDate = jsonObject.getString(Constants.TO_DATE);

        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);
        String relation = filters.getString(Constants.RELATION);
        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
        if(conditions.size()<= 1){
            relation = Constants.AND;
        }
        StringJoiner commWhere = new StringJoiner(" AND ");
        String dateCon = String.format("( productid = '%s' )", productId);
        commWhere.add(dateCon);
        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标
        // 数据
        JSONObject data = jsonObject.getJSONObject("data");



        Map map = new HashMap();
        map.put(Constants.RELATION, relation);
        map.put("commWhere", commWhere);
        map.put("unit", unit);
        map.put("productId", productId);
        Tuple2 queryOrSaveOp = queryOrSaveOp(jsonObject);
        /**
         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
         */
        Tuple3<String,String,Object> sqlOps = generateMultipleIndicatorsSQL(actions, (Tuple7)queryOrSaveOp._1, (Tuple5) queryOrSaveOp._2, map,false);

        String taskSQL = sqlOps._1();
        String prop = sqlOps._2();
        String groupid = (String)sqlOps._3();



        // 保存事件任务
        ActionReport actionReport = new ActionReport();
        actionReport.setReportName(reportName);
        final String dateRange = LocalDateTimeUtil.getDateTimePeriodDay(fromDate) + Constants.TASK_DATE_RANGE_JOINER + LocalDateTimeUtil.getDateTimePeriodDay(toDate);
        actionReport.setDateRange(dateRange);
        actionReport.setTaskSql(taskSQL);
        actionReport.setDescription(null == description ? "" : description);
        actionReport.setUnit(unit);
        actionReport.setProductId(Integer.parseInt(productId));

        // 保存条件时删除前端传过来的图表数据
        jsonObject.remove("data");
        actionReport.setTaskConditions(jsonObject.toString());
        actionReport.setTableName("temp");

        actionReport.setUserProp(prop);
        actionReport.setUserGroupid(groupid);
        actionReport.setCreateTime(new Date());
//        actionReportMapper.insert(actionReport);

        // 更新表名
        ActionReport updateHbaseTableName = new ActionReport();
        final Integer reportId = 11048;// actionReport.getReportId();
        updateHbaseTableName.setReportId(reportId);
        final String tableName = hbaseNameSpace + Constants.TASK_ACTION_TABLE_NAME + reportId;
        updateHbaseTableName.setTableName(tableName);
//        actionReportMapper.updateByPrimaryKeySelective(updateHbaseTableName);

        // 创建hbase表
//        Connection2hbase.createTable(tableName);

        // 保存数据到hbase
        saveDataToHbase( reportId, tableName, data);

        logger.debug("task's sql: {}", taskSQL);
        return reportId;
    }

    /**
     * 把事件分析实时查询出的数据保存到hbase
     *
     * @param reportId  报表ID
     * @param tableName hbase表名
     * @param data      数据
     * @throws IOException 异常
     */
    private void saveDataToHbase1( Integer reportId, String tableName, JSONObject data)  {
        String qualifier = "num";
        int PUT_NUM_PER_BATCH = 200;
//        Table table = Connection2hbase.getTable(tableName);
        if (!data.containsKey("detail_result") || data.isEmpty()){
            return;
        }
        JSONObject detailResult = data.getJSONObject("detail_result");
        if(!detailResult.containsKey("rows") || detailResult.isEmpty()){
            return;
        }
        JSONArray rows = detailResult.getJSONArray("rows");
        JSONArray series = detailResult.getJSONArray("series");
        List<Put> puts = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < rows.size(); i++) {


            JSONObject jo = rows.getJSONObject(i);
            JSONArray joValues = jo.getJSONArray("values");
            JSONArray byValues = jo.getJSONArray("by_values");
            JSONArray eventIndicator = jo.getJSONArray("event_indicator");
            StringJoiner byJoiner = new StringJoiner(Constants.SEPARATOR);
            byValues.forEach(x-> byJoiner.add(x.toString()));
            for (int d = 0; d < joValues.size(); d++) {
                JSONArray eachDatas = joValues.getJSONArray(d);
                String day = (String)series.get(d);
                for (int x = 0; x < eachDatas.size(); x++) {
                    count++;
                    String actionIndicator =  eventIndicator.getString(x);
                    String eachData = eachDatas.getString(x);
                    // row
                    String[] split = actionIndicator.split(Constants.SEPARATOR,3);

                    String rowKey = String.join (Constants.SEPARATOR,
                            Integer.toString(reportId),
                            day,
                            split[0],split[1],
                            byJoiner.toString(),split[2]);
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(qualifier), Bytes.toBytes(eachData));
                    puts.add(put);
                    if (count % PUT_NUM_PER_BATCH == 0) {
                        try {
//                            table.put(puts);
                            puts.clear();
                        } catch ( Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
//        if (!puts.isEmpty()) {
//            try {
//                table.put(puts);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            table.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }




    static CustomActionServiceImpl impl = new CustomActionServiceImpl();
//    public static void main(String[] args) throws Exception {
////        LinkedList<String> hourSeries = impl.getHourSeries(Integer.valueOf("03"), 22);
////        System.out.println(hourSeries);
//        querytest();
//
//    }

    public static void querytest() throws Exception {

        String inputStr;
//        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\"],\"to_date\":\"20190709\",\"productId\":\"11128\",\"action\":[{\"eventType\":\"loginUser\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
        JSONObject jo1;


        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190801\",\"productId\":\"11208\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.version\",\"function\":\"equal\",\"params\":[\"ad.ver.v.003\",\"ad.ver.v.004\",\"ad.ver.v.005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.osversion\",\"function\":\"equal\",\"params\":[\"os.v.003\",\"os.v.004\",\"os.v.005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"wifi001\",\"wifi004\",\"wifi005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"user.grouptwoisupdate\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190801\",\"from_hour\":\"13\",\"to_hour\":\"22\",\"productId\":\"11208\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.version\",\"function\":\"equal\",\"params\":[\"ad.ver.v.003\",\"ad.ver.v.004\",\"ad.ver.v.005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.osversion\",\"function\":\"equal\",\"params\":[\"os.v.003\",\"os.v.004\",\"os.v.005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"wifi001\",\"wifi004\",\"wifi005\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"user.grouptwoisupdate\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\",\"userGroup.groupsix\"],\"to_date\":\"20190801\",\"productId\":\"11208\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.pagetitle\",\"function\":\"equal\",\"params\":[\"sy004\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"sys_android006\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"user.grouptwonetwork\",\"function\":\"equal\",\"params\":[\"wifi002\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"userGroup.groupsix\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\",\"userGroup.groupsix\"],\"to_date\":\"20190801\",\"from_hour\":\"13\",\"to_hour\":\"22\",\"productId\":\"11208\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.pagetitle\",\"function\":\"equal\",\"params\":[\"sy004\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"sys_android006\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"user.grouptwonetwork\",\"function\":\"equal\",\"params\":[\"wifi002\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"userGroup.groupsix\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190623\",\"by_fields\":[\"event.country\",\"event.city\",\"userGroup.loginq\",\"userGroup.logina\",\"user.grouptwonetwork\"],\"to_date\":\"20190722\",\"productId\":\"11188\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";

        // by hour
        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190610\",\"from_hour\":\"04\",\"by_fields\":[\"event.country\",\"event.city\",\"userGroup.loginq\",\"userGroup.logina\",\"user.grouptwonetwork\"],\"to_date\":\"20190610\",\"to_hour\":\"14\",\"productId\":\"11188\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
          jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);
        Thread.sleep(1000 * 100);
        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190610\",\"from_hour\":\"04\",\"by_fields\":[\"event.country\",\"event.city\",\"userGroup.loginq\",\"userGroup.logina\",\"user.grouptwonetwork\"],\"to_date\":\"20190611\",\"to_hour\":\"14\",\"productId\":\"11188\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190610\",\"from_hour\":\"04\",\"by_fields\":[\"event.country\",\"event.city\",\"userGroup.loginq\",\"userGroup.logina\",\"user.grouptwonetwork\"],\"to_date\":\"20190612\",\"to_hour\":\"14\",\"productId\":\"11188\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);
        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"hour\",\"from_date\":\"20190610\",\"from_hour\":\"04\",\"by_fields\":[\"event.country\",\"event.city\",\"userGroup.loginq\",\"userGroup.logina\",\"user.grouptwonetwork\"],\"to_date\":\"20190613\",\"to_hour\":\"14\",\"productId\":\"11188\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
        jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);

    }


    static void  saveTest()  throws Exception{
        String inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"or\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190614\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"user.networkH\",\"function\":\"equal\",\"params\":[\"WIFI12\",\"WIFI16\",\"WIFI19\",\"WIFI22\",\"WIFI26\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"}},{\"eventType\":\"userid\",\"eventOriginal\":\"cf_aefzt_ccxq\",\"childFilterParam\":{\"conditions\":[{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.version\",\"function\":\"equal\",\"params\":[\"4.1.8\",\"4.1.9\",\"4.2.0\",\"4.2.1\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"}}],\"data\":{\"rollup_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\",\"20190614\"],\"total_rows\":29,\"by_fields\":[\"event.country\",\"event.region\"],\"num_rows\":29,\"rows\":[{\"values\":[[\"10\",\"0\"]],\"by_values\":[\"中国\",\"山东\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"8\",\"0\"]],\"by_values\":[\"中国\",\"福建\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"7\",\"0\"]],\"by_values\":[\"中国\",\"河南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"]],\"by_values\":[\"中国\",\"河北\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"8\",\"0\"]],\"by_values\":[\"中国\",\"江西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"3\",\"0\"]],\"by_values\":[\"中国\",\"湖北\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"5\",\"0\"]],\"by_values\":[\"中国\",\"湖南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"]],\"by_values\":[\"中国\",\"辽宁\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"81\",\"0\"]],\"by_values\":[\"中国\",\"广东\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"44\",\"0\"]],\"by_values\":[\"中国\",\"中国\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"马来西亚\",\"马来西亚\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"28\",\"1\"]],\"by_values\":[\"中国\",\"北京\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"越南\",\"越南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"42\",\"0\"]],\"by_values\":[\"中国\",\"上海\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"]],\"by_values\":[\"中国\",\"山西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"7\",\"0\"]],\"by_values\":[\"中国\",\"甘肃\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"中国\",\"黑龙江\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"3\",\"0\"]],\"by_values\":[\"中国\",\"四川\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"31\",\"0\"]],\"by_values\":[\"中国\",\"浙江\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"]],\"by_values\":[\"中国\",\"广西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"澳大利亚\",\"澳大利亚\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"7\",\"0\"]],\"by_values\":[\"中国\",\"云南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"3\",\"0\"]],\"by_values\":[\"中国\",\"陕西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"中国\",\"贵州\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"日本\",\"日本\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"]],\"by_values\":[\"中国\",\"新疆\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1419\",\"1\"]],\"by_values\":[\"中国\",\"江苏\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"3\",\"0\"]],\"by_values\":[\"中国\",\"内蒙古\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"11\",\"0\"]],\"by_values\":[\"中国\",\"安徽\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]}]},\"detail_result\":{\"series\":[\"20190611\",\"20190612\",\"20190613\",\"20190614\"],\"total_rows\":29,\"by_fields\":[\"event.country\",\"event.region\"],\"num_rows\":29,\"rows\":[{\"values\":[[\"2\",\"0\"],[\"1\",\"0\"],[\"2\",\"0\"],[\"5\",\"0\"]],\"by_values\":[\"中国\",\"山东\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"],[\"3\",\"0\"],[\"2\",\"0\"],[\"2\",\"0\"]],\"by_values\":[\"中国\",\"福建\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"4\",\"0\"],[\"2\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"中国\",\"河南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"中国\",\"河北\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"],[\"2\",\"0\"],[\"0\",\"0\"],[\"4\",\"0\"]],\"by_values\":[\"中国\",\"江西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"],[\"0\",\"0\"],[\"2\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"湖北\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"],[\"2\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"中国\",\"湖南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"2\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"辽宁\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"27\",\"0\"],[\"14\",\"0\"],[\"22\",\"0\"],[\"18\",\"0\"]],\"by_values\":[\"中国\",\"广东\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"14\",\"0\"],[\"10\",\"0\"],[\"11\",\"0\"],[\"9\",\"0\"]],\"by_values\":[\"中国\",\"中国\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"马来西亚\",\"马来西亚\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"8\",\"1\"],[\"2\",\"0\"],[\"11\",\"0\"],[\"7\",\"0\"]],\"by_values\":[\"中国\",\"北京\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"越南\",\"越南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"11\",\"0\"],[\"10\",\"0\"],[\"14\",\"0\"],[\"7\",\"0\"]],\"by_values\":[\"中国\",\"上海\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"2\",\"0\"]],\"by_values\":[\"中国\",\"山西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"5\",\"0\"],[\"2\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"甘肃\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"黑龙江\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"3\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"四川\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"5\",\"0\"],[\"7\",\"0\"],[\"5\",\"0\"],[\"14\",\"0\"]],\"by_values\":[\"中国\",\"浙江\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"中国\",\"广西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"1\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"澳大利亚\",\"澳大利亚\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"4\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"],[\"2\",\"0\"]],\"by_values\":[\"中国\",\"云南\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"],[\"2\",\"0\"]],\"by_values\":[\"中国\",\"陕西\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"贵州\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"日本\",\"日本\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"0\",\"0\"],[\"0\",\"0\"],[\"1\",\"0\"]],\"by_values\":[\"中国\",\"新疆\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"358\",\"0\"],[\"357\",\"1\"],[\"343\",\"0\"],[\"361\",\"0\"]],\"by_values\":[\"中国\",\"江苏\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"0\",\"0\"],[\"2\",\"0\"],[\"1\",\"0\"],[\"0\",\"0\"]],\"by_values\":[\"中国\",\"内蒙古\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]},{\"values\":[[\"6\",\"0\"],[\"1\",\"0\"],[\"2\",\"0\"],[\"2\",\"0\"]],\"by_values\":[\"中国\",\"安徽\"],\"event_indicator\":[\"axzh_sz\\u0001acc\\u0001A\",\"cf_aefzt_ccxq\\u0001userid\\u0001B\"]}]}},\"report_name\":\"aaaz\",\"description\":\"\"}";

        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\",\"event.region\"],\"to_date\":\"20190613\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"axzh_sz\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.platform\",\"function\":\"equal\",\"params\":[\"Android\",\"iOS\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"user.networkH\",\"function\":\"equal\",\"params\":[\"WIFI1\",\"WIFI10\",\"WIFI11\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"or\"}},{\"eventType\":\"userid\",\"eventOriginal\":\"cf_aefzt_ccxq\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.channelid\",\"function\":\"equal\",\"params\":[\"001\",\"002\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"userGroup.groupFirstH\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"cf_aefzt\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.is_new_device\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"},{\"type\":\"event.language\",\"function\":\"equal\",\"params\":[\"de-CN\",\"en-CN\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}\n";
        inputStr = "{\"filter\":{\"conditions\":[{\"type\":\"user.networkH\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"WIFI1\",\"WIFI10\",\"WIFI11\",\"WIFI12\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"},{\"type\":\"event.channelid\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[\"001\",\"002\"],\"inputForInt\":\"\",\"divForInt\":\"\",\"input\":\"\"}],\"relation\":\"or\"},\"unit\":\"day\",\"from_date\":\"20190611\",\"by_fields\":[\"event.country\"],\"to_date\":\"20190613\",\"productId\":\"11148\",\"action\":[{\"eventType\":\"userid\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.network\",\"function\":\"equal\",\"params\":[\"2G\",\"3G\",\"WIFI\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}}]}\n";
        JSONObject jo1 = JSONObject.parseObject(inputStr);
        impl.getQueryResult(jo1);
        impl.saveQueryTask(jo1);


    }


    public JSONObject querySparkSql(StringBuilder sqlBuilder, String maxLine,String params,JobHistoryDao jobHistoryDao,JobServer jobServer,String jobName) throws IOException {
        String jo2s = "{\"result\":[\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u00012\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u0001120\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190712\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00014\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190711\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u00014\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u000115\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190709\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u00014\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190705\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00012\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001wifi002\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u000116\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u000132\",\"中国\\u0001南通\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u00018\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u000115\",\"中国\\u0001南京\\u0001true\\u0001true\\u0001null\\u0001$appClick\\u0001acc\\u0001A\\u000120190710\\u000130\"],\"jobId\":\"tmp_actionreport_job_89d8bbca-51b5-43f5-bcfe-0cd128b177ab\"}";
        JSONObject jo2 = JSONObject.parseObject(jo2s);
        return jo2;
    }

    /**
     *
     * @param map
     * @param by0 activity1 ,true/真，false/假， male/男，。。。
     * @param byF event.pageTitle,userGroup.group1, user.sex ....
     * @return
     */
    private String getByres(Map<String, Map<String, String>> map, String by0, String byF) {
        String res ;
        if(byF.startsWith("userGroup")){
            res = map.get("userGroup").get(by0);
        }else  {
            res = map.get(byF).get(by0);
        }
        return  null == res ? "unknown": res ;
    }

    private Map<String, Map<String, String>> getId2Alias(JSONObject jsonObject) {
        String productId = jsonObject.getString("productId");
        Map<String,Map<String,String>>  id2Alias = new HashMap<>();
        Map<String ,String> gmap = new HashMap<>();
        gmap.put("true","真");
        gmap.put("false","假");
        id2Alias.put("userGroup",gmap );
        jsonObject.getJSONArray("by_fields").stream().filter(x->!"all".equalsIgnoreCase(x.toString())).forEach(x->{
            String by = x.toString();
            String[] split = by.split("\\.", 2);
            String type = split[0];
            Map<String ,String> map = new HashMap<>();
            if ("event".equals(type)) {
                List<Metadata> list =   new ArrayList<>();//metadataMapper.getMetadataValueName(productId,split[1]);     //    select t.original, t.display from METAdata t where t.productid = 11188  and t.metadata_type = 'version';
                list.stream().filter(
                        v-> v.getOriginal() != null && !"".equals(v.getOriginal()))
                        .forEach(
                                v -> map.put(v.getOriginal(),(null == v.getDisplay() || "".equals(v.getDisplay()))? v.getOriginal(): v.getDisplay()));
                id2Alias.put(by,map);
            }else if ("user".equals(type)) {
                List<UserMetadata> list =
                        new ArrayList<>();//userMtadataMapper.getMetadataValueName(productId,split[1]);  //    SELECT T.ORIGINAL, T.META_TYPE ,T.DISPLAY  FROM COBUB_USER_METADATA  T  WHERE T.PRODUCTID = 11188 AND T.ENABLED =1 AND T.META_TYPE = 'duration'
                list.stream().filter(v->v.getOriginal()!=null && !"".equals(v.getOriginal())).forEach(
                        v -> map.put(v.getOriginal(),(null == v.getDisplay() || "".equals(v.getDisplay()))? v.getOriginal(): v.getDisplay()));
                id2Alias.put(by,map);
            }
        });
        return id2Alias;
    }

    private LinkedList<String> getHourSeries(int from ,int to){
        LinkedList<String> res = new LinkedList();
        for (int i = from; i <=to; i++) {
            res.add(String.format("%02d:00", i));
        }
        return res;
    }

    private JSONArray getDateSeries(JSONObject jsonObject) {
        JSONArray series = new JSONArray();
        String unit = jsonObject.getString("unit");
        String from = jsonObject.getString("from_date");
        String to = jsonObject.getString("to_date");
        if("hour".equalsIgnoreCase(unit)){
            String from_hour = jsonObject.getString("from_hour");
            String to_hour = jsonObject.getString("to_hour");
            Long gapDays = DateUtil.getGapDays(to, from);


            if(gapDays >=2){
                getHourSeries(Integer.valueOf(from_hour),23).forEach(h -> series.add(from +" " + h));
                List<String> dayList = DateUtil.getDayOfRange(DateUtil.stringDateDecrease(from, 1), DateUtil.stringDateDecrease(to, -1));
                dayList.forEach(d-> getHourSeries(0,23).forEach(h-> series.add(d+" " + h)));
                getHourSeries(0,Integer.valueOf(from_hour)).forEach(h ->  series.add(to+" " + h));
            }else if (gapDays == 1){
                getHourSeries(Integer.valueOf(from_hour),23).forEach(h -> series.add(from +" " + h));
                getHourSeries(0,Integer.valueOf(from_hour)).forEach(h ->  series.add(to+" " + h));

            }else {
                getHourSeries(Integer.valueOf(from_hour),Integer.valueOf(to_hour)).forEach(h ->  series.add(to+" " + h));
            }
            return series;
        }else{
            List<String> dayOfRange = DateUtil.getDayOfRange(from, to);
            dayOfRange.forEach(x-> series.add(x));
        }
        return series;
    }
    private StringJoiner getActionCommWhere(JSONObject jsonObject) {
        StringJoiner commWhere = new StringJoiner(" AND ");
        String unit = jsonObject.getString(Constants.UNIT);
        String from = jsonObject.getString(Constants.FROM_DATE);
        String to = jsonObject.getString(Constants.TO_DATE);
        String productId = jsonObject.getString(Constants.PRODUCTID);
        String dateCon = String.format("( productid = '%s' AND day >= '%s' AND  day <= '%s' )", productId, from, to);
        if("hour".equalsIgnoreCase(unit)){
            String from_hour = jsonObject.getString("from_hour");
            String to_hour = jsonObject.getString("to_hour");
            Long gapDays = DateUtil.getGapDays(to, from);
            if(gapDays >=2){
                dateCon = String.format("( productid = '%s' AND ((day >= '%s' AND  day <= '%s') OR ((day='%s' AND hour >='%s' and hour<=23) OR (day='%s' AND hour >='00' and hour<= '%s' ))))", productId, DateUtil.stringDateDecrease(from, 1), DateUtil.stringDateDecrease(to, -1), from, from_hour, to, to_hour);
            }else if (gapDays == 1){
                dateCon = String.format("( productid = '%s' AND ((day='%s' AND hour >='%s' and hour<='23') OR (day='%s' AND hour >='00' and hour<= '%s' )))", productId, from, from_hour, to, to_hour);
            }else {
                dateCon = String.format("( productid = '%s' AND (day=%s AND hour >='%s' and hour<= '%s'))", productId, from,from_hour, to_hour);
            }
        }
        commWhere.add(dateCon);
        return commWhere;
    }


    public static void main(String[] args) throws Exception{

        String resStr = "{\"msg\":\"自定义事件查询成功\",\"code\":0,\"data\":{\"detail_result\":{\"series\":[\"20190809\",\"20190810\",\"20190811\",\"20190812\",\"20190813\",\"20190814\",\"20190815\"],\"rows\":[{\"values\":[[0,5],[0,0],[0,0],[0,5],[0,10],[0,10],[0,0]],\"by_values\":[\"中国\",\"vivo X6Plus 05\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"e_sys_login\\u0001userid\\u0001B\"]},{\"values\":[[0,4],[0,0],[0,0],[0,4],[0,8],[0,8],[0,0]],\"by_values\":[\"中国\",\"vivo X6Plus 04\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"e_sys_login\\u0001userid\\u0001B\"]},{\"values\":[[0,6],[0,0],[0,0],[0,6],[0,12],[0,12],[0,0]],\"by_values\":[\"中国\",\"vivo X6Plus 06\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"e_sys_login\\u0001userid\\u0001B\"]},{\"values\":[[0,3],[0,0],[0,0],[0,3],[0,6],[0,6],[0,0]],\"by_values\":[\"中国\",\"vivo X6Plus 012别名\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"e_sys_login\\u0001userid\\u0001B\"]},{\"values\":[[0,3],[0,0],[0,0],[0,3],[0,6],[0,6],[0,0]],\"by_values\":[\"中国\",\"vivo X6Plus 03bak\"],\"event_indicator\":[\"$appClick\\u0001acc\\u0001A\",\"e_sys_login\\u0001userid\\u0001B\"]}]}}}";
//        new CustomActionServiceImpl().saveDataToHbase(10001,"cobub3:action_10001",JSONObject.parseObject(resStr).getJSONObject("data"));


        String s = "{\"jobId\":\"tmp_actionreport_job_a2d74085-b166-4f39-9a30-87c4b09f6d42\",\"result\":[\"sys_android006\\u0001wifi006\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00016\",\"sys_android003\\u0001wifi003\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u00019\",\"sys_android006\\u0001wifi006\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u000118\",\"sys_android002\\u0001wifi002\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00012\",\"sys_android004\\u0001wifi004\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00014\",\"sys_android003\\u0001wifi003\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u00016\",\"sys_android001\\u0001wifi001\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00011\",\"sys_android002\\u0001wifi002\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u00014\",\"androidp_pf_fo001\\u0001wuxian_wifi001\\u0001$appClick\\u0001userid\\u0001B\\u000120190808\\u00011\",\"sys_android005\\u0001wifi005\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u000115\",\"sys_android001\\u0001wifi001\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u00013\",\"sys_android004\\u0001wifi004\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u000112\",\"sys_android004\\u0001wifi004\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u00018\",\"sys_android002\\u0001wifi002\\u0001e_sys_login\\u0001acc\\u0001A\\u0001null\\u00016\",\"sys_android001\\u0001wifi001\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u00012\",\"androidp_pf_fo001\\u0001wuxian_wifi001\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"sys_android003\\u0001wifi003\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00013\",\"sys_android005\\u0001wifi005\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u000110\",\"sys_android006\\u0001wifi006\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190808\\u000112\",\"sys_android005\\u0001wifi005\\u0001e_sys_login\\u0001acc\\u0001A\\u000120190809\\u00015\"] }";

        JSONObject data = JSONObject.parseObject(resStr).getJSONObject("data");

        JSONArray save_result = new CustomActionServiceImpl().toSaveDataOp(JSONObject.parseObject(s));
        data.put("save_result",save_result);
        new CustomActionServiceImpl().saveDataToHbase(10001,"cobub3:action_10001",data);
    }


    private JSONArray toSaveDataOp(JSONObject jo){
        if (!jo.containsKey("result") || jo.getJSONArray("result").isEmpty()){
            return new JSONArray();
        }
        JSONArray result = jo.getJSONArray("result");

        JSONArray ja = new JSONArray();
        result.forEach(x->{
            String[] split = x.toString().split(Constants.SEPARATOR);
            int len = split.length;
            String date = split[len - 2];
            if (null != date && !"null".equalsIgnoreCase(date)){
                String num = split[len - 1]; //
                String flag = split[len-3];
                String indicator = split[len-4];
                String action = split[len-5];
                StringJoiner by = new StringJoiner(Constants.SEPARATOR);
                if(len == 5 ){
                    by.add("all");
                }else {
                    for (int i = 0; i < len - 5; i++) {
                        by.add(split[i]);
                    }
                }
                String rkSuffix = String.join(Constants.SEPARATOR,date,action,indicator,by.toString(),flag);
                JSONObject j = new JSONObject();
                j.put(rkSuffix,num);
                ja.add(j);
            }
        });
        return ja;
    }

    private void saveDataToHbase( Integer reportId, String tableName,JSONObject data){
        if (data.isEmpty() || null == data){
            return;
        }
        if(!data.containsKey("save_result") ){
            return;
        }
        if(data.containsKey("save_result") && (data.getJSONArray("save_result").isEmpty() ||  null == data.getJSONArray("save_result"))){
            return;
        }
        String qualifier = "num";
        int PUT_NUM_PER_BATCH = 200;
        Table table = Connection2hbase.getTable(tableName);
        List<Put> puts = new ArrayList<>();
        data.getJSONArray("save_result").forEach(v->{
            JSONObject obj = JSONObject.parseObject(v.toString());
            obj.forEach((rkSuffix,num)->{
                String rk = String.join(Constants.SEPARATOR, String.valueOf(reportId), rkSuffix);
                Put put = new Put(Bytes.toBytes(rk));
                put.addColumn(Bytes.toBytes("f"),Bytes.toBytes(qualifier),Bytes.toBytes(num.toString()));
                puts.add(put);

                if (puts.size() % PUT_NUM_PER_BATCH == 0) {
                    try {
                        table.put(puts);
                        puts.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        });
        if (!puts.isEmpty()) {
            try {
                table.put(puts);
                puts.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
