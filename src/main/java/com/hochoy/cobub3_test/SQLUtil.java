package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.cobub.analytics.web.service.UserMetadataService;
import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
import scala.Tuple2;
import scala.Tuple5;
import scala.Tuple6;
import scala.Tuple7;

import java.util.*;

/**
 * 拼SQL语句
 */

/**
 * 拼SQL语句
 */
public class SQLUtil {

    public static final String AND = " and ";
    private static final String PARAMS = "params";
    private static final String FUNCTION = "function";
    private static final String NOT_LIKE = " not like '%";
    private static final String LIKE = " like '%";
    private SQLUtil() { }


    /**
     * 根据eventOriginal获取category
     * @param eventOriginal 事件
     * @return 结果
     */
    public static String getCategory(String eventOriginal) {
        String category;
        if ("$launch".equalsIgnoreCase(eventOriginal)) {
            category = "cd";
        } else if ("$exitPage".equalsIgnoreCase(eventOriginal)) {
            category = "usinglog";
        } else if ("$crash".equalsIgnoreCase(eventOriginal) || "$error".equalsIgnoreCase(eventOriginal)) {
            category = "error";
        } else {
            category = "event";
        }
        return category;
    }
    /**
     * 判断符号
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeSymbol(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType,String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhere(sqlBuilder, type, params, fieldType,  tableType);
                break;
            case "notEqual":
                pieceNotEqualWhere(sqlBuilder, type, params, fieldType);
                break;
            case "contain":
                pieceContainWhere(sqlBuilder, type, params);
                break;
            case "notContain":
                pieceNotContainWhere(sqlBuilder, type, params);
                break;
            case Constants.ISTRUE:
                pieceIsTrueWhere(sqlBuilder, type);
                break;
            case Constants.ISFALSE:
                pieceIsFalseWhere(sqlBuilder, type);
                break;
            case "more":
                pieceMoreWhere(sqlBuilder, type, params);
                break;
            case "less":
                pieceLessWhere(sqlBuilder, type, params);
                break;
            case Constants.REGION:
                pieceRegionWhere(sqlBuilder, type, params);
                break;
            default:
                break;
        }
    }

    /**
     * 拼凑包含条件
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceContainWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        final String param = params.getString(0);
        // 针对类似苹果手机一对多关系的处理
        if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
            String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
            StringBuilder likeStr = new StringBuilder();
            sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND);
            likeStr.append(type).append(LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(" or ").append(type).append(LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append("))");
        } else {
            sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append(LIKE).append(param).append("%'))");
        }
    }

    /**
     * 拼凑不包含条件
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotContainWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        final String param = params.getString(0);

        // 针对类似苹果手机一对多关系的处理
        if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
            String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
            StringBuilder likeStr = new StringBuilder();
            sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND);
            likeStr.append(type).append(NOT_LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(AND).append(type).append(NOT_LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append(")");
        } else {
            sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append(NOT_LIKE).append(param).append("%'))");
        }
    }

    /**
     * 拼凑不等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotEqualWhere(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append("(").append(type).append(" not in ").append(in).append(")");
    }

    /**
     * 拼凑等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceEqualWhere(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType,String tableType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append(tableType).append(type).append(" in ").append(in);
    }

    /**
     * 拼凑大于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceMoreWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append(">").append(Long.valueOf( params.getString(0))).append("))");
    }
    /**
     * 拼凑小于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceLessWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append("<").append(Long.valueOf( params.getString(0))).append("))");
    }

    /**
     * 拼凑区间
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceRegionWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(" is not null ) AND ( ").append(type).append(">=").append(Long.valueOf( params.getString(0))).append(") AND");
        sqlBuilder.append("(").append(type).append("<=").append(Long.valueOf( params.getString(1))).append("))");
    }


    /**
     * 拼凑为真
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsTrueWhere(StringBuilder sqlBuilder, String type) {
        if(type.startsWith("event.")){
            sqlBuilder.append("((").append(type.substring(6)).append(Constants.IS_NOT_NULL_AND).append(type.substring(6)).append(Constants.EQUAL_TRUE).append("))").append(" and ");
        }else{
            sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append(Constants.EQUAL_TRUE).append("))").append(" and ");
        }
    }

    /**
     * 拼凑为假
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsFalseWhere(StringBuilder sqlBuilder, String type) {
        sqlBuilder.append("((").append(type).append(Constants.IS_NOT_NULL_AND).append(type).append(Constants.EQUAL_FALSE).append("))").append(" and ");
    }

    /**
     * 拼凑查询的某个筛选条件的值是多个的，如苹果手机
     *
     * @param params 参数
     * @param in     拼凑的sql
     */
    private static void pieceParams(JSONArray params, StringBuilder in, String fieldType) {
        if (StringUtils.isEmpty(fieldType)){
            dealParamForString(params, in);
        }else {
            switch (fieldType){
                case "StringType":
                    dealParamForString(params, in);
                    break;
                case "ShortType":
                    dealParamForInteger(params, in);
                    break;
                case "LongType":
                    dealParamForInteger(params, in);
                    break;
                default:
                    dealParamForString(params, in);
                    break;
            }
        }
    }

    /**
     * 处理参数为字符数据
     * @param params 参数
     * @param in 结果
     */
    private static void dealParamForString(JSONArray params, StringBuilder in) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);

            // 处理一对多的关系，如苹果手机
            if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
                String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
                for (String string : strings) {
                    in.append("'").append(string).append("',");
                }
            } else {
                in.append("'").append(param).append("',");
            }
        }
    }

    /**
     * 处理参数为整型数据
     * @param params 参数
     * @param in 结果
     */
    private static void dealParamForInteger(JSONArray params, StringBuilder in) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            in.append(param).append(",");
        }
    }


    /**
     * 排序
     *
     * @param oldMap 原数据
     * @return 排序后的数据
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> oldMap) {
        if (oldMap == null || oldMap.isEmpty()) {
            return oldMap;
        }
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(oldMap.entrySet());
        list.sort((arg0, arg1) -> arg1.getValue() - arg0.getValue());
        Map<String, Integer> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aList : list) {
            newMap.put(aList.getKey(), aList.getValue());
        }
        return newMap;
    }

    /**
     * 拼凑漏斗过滤条件sql
     *
     * @param sqlBuilder sql字符串
     * @param conditions 过滤条件
     * @param relation   关系
     */
    public static void pieceFunnelFilterSql(StringBuilder sqlBuilder, JSONArray conditions, String relation) {

        if (null == conditions || conditions.isEmpty()) {
            return;
        }

        if ("and".equals(relation)) {
            final int size = conditions.size();
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String fieldType = condition.getString("fieldType");
                String function = condition.getString(FUNCTION);
                JSONArray params = condition.getJSONArray(PARAMS);
                String tableType ="";
                judgeSymbol(sqlBuilder, type, function, params, fieldType,tableType);
                sqlBuilder.append(AND);
            }
            sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
        } else if ("or".equals(relation)) {
            final int size = conditions.size();
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String fieldType = condition.getString("fieldType");
                String function = condition.getString(FUNCTION);
                JSONArray params = condition.getJSONArray(PARAMS);
                String tableType ="";
                judgeSymbol(sqlBuilder, type, function, params, fieldType,tableType);
                if (sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
                }
                sqlBuilder.append(" or ");
            }
            sqlBuilder.delete(sqlBuilder.lastIndexOf("or"), sqlBuilder.length());
        }
    }

    public static Tuple2<String, String> getIndicatorType(String eventType) {
        String indicatorType = "";
        String event = "";
        switch (eventType) {
            case "acc":
                indicatorType = "COUNT(1) AS ct";
                event = "1";
                break;
            case Constants.USERID:
                indicatorType = "COUNT(DISTINCT deviceid)  AS ct";
                event = "deviceid";
                break;
            case Constants.LOGINUSER:
                indicatorType = "COUNT(DISTINCT userid)  AS ct";
                event = Constants.USERID;
                break;
            default:
                break;
        }
        return new Tuple2<>(indicatorType, event);
    }

    /**
     * 根据 每一组的 查询条件 和 条件关系(adn/or) 拼接 每一组 action 表和user表的where 条件
     *
     * @param conditions 查询条件集合
     * @param relation   条件间的逻辑关系 ：and / or
     * @return 五元组：(
     * action的where条件，user的where条件，用户分群id的set集合，查询条件中action属性列名set集合，用户属性名及其类型的map)
     */
    public static Tuple5<
            StringJoiner, StringJoiner,
            Set<String>, Set<String>,
            Map<String, String>
            > queryConditionOp(JSONArray conditions, String relation, String productId) {

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
                    actionFields.add(column);
                    switch (function) {
                        case "equal": { // string  and  Number
                            String equalCon = equalConditionOp(params, isNumber);
                            con = String.format("(%s IN  %s)", column,equalCon);
                            actionWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  Number
                            String equalCon = equalConditionOp(params, isNumber);
                            con = String.format("(%s NOT IN  %s)", column, equalCon );
                            actionWhere.add(con);// IN  v.s NOT IN
                            break;
                        }
                        case "contain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", column, column, "%" +param +"%"));// LIKE  v.s    LIKE
                            break;
                        }
                        case Constants.ISTRUE:
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", column, column));
                            break;
                        case Constants.ISFALSE:
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", column, column));
                            break;
                        case "notContain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", column, column, "%" +param +"%"));// LIKE  v.s NOT LIKE
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
                        case Constants.REGION: {  // Number  between ：  >= and <=
                            String param1 = condition.getString(Constants.PARAM1);
                            String param2 = condition.getString(Constants.PARAM2);
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", column, param1, column, param2);
                            actionWhere.add(con);
                            break;
                        }
                    }


                    break;
                }
                case "user": {
                    String propIdInHBase = productId+"_"+column;
                    switch (function) {
                        // String:
                        case "equal": { // string  and  Number
                            StringJoiner joiner = new StringJoiner(" or ", "(", ")");
                            if (Constants.ISTRUE.equals(isNumber)) {
                                params.forEach(x -> joiner.add(String.format("  %s = %s", propIdInHBase, x)));
                                userProps.put(propIdInHBase, "int");
                            } else {
                                params.forEach(x -> {
                                    if(x.toString().contains("#_#")){
                                        Arrays.asList(x.toString().split("#_#")).forEach(k->  joiner.add(String.format("  %s = '%s'", propIdInHBase, k)));
                                    }else {
                                        joiner.add(String.format("  %s = '%s'", propIdInHBase, x));
                                    }
                                });
                                userProps.put(propIdInHBase, "string");
                            }
                            con = String.format("( %s IS NOT NULL  AND  %s )", propIdInHBase, joiner.toString());
                            userWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  Number
                            StringJoiner joiner = new StringJoiner(" AND ", "(", ")");  // and v.s and  or
                            if (Constants.ISTRUE.equals(isNumber)) {
                                params.forEach(x -> joiner.add(String.format("  %s <> %s", propIdInHBase, x)));
                                userProps.put(propIdInHBase, "int");
                            } else {
                                params.forEach(x -> {
                                    if(x.toString().contains("#_#")){
                                        Arrays.asList(x.toString().split("#_#")).forEach(k->  joiner.add(String.format("  %s <> '%s'", propIdInHBase, k)));
                                    }else {
                                        joiner.add(String.format("  %s <> '%s'", propIdInHBase, x));
                                    }
                                });
                            }
                            userWhere.add(String.format("((%s IS NOT NULL ) AND  %s )", propIdInHBase, joiner.toString()));
                            break;
                        }
                        case "contain": { //string like
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", propIdInHBase, propIdInHBase, "%" +param +"%");
                            userWhere.add(con);// LIKE  v.s    LIKE
                            userProps.put(propIdInHBase, "string");
                            break;
                        }
                        case "notContain": { //string
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", propIdInHBase, propIdInHBase, "%" +param +"%");
                            userWhere.add(con);//NOT LIKE    v.s NOT LIKE
                            userProps.put(propIdInHBase, "string");
                            break;
                        }
                        case Constants.ISTRUE:
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", propIdInHBase, propIdInHBase));
                            userProps.put(propIdInHBase, "boolean");
                            break;
                        case Constants.ISFALSE:
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", propIdInHBase, propIdInHBase));
                            userProps.put(propIdInHBase, "boolean");
                            break;
                        case "more": {  // Number >
                            String param = params.getString(0);
                            con = String.format("(%s  >  %s)", propIdInHBase, param);
                            userProps.put(propIdInHBase, "int");
                            userWhere.add(con);
                            break;
                        }
                        case "less": { // Number <
                            String param = params.getString(0);
                            con = String.format("(%s  <  %s)", propIdInHBase, param);
                            userProps.put(propIdInHBase, "int");
                            userWhere.add(con);
                            break;
                        }
                        case Constants.REGION: {  // Number  between ：  >= and <=
                            String param1 = condition.getString(Constants.PARAM1);
                            String param2 = condition.getString(Constants.PARAM2);
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", propIdInHBase, param1, propIdInHBase, param2);
                            userProps.put(propIdInHBase, "int");
                            userWhere.add(con);
                            break;
                        }


                    }
                    break;
                }
                case "userGroup": {
                    String param = "false";
                    if (Constants.ISTRUE.equals(function)) {
                        param = "true";
                    }
                    String groupIdInHBase = productId+"_"+column;
                    userWhere.add(String.format("((%s IS NOT NULL ) AND (%s = %s) )", groupIdInHBase, groupIdInHBase, param));
                    groupId.add(groupIdInHBase);
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
    public static Tuple7<StringJoiner, StringJoiner, HashSet, HashSet,
            HashMap<String, String>,
            HashSet<String>
            , StringJoiner
            > byFieldOp(JSONArray by_field, String productId,Map<String, String> userPropertiesMap) {

        StringJoiner outGroupByUser = new StringJoiner(", "); // user group by 字段
        StringJoiner outGroupByAction = new StringJoiner(", "); //  action group by 字段

        HashSet outSelectFieldUser = new HashSet(); //  user select  字段
        HashSet outSelectFieldAction = new HashSet(); // action select  字段

        Map<String, String> userProps = new HashMap<>();
        HashSet<String> groupId = new HashSet<>();
        StringJoiner allSelect = new StringJoiner(", ");

        by_field.stream().filter(x->!"all".equalsIgnoreCase(x.toString())).forEach(field -> {
            String[] split = field.toString().split("\\.", 2);
            String type = split[0];
            String column = split[1];
            if ("event".equals(type)) {
                //parquetTmpTable 表
                outGroupByAction.add(column);//  group by 字段拼接
                outSelectFieldAction.add(column); // select 字段拼接
                allSelect.add(column);
            } else {
                String columnInHBase = productId+"_"+column;
                allSelect.add(columnInHBase);
                if ("user".equals(type)) {

                    //usersTable  表的用户属性
                    outGroupByUser.add(columnInHBase);// group by 字段拼接
                    userProps.put(columnInHBase, userPropertiesMap.get(column));
                    outSelectFieldUser.add(columnInHBase);// select 字段拼接

                } else if ("userGroup".equals(type)) {
                    //usersTable  表的用户分群属性处理

                    outGroupByUser.add(columnInHBase);// group by 字段拼接
                    groupId.add(columnInHBase);
                    outSelectFieldUser.add(columnInHBase);// select 字段拼接
                }

            }

        });
        return new Tuple7(outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction, userProps, groupId, allSelect);
    }

    private static String equalConditionOp(JSONArray params, String isNumber) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        if (Constants.ISTRUE.equals(isNumber)) {
            params.forEach(x -> joiner.add(String.format(" %s ", x)));
        } else {
            params.forEach(x -> {
                if(x.toString().contains("#_#")){
                    Arrays.asList(x.toString().split("#_#")).forEach(k->  joiner.add(String.format("'%s'", k)));
                }else {
                    joiner.add(String.format("'%s'", x.toString()));
                }
            });
        }
        return joiner.toString();
    }

}
