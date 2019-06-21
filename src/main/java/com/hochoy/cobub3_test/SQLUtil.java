package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼SQL语句
 */
public class SQLUtil {

    public static final String AND = " and ";
    private static final String PARAMS = "params";
    private static final String FUNCTION = "function";
    private static final String NOT_LIKE = " not like '%";
    private static final String LIKE = " like '%";


    private SQLUtil() {
    }

    /**
     * 拼凑子查询字段
     *
     * @param sqlForEvent 事件属性语句
     * @param sqlForUser  用户属性语句
     * @param outField    用户外部查询字段
     * @param fields      字段，即分组
     * @param actions     事件
     * @param unit        聚合单位
     */
    public static void pieceInnerSql(StringBuilder sqlBuilder, StringBuilder sqlForEvent, StringBuilder sqlForUser,
                                     StringBuilder outField, List<String> groupidList, List<String> propList, JSONArray fields,
                                     JSONArray actions, String unit, String productId) {
        // 如果多个事件会出错
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            String eventType = action.getString(Constants.EVENT_TYPE);
            switch (eventType) {
                case "acc":
                    sqlForEvent.append("SELECT global_user_id,");
                    break;
                case "userid":
                    sqlForEvent.append("SELECT deviceid,global_user_id,");
                    break;
                case "loginUser":
                    sqlForEvent.append("SELECT userid,global_user_id,");
                    break;
                default:
                    break;
            }
        }
        sqlForUser.append("SELECT pk ,");
        sqlBuilder.append("SELECT ");
        //  groupid.append("{");
        for (int i = 0; i < fields.size(); i++) {
            if ("all".equals(fields.getString(i))) {
                continue;
            }
            if (fields.getString(i).startsWith("event.")) {//事件属性
                String event = fields.getString(i).substring(6);
                sqlForEvent.append(event).append(",");
                outField.append(event).append(",");
            } else if (fields.getString(i).startsWith("user.")) {//用户属性
                String metaType = fields.getString(i).substring(5);
                sqlForUser.append(metaType).append(",");
                outField.append(metaType).append(",");
                if (!propList.contains(metaType)) {
                    propList.add(metaType);
                }
            } else if (fields.getString(i).startsWith("userGroup.")) {//用户属性
                String userGroup = fields.getString(i).substring(10);
                sqlForUser.append(productId).append("_").append(userGroup).append(",");
                outField.append(productId).append("_").append(userGroup).append(",");
                if (!groupidList.contains(productId + "_" + userGroup)) {
                    groupidList.add(productId + "_" + userGroup);
                }
            }
        }
        sqlBuilder.append(outField);


        if (StringUtils.isNotEmpty(unit)) {
            sqlForEvent.append(unit).append(",action FROM parquetTmpTable WHERE(");
            sqlBuilder.append(unit);
        } else {
            sqlForEvent.append(" action FROM parquetTmpTable WHERE(");
        }

        if (!outField.toString().endsWith(",")) {
            outField.append(",");
        }
        if (!sqlBuilder.toString().endsWith(",")) {
            sqlBuilder.append(",");
        }


        // 如果多个事件要改造，现在只支持单个事件
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            String eventOriginal = action.getString("eventOriginal");
            String eventType = action.getString("eventType");

            // 分别为总次数、触发用户数、登录用户数
            switch (eventType) {
                case "acc":
                    sqlBuilder.append("count(action) FROM (");
                    break;
                case "userid":
                    sqlBuilder.append("count(distinct deviceid) FROM (");
                    break;
                case "loginUser":
                    sqlBuilder.append("count(distinct userid) FROM (");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拼凑where条件
     *
     * @param sqlForEvent sql事件属性字符串
     * @param sqlForUser  sql用户属性字符串
     * @param conditions  过滤条件
     * @param relation    关系 and 或者 or
     */
    public static void pieceSqlWhere(StringBuilder sqlForEvent, StringBuilder sqlForUser, List<String> groupidList,
                                     List<String> propList, JSONArray conditions, String relation, String productId) {

        if (null == conditions || conditions.isEmpty()) {
            return;
        }

        final int size = conditions.size();
        if ("and".equals(relation)) {
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                String isNumber = condition.getString("isNumber");
                String isRegion = condition.getString("isRegion");
                JSONArray params = condition.getJSONArray(PARAMS);
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isFalse".equals(isRegion)) {
                    String inputForint = condition.getString("inputForInt");
                    String[] inputValues = inputForint.split(",");
                    if (inputValues != null && inputValues.length > 0) {
                        for (int k = 0; k < inputValues.length; k++) {
                            params.add(inputValues[k]);
                        }
                    }
                }
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isTrue".equals(isRegion)) {
                    String param1 = condition.getString("param1");
                    String param2 = condition.getString("param2");
                    params.add(param1);
                    params.add(param2);
                }

                if (type.startsWith("event.")) {//事件属性
                    if (!sqlForEvent.toString().trim().endsWith("and")) {
                        sqlForEvent.append(AND);
                    }
                    String tableType = "";
                    judgeSymbol(sqlForEvent, type.substring(6), function, params, fieldType, tableType);
                } else if (type.startsWith("user.")) {//用户属性
                    String tableType = "";
                    judgeUserSymbolForAnd(sqlForUser, type.substring(5), function, params, fieldType, isNumber, isRegion, tableType);
                    if (!sqlForUser.toString().trim().endsWith("and")) {
                        sqlForUser.append(AND);
                    }
                    if (!propList.contains(type.substring(5))) {
                        propList.add(type.substring(5));
                    }
                } else if (type.startsWith("userGroup.")) {//用户分群
                    judgeSymbolForUserGroup(sqlForUser, type.substring(10), function, params, fieldType, productId);
                    //   groupid.append(productId).append("_").append(type.substring(10)).append(",");

                    if (!groupidList.contains(productId + "_" + type.substring(10))) {
                        groupidList.add(productId + "_" + type.substring(10));
                    }

                }
            }
        } else if ("or".equals(relation)) {
            sqlForEvent.append(AND).append("( ");
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                JSONArray params = condition.getJSONArray(PARAMS);
                String isNumber = condition.getString("isNumber");
                String isRegion = condition.getString("isRegion");
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isFalse".equals(isRegion)) {
                    String inputForint = condition.getString("inputForInt");
                    params.add(inputForint);
                }
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isTrue".equals(isRegion)) {
                    String param1 = condition.getString("param1");
                    String param2 = condition.getString("param2");
                    params.add(param1);
                    params.add(param2);
                }

                if (type.startsWith("event.")) {//事件属性
//                    String tableType="e1.";
                    String tableType = "";
                    judgeSymbol(sqlForEvent, type.substring(6), function, params, fieldType, tableType);
                    if (sqlForEvent.toString().trim().endsWith("and")) {
                        sqlForEvent.delete(sqlForEvent.lastIndexOf("and"), sqlForEvent.length());
                    }
                    sqlForEvent.append(" or ");
                } else if (type.startsWith("user.")) {//用户属性
                    if (sqlForUser.toString().trim().endsWith("AND")) {
                        sqlForUser.append("( (");
                    } else {
                        sqlForUser.append("( ");
                    }
//                    String tableType="e2.";
                    String tableType = "";
                    judgeUserSymbolForAnd(sqlForUser, type.substring(5), function, params, fieldType, isNumber, isRegion, tableType);

                    if (sqlForUser.toString().trim().endsWith("and")) {
                        sqlForUser.delete(sqlForUser.lastIndexOf("and"), sqlForUser.length());
                    }
                    sqlForUser.append(" )");
                    sqlForUser.append(" or ");
                    if (!propList.contains(type.substring(5))) {
                        propList.add(type.substring(5));
                    }
                } else if (type.startsWith("userGroup.")) {//用户分群
                    if (sqlForUser.toString().trim().endsWith("AND")) {
                        sqlForUser.append("( (");
                    } else {
                        sqlForUser.append("( ");
                    }
//                    String tableType="e2.";
                    String tableType = "";
                    //  judgeSymbol(sqlForUser, type.substring(10), function, params, fieldType, tableType);
                    judgeSymbolForUserGroup(sqlForUser, type.substring(10), function, params, fieldType, productId);
                    if (sqlForUser.toString().trim().endsWith("and")) {
                        sqlForUser.delete(sqlForUser.lastIndexOf("and"), sqlForUser.length());
                    }
                    sqlForUser.append(" )");
                    sqlForUser.append(" or ");
                    // groupid.append(productId).append("_").append(type.substring(10)).append(",");

                    if (!groupidList.contains(productId + "_" + type.substring(10))) {
                        groupidList.add(productId + "_" + type.substring(10));
                    }
                }
            }
            if (sqlForEvent.toString().trim().endsWith("AND")) {
                sqlForEvent.delete(sqlForEvent.lastIndexOf("AND"), sqlForEvent.length());
            }

            if (sqlForEvent.toString().trim().endsWith("or")) {
                sqlForEvent.delete(sqlForEvent.lastIndexOf("or"), sqlForEvent.length());
            }


            if (sqlForUser.toString().trim().endsWith("or")) {
                sqlForUser.delete(sqlForUser.lastIndexOf("or"), sqlForUser.length());
            }
            sqlForUser.append(")");
            if (sqlForUser.toString().trim().endsWith("AND )")) {
                sqlForUser.delete(sqlForUser.lastIndexOf("AND )"), sqlForUser.length());
            }

            if (sqlForEvent.toString().endsWith("and ( ")) {
                sqlForEvent.delete(sqlForEvent.lastIndexOf("and ( "), sqlForEvent.length());
            } else {
                sqlForEvent.append(")");
            }
        }
    }

    /**
     * 拼凑where条件
     *
     * @param sqlWhereForEvent sql事件属性字符串
     * @param sqlWhereForUser  sql用户属性字符串
     * @param conditions       过滤条件
     * @param relation         关系 and 或者 or
     */
    public static void pieceSqlWhereOr(StringBuilder sqlWhereForEvent, StringBuilder sqlWhereForUser, List<String> groupidList,
                                       List<String> propList, JSONArray conditions, String relation, String productId, List<String> whereEventFields, List<String> whereUserFields) {

        if (null == conditions || conditions.isEmpty()) {
            return;
        }

        final int size = conditions.size();
        for (int i = 0; i < size; i++) {
            JSONObject condition = conditions.getJSONObject(i);
            String type = condition.getString("type");
            String function = condition.getString(FUNCTION);
            String fieldType = condition.getString("fieldType");
            JSONArray params = condition.getJSONArray(PARAMS);
            String isNumber = condition.getString("isNumber");
            String isRegion = condition.getString("isRegion");
            if (params.size() == 0 && "isTrue".equals(isNumber) && "isFalse".equals(isRegion)) {
                String inputForint = condition.getString("inputForInt");
                params.add(inputForint);
            }
            if (params.size() == 0 && "isTrue".equals(isNumber) && "isTrue".equals(isRegion)) {
                String param1 = condition.getString("param1");
                String param2 = condition.getString("param2");
                params.add(param1);
                params.add(param2);
            }

            if (type.startsWith("event.")) {//事件属性
                // judgeSymbol(sqlWhereForEvent, type.substring(6), function, params, fieldType);
                String tableType = "e1.";
                judgeSymbolOr(sqlWhereForEvent, type.substring(6), function, params, fieldType, tableType);
                if (sqlWhereForEvent.toString().trim().endsWith("and")) {
                    sqlWhereForEvent.delete(sqlWhereForEvent.lastIndexOf("and"), sqlWhereForEvent.length());
                }
                sqlWhereForEvent.append(" or ");
                if (!whereEventFields.contains(type.substring(6))) {
                    whereEventFields.add(type.substring(6));
                }
            } else if (type.startsWith("user.")) {//用户属性
                if (sqlWhereForUser.toString().trim().endsWith("AND")) {
                    sqlWhereForUser.append("( (");
                } else {
                    sqlWhereForUser.append("( ");
                }
                String tableType = "e2.";
                judgeUserSymbolForOr(sqlWhereForUser, type.substring(5), function, params, fieldType, isNumber, isRegion, tableType);

                if (sqlWhereForUser.toString().trim().endsWith("and")) {
                    sqlWhereForUser.delete(sqlWhereForUser.lastIndexOf("and"), sqlWhereForUser.length());
                }
                sqlWhereForUser.append(" )");
                sqlWhereForUser.append(" or ");
                if (!propList.contains(type.substring(5))) {
                    propList.add(type.substring(5));
                }

                if (!whereUserFields.contains(type.substring(5))) {
                    whereUserFields.add(type.substring(5));
                }

            } else if (type.startsWith("userGroup.")) {//用户分群
                if (sqlWhereForUser.toString().trim().endsWith("AND")) {
                    sqlWhereForUser.append("( (");
                } else {
                    sqlWhereForUser.append("( ");
                }
                String tableType = "e2.";
                judgeSymbolUserGroupOr(sqlWhereForUser, type.substring(10), function, params, fieldType, tableType, productId);
                if (sqlWhereForUser.toString().trim().endsWith("and")) {
                    sqlWhereForUser.delete(sqlWhereForUser.lastIndexOf("and"), sqlWhereForUser.length());
                }
                sqlWhereForUser.append(" )");
                sqlWhereForUser.append(" or ");
                if (!groupidList.contains(productId + "_" + type.substring(10))) {
                    groupidList.add(productId + "_" + type.substring(10));
                }

                if (!whereUserFields.contains(type.substring(10))) {
                    whereUserFields.add(productId + "_" + type.substring(10));
                }
            }
        }
        if (sqlWhereForEvent.toString().trim().endsWith("AND")) {
            sqlWhereForEvent.delete(sqlWhereForEvent.lastIndexOf("AND"), sqlWhereForEvent.length());
        }

        if (sqlWhereForEvent.toString().trim().endsWith("or")) {
            sqlWhereForEvent.delete(sqlWhereForEvent.lastIndexOf("or"), sqlWhereForEvent.length());
        }


        if (sqlWhereForUser.toString().trim().endsWith("or")) {
            sqlWhereForUser.delete(sqlWhereForUser.lastIndexOf("or"), sqlWhereForUser.length());
        }
        sqlWhereForUser.append(")");
        if (sqlWhereForUser.toString().trim().endsWith("AND )")) {
            sqlWhereForUser.delete(sqlWhereForUser.lastIndexOf("AND )"), sqlWhereForUser.length());
        }

        if (sqlWhereForEvent.toString().endsWith("and ( ")) {
            sqlWhereForEvent.delete(sqlWhereForEvent.lastIndexOf("and ( "), sqlWhereForEvent.length());
        } else {
            sqlWhereForEvent.append(")");
        }
    }


    /**
     * 拼凑字段
     *
     * @param sqlBuilder sql字符串
     * @param fields     字段，即分组
     * @param actions    事件
     * @param unit       聚合单位
     */
    public static void pieceActionField(StringBuilder sqlBuilder, JSONArray fields, JSONArray actions, String unit) {
        sqlBuilder.append("select ");
        for (int i = 0; i < fields.size(); i++) {
            if ("all".equals(fields.getString(i))) {
                continue;
            }
            sqlBuilder.append(fields.getString(i)).append(",");
        }


        if (StringUtils.isNotEmpty(unit)) {
            sqlBuilder.append(unit).append(",");
        }

        // 如果多个事件要改造，现在只支持单个事件
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            String eventOriginal = action.getString("eventOriginal");
            String eventType = action.getString("eventType");

            // 分别为总次数、触发用户数、登录用户数
            switch (eventType) {
                case "acc":
                    sqlBuilder.append("count(action) from parquetTmpTable where category='");
                    break;
                case "userid":
                    sqlBuilder.append("count(distinct deviceid) from parquetTmpTable where category='");
                    break;
                case "loginUser":
                    sqlBuilder.append("count(distinct userid) from parquetTmpTable where category='");
                    break;
                default:
                    break;
            }

            String category = getCategory(eventOriginal);
            sqlBuilder.append(category).append("' and action='").append(eventOriginal).append("'");
        }
    }

    /**
     * 拼凑字段
     *
     * @param sqlBuilder sql字符串
     * @param fields     字段，即分组
     * @param actions    事件
     * @param unit       聚合单位
     */
    public static void pieceOnlyActionField(StringBuilder sqlBuilder, JSONArray fields, JSONArray actions, String unit) {
        sqlBuilder.append("select ");

        for (int i = 0; i < fields.size(); i++) {
            if ("all".equals(fields.getString(i))) {
                continue;
            }
            sqlBuilder.append(fields.getString(i).substring(6)).append(",");
        }

        if (StringUtils.isNotEmpty(unit)) {
            sqlBuilder.append(unit).append(",");
        }

        // 如果多个事件要改造，现在只支持单个事件
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            String eventOriginal = action.getString("eventOriginal");
            String eventType = action.getString("eventType");

            // 分别为总次数、触发用户数、登录用户数
            switch (eventType) {
                case "acc":
                    sqlBuilder.append("count(action) from parquetTmpTable where category='");
                    break;
                case "userid":
                    sqlBuilder.append("count(distinct deviceid) from parquetTmpTable where category='");
                    break;
                case "loginUser":
                    sqlBuilder.append("count(distinct userid) from parquetTmpTable where category='");
                    break;
                default:
                    break;
            }

            String category = getCategory(eventOriginal);
            sqlBuilder.append(category).append("' and action='").append(eventOriginal).append("'");
        }
    }

    /**
     * 根据eventOriginal获取category
     *
     * @param eventOriginal 事件
     * @return 结果
     * // SDK默认占用的actionId,包含
     * // $launch、$exitPage、$appClick、$crash、$error
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
     * // eventType：指标类型，类型和起对应指标计算方式：
     * // "acc" :COUNT(action)
     * // userid:COUNT(DISTINCT deviceid)
     * // loginUser ：COUNT(DISTINCT userid)
     *
     * @param eventType
     * @return
     * 查询的指标,
     */
    public static Tuple2<String, String> getIndicatorType(String eventType) {
        String indicatorType = "";
        String event = "";
        switch (eventType) {
            case "acc":
                indicatorType = "COUNT(1) AS ct";
                event = "1";
                break;
            case "userid":
                indicatorType = "COUNT(DISTINCT deviceid)  AS ct";
                event = "deviceid";
                break;
            case "loginUser":
                indicatorType = "COUNT(DISTINCT userid)  AS ct";
                event = "userid";
                break;
            default:
                break;
        }
        return new Tuple2<>(indicatorType, event);
    }

    /**
     * 拼凑where条件
     *
     * @param sqlBuilder sql字符串
     * @param conditions 过滤条件
     * @param relation   关系 and 或者 or
     */
    public static void pieceActionWhere(StringBuilder sqlBuilder, JSONArray conditions, String relation) {

        if (null == conditions || conditions.isEmpty()) {
            return;
        }

        final int size = conditions.size();
        if ("and".equals(relation)) {
            for (int i = 0; i < size; i++) {
                if (!sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.append(AND);
                }
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                JSONArray params = condition.getJSONArray(PARAMS);
                String tableType = "";
                judgeSymbol(sqlBuilder, type, function, params, fieldType, tableType);
            }
        } else if ("or".equals(relation)) {
            if (!sqlBuilder.toString().trim().endsWith("and")) {
                sqlBuilder.append(AND).append("( ");
            } else {
                sqlBuilder.append("( ");
            }
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                JSONArray params = condition.getJSONArray(PARAMS);
                String tableType = "";
                judgeSymbol(sqlBuilder, type, function, params, fieldType, tableType);
                if (sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
                }
                sqlBuilder.append(" or ");
            }
            sqlBuilder.delete(sqlBuilder.lastIndexOf("or"), sqlBuilder.length());
            sqlBuilder.append(")");
        }
    }

    /**
     * 拼凑where条件
     *
     * @param sqlBuilder sql字符串
     * @param conditions 过滤条件
     * @param relation   关系 and 或者 or
     */
    public static void pieceOnlyActionWhere(StringBuilder sqlBuilder, JSONArray conditions, String relation) {

        if (null == conditions || conditions.isEmpty()) {
            return;
        }

        final int size = conditions.size();
        if ("and".equals(relation)) {
            for (int i = 0; i < size; i++) {
                if (!sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.append(AND);
                }
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                String isNumber = condition.getString("isNumber");
                String isRegion = condition.getString("isRegion");
                JSONArray params = condition.getJSONArray(PARAMS);
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isFalse".equals(isRegion)) {
                    String inputForint = condition.getString("inputForInt");
                    params.add(inputForint);
                }
                if (params.size() == 0 && "isTrue".equals(isNumber) && "isTrue".equals(isRegion)) {
                    String param1 = condition.getString("param1");
                    String param2 = condition.getString("param2");
                    params.add(param1);
                    params.add(param2);
                }

                String tableType = "";
                judgeSymbol(sqlBuilder, type.substring(6), function, params, fieldType, tableType);
            }
        } else if ("or".equals(relation)) {

            if (!sqlBuilder.toString().trim().endsWith("and")) {
                sqlBuilder.append(AND).append("( ");
            } else {
                sqlBuilder.append("( ");
            }
            for (int i = 0; i < size; i++) {
                JSONObject condition = conditions.getJSONObject(i);
                String type = condition.getString("type");
                String function = condition.getString(FUNCTION);
                String fieldType = condition.getString("fieldType");
                JSONArray params = condition.getJSONArray(PARAMS);
                String tableType = "";
                judgeSymbol(sqlBuilder, type.substring(6), function, params, fieldType, tableType);
                if (sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
                }
                sqlBuilder.append(" or ");
            }
            sqlBuilder.delete(sqlBuilder.lastIndexOf("or"), sqlBuilder.length());
            if (sqlBuilder.toString().trim().endsWith("and")) {
                sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
            }
            sqlBuilder.append(")");
        }
    }

    /**
     * 判断用户属性符号
     *
     * @param sqlBuilder sql
     * @param type       用户类型
     * @param function   符号
     * @param params     参数hhhh
     */
    private static void judgeUserSymbol(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String isNumber, String isRegion, String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhere(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "notEqual":
                pieceNotEqualWhere(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "contain":
                pieceContainWhere(sqlBuilder, type, params);
                break;
            case "notContain":
                pieceNotContainWhere(sqlBuilder, type, params);
                break;
            case "isTrue":
                pieceIsTrueWhere(sqlBuilder, type);
                break;
            case "isFalse":
                pieceIsFalseWhere(sqlBuilder, type);
                break;
            case "more":
                pieceMoreWhere(sqlBuilder, type, params);
                break;
            case "less":
                pieceLessWhere(sqlBuilder, type, params);
                break;
            case "region":
                pieceRegionWhere(sqlBuilder, type, params);
                break;
            default:
                break;
        }
    }


    /**
     * 判断用户属性符号 用and 连接
     *
     * @param sqlBuilder sql
     * @param type       用户类型
     * @param function   符号
     * @param params     参数hhhh
     */
    private static void judgeUserSymbolForAnd(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String isNumber, String isRegion, String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhereForAnd(sqlBuilder, type, params, fieldType, isNumber, tableType);
                break;
            case "notEqual":
                pieceNotEqualWhereForAnd(sqlBuilder, type, params, fieldType, isNumber);
                break;
            case "contain":
                pieceContainWhere(sqlBuilder, type, params);
                break;
            case "notContain":
                pieceNotContainWhere(sqlBuilder, type, params);
                break;
            case "isTrue":
                pieceIsTrueWhere(sqlBuilder, type);
                break;
            case "isFalse":
                pieceIsFalseWhere(sqlBuilder, type);
                break;
            case "more":
                pieceMoreWhere(sqlBuilder, type, params);
                break;
            case "less":
                pieceLessWhere(sqlBuilder, type, params);
                break;
            case "region":
                pieceRegionWhere(sqlBuilder, type, params);
                break;
            default:
                break;
        }
    }


    /**
     * 判断符号
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeSymbol(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhere(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "notEqual":
                pieceNotEqualWhere(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "contain":
                pieceContainWhere(sqlBuilder, type, params);
                break;
            case "notContain":
                pieceNotContainWhere(sqlBuilder, type, params);
                break;
            case "isTrue":
                pieceIsTrueWhere(sqlBuilder, type);
                break;
            case "isFalse":
                pieceIsFalseWhere(sqlBuilder, type);
                break;
            case "more":
                pieceMoreWhere(sqlBuilder, type, params);
                break;
            case "less":
                pieceLessWhere(sqlBuilder, type, params);
                break;
            case "region":
                pieceRegionWhere(sqlBuilder, type, params);
                break;
            default:
                break;
        }
    }

    /**
     * 判断符号
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeSymbolUserGroupOr(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String tableType, String productId) {
        switch (function) {

            case "isTrue":
                pieceIsTrueUserGroupOr(sqlBuilder, type, tableType, productId);
                break;
            case "isFalse":
                pieceIsFalseUserGroupOr(sqlBuilder, type, tableType, productId);
                break;

            default:
                break;
        }
    }

    /**
     * 判断符号
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeSymbolOr(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhereOr(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "notEqual":
                pieceNotEqualWhereOr(sqlBuilder, type, params, fieldType, tableType);
                break;
            case "contain":
                pieceContainWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "notContain":
                pieceNotContainWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "isTrue":
                pieceIsTrueWhereOr(sqlBuilder, type, tableType);
                break;
            case "isFalse":
                pieceIsFalseWhereOr(sqlBuilder, type, tableType);
                break;
            case "more":
                pieceMoreWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "less":
                pieceLessWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "region":
                pieceRegionWhereOr(sqlBuilder, type, params, tableType);
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
            sqlBuilder.append("((").append(type).append(" is not null ) and (");
            likeStr.append(type).append(LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(" or ").append(type).append(LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append("))");
        } else {
            sqlBuilder.append("((").append(type).append(" is not null ) and (").append(type).append(LIKE).append(param).append("%'))");
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
            sqlBuilder.append("((").append(type).append(" is not null ) and (");
            likeStr.append(type).append(NOT_LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(AND).append(type).append(NOT_LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append(")");
        } else {
            sqlBuilder.append("((").append(type).append(" is not null ) and (").append(type).append(NOT_LIKE).append(param).append("%'))");
        }
    }

    /**
     * 拼凑不等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotEqualWhere(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String tableType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append("(").append(type).append(" not in ").append(in).append(")");
    }


    /**
     * 拼凑不等于
     *
     * @param sqlBuilder sql 用于and 拼接用户属性
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotEqualWhereForAnd(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String isNumber) {
        StringBuilder andstr = new StringBuilder();
        pieceParamsForNoAnd(params, andstr, fieldType, type, isNumber);
        if (andstr.toString().endsWith("and ")) {
            andstr.delete(andstr.lastIndexOf("and "), andstr.length());
        }
        sqlBuilder.append("(").append(type).append(" is not null ) AND (").append(andstr).append(")");
    }


    /**
     * 拼凑不等于
     *
     * @param sqlBuilder sql 用于and 拼接用户属性
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotEqualWhereForOr(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String isNumber, String tableType) {
        StringBuilder andstr = new StringBuilder();
        pieceParamsForNoAnd(params, andstr, fieldType, type, isNumber);
        if (andstr.toString().endsWith("and ")) {
            andstr.delete(andstr.lastIndexOf("and "), andstr.length());
        }
        sqlBuilder.append("(").append(tableType).append(type).append(" is not null ) AND (").append(tableType).append(andstr).append(")");
    }


    /**
     * 拼凑等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceEqualWhere(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String tableType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append(tableType).append(type).append(" in ").append(in);
    }

    /**
     * 拼凑等于 用and连接用户属性
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceEqualWhereForAnd(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String isNumber, String tableType) {
        StringBuilder andstr = new StringBuilder();
        pieceParamsForAnd(params, andstr, fieldType, type, isNumber, tableType);
        if (andstr.toString().endsWith("or ")) {
            andstr.delete(andstr.lastIndexOf("or "), andstr.length());
        }
        sqlBuilder.append("(").append(type).append(" is not null ) AND (").append(andstr).append(")");
    }


    /**
     * 拼凑大于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceMoreWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(" is not null ) AND (").append(type).append(">").append(Long.valueOf(params.getString(0))).append("))");
    }

    /**
     * 拼凑小于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceLessWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(" is not null ) AND (").append(type).append("<").append(Long.valueOf(params.getString(0))).append("))");
    }

    /**
     * 拼凑区间
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceRegionWhere(StringBuilder sqlBuilder, String type, JSONArray params) {
        sqlBuilder.append("((").append(type).append(" is not null ) AND ( ").append(type).append(">=").append(Long.valueOf(params.getString(0))).append(") AND");
        sqlBuilder.append("(").append(type).append("<=").append(Long.valueOf(params.getString(1))).append("))");
    }


    /**
     * 拼凑为真
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsTrueWhere(StringBuilder sqlBuilder, String type) {
        if (type.startsWith("event.")) {
            sqlBuilder.append("((").append(type.substring(6)).append(" is not null ) AND (").append(type.substring(6)).append(" = true ").append("))").append(" and ");
        } else {
            sqlBuilder.append("((").append(type).append(" is not null ) AND (").append(type).append(" = true ").append("))").append(" and ");
        }
    }

    /**
     * 拼凑为假
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsFalseWhere(StringBuilder sqlBuilder, String type) {
        sqlBuilder.append("((").append(type).append(" is not null ) AND (").append(type).append(" = false ").append("))").append(" and ");
    }

    /**
     * 拼凑查询的某个筛选条件的值是多个的，如苹果手机
     *
     * @param params 参数
     * @param in     拼凑的sql
     */
    private static void pieceParams(JSONArray params, StringBuilder in, String fieldType) {
        if (StringUtils.isEmpty(fieldType)) {
            dealParamForString(params, in);
        } else {
            switch (fieldType) {
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
     * 拼凑查询的某个筛选条件的值是多个的，如苹果手机
     *
     * @param params 参数
     * @param in     拼凑的sql
     */
    private static void pieceParamsForAnd(JSONArray params, StringBuilder in, String fieldType, String type, String isNumber, String tableType) {
        if ("isTrue".equals(isNumber)) {
            dealParamForIntegerAnd(params, in, type, tableType);
        } else if (StringUtils.isEmpty(fieldType)) {
            dealParamForStringAnd(params, in, type, tableType);
        } else {
            switch (fieldType) {
                case "int":
                    dealParamForIntegerAnd(params, in, type, tableType);
                    break;
                case "double":
                    dealParamForIntegerAnd(params, in, type, tableType);
                    break;
                default:
                    dealParamForStringAnd(params, in, type, tableType);
                    break;
            }
        }
    }


    /**
     * 拼凑查询的某个筛选条件的值是多个的，如苹果手机
     *
     * @param params 参数
     * @param in     拼凑的sql
     */
    private static void pieceParamsForNoAnd(JSONArray params, StringBuilder in, String fieldType, String type, String isNumber) {
        if ("isTrue".equals(isNumber)) {
            dealParamForIntegerNoAnd(params, in, type);
        } else if (StringUtils.isEmpty(fieldType)) {
            dealParamForStringNoAnd(params, in, type);
        } else {
            switch (fieldType) {
                case "int":
                    dealParamForIntegerNoAnd(params, in, type);
                    break;
                case "double":
                    dealParamForIntegerNoAnd(params, in, type);
                    break;
                default:
                    dealParamForStringNoAnd(params, in, type);
                    break;
            }
        }
    }


    /**
     * 处理参数为字符数据
     *
     * @param params 参数
     * @param in     结果
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
     * 处理参数为字符数据 用于and拼接 用户属性
     *
     * @param params 参数
     * @param params 类型
     * @param andstr 结果
     */
    private static void dealParamForStringAnd(JSONArray params, StringBuilder andstr, String type, String tableType) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            // 处理一对多的关系，如苹果手机
            if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
                String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
                for (String string : strings) {
                    andstr.append(tableType).append(type).append(" = '").append(string).append("' or ");
                }
            } else {
                andstr.append(tableType).append(type).append(" = '").append(param).append("' or ");
            }
        }
    }

    /**
     * 处理参数为整型数据   用于and拼接用户属性
     *
     * @param params 参数
     * @param andstr 结果
     */
    private static void dealParamForIntegerAnd(JSONArray params, StringBuilder andstr, String type, String tableType) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            andstr.append(tableType).append(type).append(" = ").append(param).append(" or ");
        }
    }


    /**
     * 处理参数为整型数据
     *
     * @param params 参数
     * @param in     结果
     */
    private static void dealParamForInteger(JSONArray params, StringBuilder in) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            in.append(param).append(",");
        }
    }

    /**
     * 拼凑分组
     *
     * @param sqlBuilder sql字符串
     * @param fields     字段，即分组
     * @param unit       聚合单位
     */
    public static void pieceActionGroupBy(StringBuilder sqlBuilder, JSONArray fields, String unit) {

        if (StringUtils.isNotBlank(unit)) {
            sqlBuilder.append(" group by ");
            for (int i = 0; i < fields.size(); i++) {
                if ("all".equals(fields.getString(i))) {
                    continue;
                }
                sqlBuilder.append(fields.getString(i).substring(6)).append(",");
            }
            sqlBuilder.append(unit);
        } else {
            sqlBuilder.append(" group by ");
            for (int i = 0; i < fields.size(); i++) {
                if ("all".equals(fields.getString(i))) {
                    continue;
                }
                sqlBuilder.append(fields.getString(i).substring(6)).append(",");
            }

            if (sqlBuilder.toString().trim().endsWith(",")) {
                sqlBuilder.delete(sqlBuilder.lastIndexOf(","), sqlBuilder.length());
            }

            if (sqlBuilder.toString().trim().endsWith("group by")) {
                sqlBuilder.delete(sqlBuilder.lastIndexOf("group by"), sqlBuilder.length());
            }

        }

    }

    /**
     * 拼凑分组
     *
     * @param sqlBuilder sql字符串
     * @param fields     字段，即分组
     */
    public static void pieceActionTaskGroupBy(StringBuilder sqlBuilder, JSONArray fields) {

        if (fields.size() != 1 || !"all".equals(fields.getString(0))) {
            sqlBuilder.append(" group by ");
            for (int i = 0; i < fields.size(); i++) {
                if ("all".equals(fields.getString(i))) {
                    continue;
                }
                sqlBuilder.append(fields.getString(i)).append(",");
            }

            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
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
                String tableType = "";
                judgeSymbol(sqlBuilder, type, function, params, fieldType, tableType);
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
                String tableType = "";
                judgeSymbol(sqlBuilder, type, function, params, fieldType, tableType);
                if (sqlBuilder.toString().trim().endsWith("and")) {
                    sqlBuilder.delete(sqlBuilder.lastIndexOf("and"), sqlBuilder.length());
                }
                sqlBuilder.append(" or ");
            }
            sqlBuilder.delete(sqlBuilder.lastIndexOf("or"), sqlBuilder.length());
        }
    }

    /**
     * 处理参数为字符数据 用于and拼接
     *
     * @param params 参数
     * @param params 类型
     * @param andstr 结果
     */
    private static void dealParamForStringNoAnd(JSONArray params, StringBuilder andstr, String type) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            // 处理一对多的关系，如苹果手机
            if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
                String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
                for (String string : strings) {
                    andstr.append(type).append(" <> '").append(string).append("' and ");
                }
            } else {
                andstr.append(type).append(" <> '").append(param).append("' and ");
            }
        }
    }

    /**
     * 处理参数为整型数据   用于and拼接
     *
     * @param params 参数
     * @param andstr 结果
     */
    private static void dealParamForIntegerNoAnd(JSONArray params, StringBuilder andstr, String type) {
        for (int i = 0; i < params.size(); i++) {
            String param = params.getString(i);
            andstr.append(type).append(" <> ").append(param).append(" and ");
        }
    }

    /**
     * 拼凑为真
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsTrueWhereForUserGroup(StringBuilder sqlBuilder, String type, String productId) {
        sqlBuilder.append("((").append(productId).append("_").append(type).append(" is not null ) AND (").append(productId).append("_").append(type).append(" = true ").append("))").append(" and ");
    }

    /**
     * 拼凑为假
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsFalseWhereForUserGroup(StringBuilder sqlBuilder, String type, String productId) {
        sqlBuilder.append("((").append(productId).append("_").append(type).append(" is not null ) AND (").append(productId).append("_").append(type).append(" = false ").append("))").append(" and ");
    }


    /**
     * 判断符号
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeSymbolForUserGroup(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String productId) {
        switch (function) {

            case "isTrue":
                pieceIsTrueWhereForUserGroup(sqlBuilder, type, productId);
                break;
            case "isFalse":
                pieceIsFalseWhereForUserGroup(sqlBuilder, type, productId);
                break;
            default:
                break;
        }
    }

    /**
     * 拼凑等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceEqualWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String tableType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append(tableType).append(type).append(" in ").append(in);
    }


    /**
     * 拼凑不等于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotEqualWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String tableType) {
        StringBuilder in = new StringBuilder();
        in.append("(");
        pieceParams(params, in, fieldType);
        in.delete(in.length() - 1, in.length());
        in.append(")");
        sqlBuilder.append("(").append(tableType).append(type).append(" not in ").append(in).append(")");
    }

    /**
     * 拼凑包含条件
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceContainWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String tableType) {
        final String param = params.getString(0);

        // 针对类似苹果手机一对多关系的处理
        if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
            String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
            StringBuilder likeStr = new StringBuilder();
            sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) and (");
            likeStr.append(tableType).append(type).append(LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(" or ").append(tableType).append(type).append(LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append("))");
        } else {
            sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) and (").append(tableType).append(type).append(LIKE).append(param).append("%'))");
        }
    }

    /**
     * 拼凑不包含条件
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceNotContainWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String tableType) {
        final String param = params.getString(0);

        // 针对类似苹果手机一对多关系的处理
        if (param.contains(Constants.ACTION_RESULT_SEPARATOR)) {
            String[] strings = param.split(Constants.ACTION_RESULT_SEPARATOR);
            StringBuilder likeStr = new StringBuilder();
            sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) and (");
            likeStr.append(tableType).append(type).append(NOT_LIKE).append(strings[0]).append("%'");
            for (int i = 1; i < strings.length; i++) {
                likeStr.append(AND).append(tableType).append(type).append(NOT_LIKE).append(strings[i]).append("%'");
            }
            sqlBuilder.append(likeStr).append("))");
        } else {
            sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) and (").append(tableType).append(type).append(NOT_LIKE).append(param).append("%'))");
        }
    }


    /**
     * 拼凑等于 用and连接用户属性
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceEqualWhereForAndOr(StringBuilder sqlBuilder, String type, JSONArray params, String fieldType, String isNumber, String tableType) {
        StringBuilder andstr = new StringBuilder();
        pieceParamsForAnd(params, andstr, fieldType, type, isNumber, tableType);
        if (andstr.toString().endsWith("or ")) {
            andstr.delete(andstr.lastIndexOf("or "), andstr.length());
        }
        sqlBuilder.append("(").append(tableType).append(type).append(" is not null ) AND (").append(andstr).append(")");
    }


    /**
     * 拼凑大于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceMoreWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String tableType) {
        sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) AND (").append(tableType).append(type).append(">").append(Long.valueOf(params.getString(0))).append("))");
    }

    /**
     * 拼凑小于
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceLessWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String tableType) {
        sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) AND (").append(tableType).append(type).append("<").append(Long.valueOf(params.getString(0))).append("))");
    }

    /**
     * 拼凑区间
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     * @param params     参数
     */
    private static void pieceRegionWhereOr(StringBuilder sqlBuilder, String type, JSONArray params, String tableType) {
        sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) AND ( ").append(tableType).append(type).append(">=").append(Long.valueOf(params.getString(0))).append(") AND");
        sqlBuilder.append("(").append(tableType).append(type).append("<=").append(Long.valueOf(params.getString(1))).append("))");
    }


    /**
     * 拼凑为真
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsTrueWhereOr(StringBuilder sqlBuilder, String type, String tableType) {
        sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) AND (").append(tableType).append(type).append(" = true ").append("))").append(" and ");
    }

    /**
     * 拼凑为假
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsFalseWhereOr(StringBuilder sqlBuilder, String type, String tableType) {
        sqlBuilder.append("((").append(tableType).append(type).append(" is not null ) AND (").append(tableType).append(type).append(" = false ").append("))").append(" and ");
    }

    /**
     * 拼凑为真
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsTrueUserGroupOr(StringBuilder sqlBuilder, String type, String tableType, String productId) {
        sqlBuilder.append("((").append(tableType).append(productId).append("_").append(type).append(" is not null ) AND (").append(tableType).append(productId).append("_").append(type).append(" = true ").append("))").append(" and ");
    }

    /**
     * 拼凑为假
     *
     * @param sqlBuilder sql
     * @param type       元数据类型
     */
    private static void pieceIsFalseUserGroupOr(StringBuilder sqlBuilder, String type, String tableType, String productId) {
        sqlBuilder.append("((").append(tableType).append(productId).append("_").append(type).append(" is not null ) AND (").append(tableType).append(productId).append("_").append(type).append(" = false ").append("))").append(" and ");
    }


    /**
     * 判断用户属性符号 用or 连接
     *
     * @param sqlBuilder sql
     * @param type       用户类型
     * @param function   符号
     * @param params     参数
     */
    private static void judgeUserSymbolForOr(StringBuilder sqlBuilder, String type, String function, JSONArray params, String fieldType, String isNumber, String isRegion, String tableType) {
        switch (function) {
            case "equal":
                pieceEqualWhereForAndOr(sqlBuilder, type, params, fieldType, isNumber, tableType);
                break;
            case "notEqual":
                pieceNotEqualWhereForOr(sqlBuilder, type, params, fieldType, isNumber, tableType);
                break;
            case "contain":
                pieceContainWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "notContain":
                pieceNotContainWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "isTrue":
                pieceIsTrueWhereOr(sqlBuilder, type, tableType);
                break;
            case "isFalse":
                pieceIsFalseWhereOr(sqlBuilder, type, tableType);
                break;
            case "more":
                pieceMoreWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "less":
                pieceLessWhereOr(sqlBuilder, type, params, tableType);
                break;
            case "region":
                pieceRegionWhereOr(sqlBuilder, type, params, tableType);
                break;
            default:
                break;
        }
    }


    /**
     * 判断分组中是否含有事件属性和用户属性
     *
     * @param fields       分组
     * @param isFieldEvent 是否是事件属性
     * @param isFieldUser  是否是用户属性
     */
    public static void handleFieldType(JSONArray fields, boolean isFieldEvent, boolean isFieldUser) {
        for (int j = 0; j < fields.size(); j++) {
            if ("all".equals(fields.getString(j))) {
                continue;
            }
            if (fields.getString(j).startsWith(Constants.EVENT_START)) {//事件属性
                isFieldEvent = true;
            } else if (fields.getString(j).startsWith(Constants.USER_START)) {//用户属性
                isFieldUser = true;
            } else if (fields.getString(j).startsWith(Constants.USERGROUP_START)) {//用户属性
                isFieldUser = true;
            }
        }
    }

    /**
     * 判断外部条件中是否含有事件属性和用户属性
     *
     * @param conditions 分组
     * @param outIsEvent 是否是事件属性
     * @param outItsUser 是否是用户属性
     */
    public static void handleWhereType(JSONArray conditions, boolean outIsEvent, boolean outItsUser) {
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject condition = conditions.getJSONObject(i);
            String type = condition.getString(Constants.TYPE);
            if (type.startsWith(Constants.EVENT_START)) {//事件属性
                outIsEvent = true;
            } else if (type.startsWith(Constants.USER_START)) {//用户属性
                outItsUser = true;
            } else if (type.startsWith(Constants.USERGROUP_START)) {//用户分群
                outItsUser = true;
            }
        }
    }

}
