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
        String relation = filters.getString("relation"); // 主查询条件 逻辑关系 and / or
        String unit = jsonObject.getString(Constants.UNIT);
        String from = jsonObject.getString(Constants.FROM_DATE);
        String to = jsonObject.getString(Constants.TO_DATE);
        String productId = jsonObject.getString(Constants.PRODUCTID);
        if (!"day".equalsIgnoreCase(unit)){
            return new JSONObject();
        }

        StringJoiner commWhere = new StringJoiner(" AND ");
//
        String dateCon = String.format("( productid = '%s' AND day >= '%s' AND  day <= '%s' )", productId, from, to);
        commWhere.add(dateCon);
        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标


        Map map = new HashMap();
        map.put("relation", relation);
        map.put("commWhere", commWhere);
        map.put("productId", productId);

        Tuple2 queryOrSaveOp = queryOrSaveOp(jsonObject);
        /**
         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
         */
        Tuple3<String,String,Object> sqlOps = generateMultipleIndicatorsSQL(actions, (Tuple7)queryOrSaveOp._1, (Tuple5) queryOrSaveOp._2, map,true);

        JSONObject responseResult = querySparkSql(new StringBuilder(sqlOps._1()), maxLine,sqlOps._2());

        if (null == responseResult || !responseResult.containsKey("result") ||responseResult.isEmpty() ||responseResult == null ||
                (responseResult.containsKey("status") && responseResult.getString("status").equalsIgnoreCase("error"))) {
            return new JSONObject();
        }
        logger.debug("responseResult : {}"+responseResult);

        Tuple3<JSONArray, JSONArray, JSONArray> commReturn = commReturnOp(jsonObject);

        JSONObject result = resultOp(responseResult, commReturn,(LinkedList)sqlOps._3());

        return result;
    }

    private Tuple2<Tuple7,Tuple5>  queryOrSaveOp(JSONObject jsonObject){
        String productId = jsonObject.getString(Constants.PRODUCTID);

        JSONObject filters = jsonObject.getJSONObject(Constants.FILTER);//外部筛选条件
        JSONArray fields = jsonObject.getJSONArray(Constants.BY_FIELDS);

        JSONArray conditions = filters.getJSONArray(Constants.CONDITIONS);
        String relation = conditions.size() < 2 ? Constants.AND : filters.getString("relation"); // 主查询条件 逻辑关系 and / or


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






    private Tuple3<JSONArray,JSONArray,JSONArray> commReturnOp(JSONObject jsonObject ) {
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



    private JSONObject resultOp(JSONObject jo, Tuple3<JSONArray,JSONArray,JSONArray> commReturn,LinkedList idxs) {


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

        String groupAndGroupingBy = " %s GROUPING SETS(( %s, day ),( %s ) )";

        String groupAndGroupByEachSelect;
        if(isQuery) {
            groupAndGroupByEachSelect = String.format(
                    groupAndGroupingBy,
                    String.join(",", partialAggGroupBy, "day"),
                    partialAggGroupBy, partialAggGroupBy);
        }else {
            groupAndGroupByEachSelect = partialAggGroupBy ;
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
            commWhere1.add(commWhere).add(String.format("(category = '%s')", category)).add(String.format("( action = '%s' )",eventOriginal));
            JSONObject childFilterParam = action.getJSONObject(Constants.CHILDFILTERPARAM);
            JSONArray conditions = childFilterParam.getJSONArray("conditions");
            String relate = conditions.size() >= 2 ? (childFilterParam.getString("relation")):(Constants.AND);
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
            actionSelectSet.addAll(Arrays.asList("global_user_id", "action", "day"));
            actionSelectSet.addAll(outSelectFieldAction);
            actionSelectSet.add(eventCol);

            HashSet<String> userSelectSet = new HashSet<>();
            userSelectSet.add("pk");
            userSelectSet.addAll(outSelectFieldUser);

            // actionSelect : global_user_id, 分组字段，action，day

            String userSelect = String.join(", ", userSelectSet);   //userSelect 基本筛选条件 : pk, 分组字段


            //joinselect : 分组字段，action，day, 指标 , 'A' AS an
            String joinSelect ;
            char c = (char) (i.getAndAdd(1));
            idxs.add(String.join(separator,eventOriginal,eventType,String.valueOf(c)));
            if (!groupByJoiner.toString().isEmpty()){
                joinSelect = String.join(", ", groupByJoiner.toString(), "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", c), indicatorType);
            }else {
                joinSelect = String.join(", ",  "action", "day", String.format("'%s' AS INDICATORTYPE",eventType), String.format("'%s' AS an", c), indicatorType);
            }

            if (outGroupByUser.toString().isEmpty() && inUserWhere.toString().isEmpty() && outUserWhere.toString().isEmpty()) {
                //分组、内部条件、外部条件中只有事件属性，只查 parquet 表
                StringJoiner actionWhere = getWhereOfUserOrAction(commWhere1, outActionWhere, inActionWhere);
                String singleSQL;
                singleSQL = String.format(parquetSQL, joinSelect, actionWhere.toString(), groupAndGroupByEachSelect);
                sqlList.add(singleSQL);
            } else {
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
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, groupAndGroupByEachSelect);
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
//                        String partialAggSQLFormat = "select %s from %s group by %s";

                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, groupAndGroupByEachSelect);
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
                        String actionSelectOr = String.join(String.join(", ", actionSelectSetOr),String.join(",", String.format("'%s' AS INDICATORTYPE",eventType)));
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
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, groupAndGroupByEachSelect);
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
                    String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, groupAndGroupByEachSelect);
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


        String group = String.join(",", partialAggGroupBy,"INDICATORTYPE", "an","day");;
        String allSelect ;
        if( isQuery ){
//            String groupingSet = String.format("GROUPING SETS(( %s ),(%s) ) ORDER BY %s", String.join(",",partialAggGroupBy,"INDICATORTYPE","an"), grouping,partialAggGroupBy); // groupby + action
            if (!allSelectJoiner.toString().isEmpty()){
                allSelect = String.join(", ", allSelectJoiner.toString(), "action","INDICATORTYPE","an", "day", "SUM(ct)");
            }else{
                allSelect = String.join(", ", "action","INDICATORTYPE","an", "day", "SUM(ct)");
            }

        }else {

            if (!allSelectJoiner.toString().isEmpty()){
                allSelect = String.join(", ","action","INDICATORTYPE", allSelectJoiner.toString(), "an",  "SUM(ct) as num");
            }else{
                allSelect = String.join(", ", "action","INDICATORTYPE","'all'","an", "SUM(ct) as num");
            }

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

        logger.debug("taskSql: {}", taskSql );
        logger.debug("allSQL: {}", allSQL);
        System.out.println("taskSql: {}"+ taskSql );;
        System.out.println("allSQL: {} " + allSQL);

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
//
        String dateCon = String.format("( productid = '%s' )", productId);
        commWhere.add(dateCon);
        JSONArray actions = jsonObject.getJSONArray(Constants.ACTION);//多指标
        // 数据
        JSONObject data = jsonObject.getJSONObject("data");



        Map map = new HashMap();
        map.put("relation", relation);
        map.put("commWhere", commWhere);
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
    private void saveDataToHbase( Integer reportId, String tableName, JSONObject data)  {
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
                    count ++;
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



    public static void main(String[] args) throws Exception {
        CustomActionServiceImpl impl = new CustomActionServiceImpl();

        String inputStr;
//        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\"],\"to_date\":\"20190709\",\"productId\":\"11128\",\"action\":[{\"eventType\":\"loginUser\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[]}}]}";
        inputStr = "{\"filter\":{\"conditions\":[],\"relation\":\"and\"},\"unit\":\"day\",\"from_date\":\"20190703\",\"by_fields\":[\"event.country\",\"user.durationa\",\"event.city\",\"userGroup.firstsixbak\"],\"to_date\":\"20190709\",\"productId\":\"11128\",\"action\":[{\"eventType\":\"acc\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.channelid\",\"function\":\"equal\",\"params\":[\"ad_qd002\",\"ad_qd004\"],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"userid\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[{\"type\":\"userGroup.firstsixbak\",\"function\":\"isTrue\",\"params\":[],\"input\":\"\",\"isRegion\":\"isFalse\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\"}],\"relation\":\"and\"}},{\"eventType\":\"loginUser\",\"eventOriginal\":\"$appClick\",\"childFilterParam\":{\"conditions\":[],\"relation\":\"and\"}}]}";
        JSONObject jo1 = JSONObject.parseObject(inputStr);

        impl.getQueryResult(jo1);

    }

    private JSONObject querySparkSql(StringBuilder sb, String s,String ss){
        String jo2s = "{\"jobId\":\"1d2f4a98-5515-40d2-98ed-c6ebfa696e39\",\"result\":[\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00013\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190704\\u000115\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u00015\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u000127\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190708\\u00011\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00013\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00011\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u000112\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190708\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190707\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u0001null\\u00011\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u00013\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u000112\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00013\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00019\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190707\\u00013\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190708\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u00013\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u0001null\\u00011\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u000112\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00013\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001112003\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190704\\u00016\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001null\\u0001南通\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00012\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001acc\\u0001A\\u000120190703\\u00011\",\"中国\\u0001112001\\u0001南通\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190708\\u00011\",\"中国\\u0001112005\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190707\\u00011\",\"中国\\u0001112004\\u0001南京\\u0001true\\u0001$appClick\\u0001userid\\u0001B\\u000120190703\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190704\\u00014\",\"中国\\u0001112006\\u0001南京\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190703\\u00011\",\"中国\\u0001112002\\u0001南通\\u0001true\\u0001$appClick\\u0001loginUser\\u0001C\\u000120190708\\u00011\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u000120190708\\u00013\",\"中国\\u0001null\\u0001南京\\u0001false\\u0001$appClick\\u0001acc\\u0001A\\u0001null\\u000113\"]}";
        JSONObject jo2 = JSONObject.parseObject(jo2s);
        return jo2;
    }


}
