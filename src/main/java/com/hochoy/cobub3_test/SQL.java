package com.hochoy.cobub3_test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import scala.Tuple2;
import scala.Tuple4;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/6/17
 */
public class SQL {


    public static void main(String[] args) throws IOException {

        Long start = System.currentTimeMillis();
        new SQL().getQueryResult();
        Long end = System.currentTimeMillis();
        System.out.println((end - start));
    }

    /**
     * 根据 每一组的 查询条件 和 条件关系(adn/or) 拼接 每一组 action 表和user表的where 条件
     *
     * @param conditions 查询条件集合
     * @param relation   条件间的逻辑关系 ：and / or
     * @return 二元组：actionWhere,userWhere
     */
    Tuple2<StringJoiner, StringJoiner> queryConditionOp(JSONArray conditions, String relation) {

        StringJoiner actionWhere = new StringJoiner(String.format(" %s ", relation)); // action where 条件
        StringJoiner userWhere = new StringJoiner(String.format(" %s ", relation));
        conditions.forEach(v -> {
            JSONObject condition = JSONObject.parseObject(String.valueOf(v));
            String type = condition.getString("type");  //event.country, user.age, userGroup.benyueqianzaitouziyonghu
            String function = condition.getString("function");
            JSONArray params = condition.getJSONArray("params");
            String[] split = type.split("\\.", 2);
            String con;
            switch (split[0]) {
                case "event": {
//                    eventWhere = eventWhereFunctionOp(type, function, params, eventWhere);
                    switch (function) {
                        case "equal": { // string  and  Number
                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
                            params.forEach(x -> joiner.add(String.format("'%s'", x)));
                            con = String.format("(%s IN  %s)", split[1], joiner.toString());
                            actionWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  Number
                            StringJoiner joiner = new StringJoiner(", ", "(", ")");
                            params.forEach(x -> joiner.add(String.format("'%s'", x)));
                            con = String.format("(%s NOT IN  %s)", split[1], joiner.toString());
                            actionWhere.add(con);// IN  v.s NOT IN
                            break;
                        }
                        case "contain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", split[1], split[1], param));// LIKE  v.s    LIKE
                            break;
                        }
                        case "isTrue":
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", split[1], split[1]));
                            break;
                        case "isFalse":
                            actionWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", split[1], split[1]));
                            break;
                        case "notContain": { //string
                            String param = params.getString(0);
                            actionWhere.add(String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", split[1], split[1], param));// LIKE  v.s NOT LIKE
                            break;
                        }
                        case "more": {  // Number >
                            String param = params.getString(0);
                            con = String.format("(%s  >  %s)", split[1], param);
                            actionWhere.add(con);
                            break;
                        }
                        case "less": { // Number <
                            String param = params.getString(0);
                            con = String.format("(%s  <  %s)", split[1], param);
                            actionWhere.add(con);
                            break;
                        }
                        case "region": {  // Number  between ：  >= and <=
                            String param1 = params.getString(0);
                            String param2 = params.getString(0);
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", split[1], param1, split[1], param2);
                            actionWhere.add(con);
                            break;
                        }
                    }


                    break;
                }
                case "user": {
                    switch (function) {
                        // String:
                        case "equal": { // string  and todo Number
                            StringJoiner joiner = new StringJoiner(" or ", "(", ")");
                            params.forEach(x -> joiner.add(String.format("  %s = '%s'", split[1], x)));
                            con = String.format("( %s IS NOT NULL  AND  %s )", split[1], joiner.toString());
                            userWhere.add(con);
                            break;
                        }
                        case "notEqual": {  // string  and  todo Number
                            StringJoiner joiner = new StringJoiner(" AND ", "(", ")");  // and v.s and  or
                            params.forEach(x -> joiner.add(String.format("  %s <> '%s'", split[1], x)));
                            userWhere.add(String.format("((%s IS NOT NULL ) AND  %s )", split[1], joiner.toString()));
                            break;
                        }
                        case "contain": { //string like
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND ( %s LIKE  '%s') )", split[1], split[1], param);
                            userWhere.add(con);// LIKE  v.s    LIKE
                            break;
                        }
                        case "notContain": { //string
                            String param = params.getString(0);
                            con = String.format("((%s IS NOT NULL ) AND (%s NOT LIKE  '%s') )", split[1], split[1], param);
                            userWhere.add(con);//NOT LIKE    v.s NOT LIKE
                            break;
                        }
                        case "isTrue":
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = true)", split[1], split[1]));
                            break;
                        case "isFalse":
                            userWhere.add(String.format("(%s  IS NOT NULL AND %s = false)", split[1], split[1]));
                            break;
                        case "more": {  // Number >
                            String param = params.getString(0);
                            con = String.format("(%s  >  %s)", split[1], param);
                            userWhere.add(con);
                            break;
                        }
                        case "less": { // Number <
                            String param = params.getString(0);
                            con = String.format("(%s  <  %s)", split[1], param);
                            userWhere.add(con);
                            break;
                        }
                        case "region": {  // Number  between ：  >= and <=
                            String param1 = params.getString(0);
                            String param2 = params.getString(0);
                            con = String.format("(%s  >=  %s AND  %s  <=  %s)", split[1], param1, split[1], param2);
                            userWhere.add(con);
                            break;
                        }


                    }
                    break;
                }
                case "userGroup": {
                    String param = "true";//params.getString(0);
                    userWhere.add(String.format("((%s IS NOT NULL ) AND (%s = %s) )", split[1], split[1], param));// todo true or false op
                    break;
                }
            }


        });
        return new Tuple2<>(userWhere, actionWhere);

    }


    /**
     * 根据分组字段获取 usersTable 和 parquetTmpTable 的 group by 和 select 字段
     * todo 获取usersTable 的 schema
     *
     * @param by_field
     * @return
     */
    private Tuple4<StringJoiner, StringJoiner, StringJoiner, StringJoiner> byFieldOp(JSONArray by_field) {
        StringJoiner outGroupByUser = new StringJoiner(", "); // user group by 字段
        StringJoiner outGroupByAction = new StringJoiner(", "); //  action group by 字段
        StringJoiner outSelectFieldUser = new StringJoiner(", "); //  user select  字段
        StringJoiner outSelectFieldAction = new StringJoiner(", "); // action select  字段
        by_field.forEach(field -> {
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
                outSelectFieldUser.add(column);// select 字段拼接

                // todo 动态 schema 获取和拼接
            } else if ("userGroup".equals(type)) {
                //usersTable  表的用户分群属性处理
                outGroupByUser.add(column);// group by 字段拼接
                outSelectFieldUser.add(column);// select 字段拼接
                // todo 动态 schema 获取和拼接
            }

        });
        return new Tuple4<>(outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction);
    }


    public JSONObject getQueryResult() throws IOException {

        //inOr && (!outOr
//        String jsonMessage = "{\"action\":[{\"eventOriginal\":\"$appClick\",\"eventType\":\"userid\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.country\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"中国\",\"美国\"]},{\"type\":\"event.wifi\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"user.age\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"11\",\"22\"]},{\"type\":\"userGroup.fenqun27\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]}],\"relation\":\"or\"}},{\"eventOriginal\":\"$appClick\",\"eventType\":\"acc\",\"childFilterParam\":{\"conditions\":[{\"type\":\"user.utm_source\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"36kr\",\"今日头条\",\"地推\"]},{\"type\":\"userGroup.benyueqianzaitouziyonghu\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.pageTitle\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"HelloActivity\"]}],\"relation\":\"and\"}}],\"unit\":\"day\",\"filter\":{\"relation\":\"or\",\"conditions\":[{\"type\":\"user.channel\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"Baidu\",\"Offline\"]},{\"type\":\"userGroup.fenxijieguo8\",\"function\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[]},{\"type\":\"user.Province\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"北京市\",\"上海市\"]}]},\"by_fields\":[\"user.Province\",\"userGroup.fenxijieguo8\",\"event.country\"],\"from_date\":\"2019-05-20\",\"to_date\":\"2019-05-26\",\"productId\":\"11001\"}";

        //!inOr && outOr
//        String jsonMessage = "{\"action\":[{\"eventOriginal\":\"$appClick\",\"eventType\":\"userid\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.country\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"中国\",\"美国\"]},{\"type\":\"event.wifi\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.age\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"11\",\"22\"]},{\"type\":\"event.fenqun27\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]}],\"relation\":\"or\"}},{\"eventOriginal\":\"$appClick\",\"eventType\":\"acc\",\"childFilterParam\":{\"conditions\":[{\"type\":\"user.utm_source\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"36kr\",\"今日头条\",\"地推\"]},{\"type\":\"userGroup.benyueqianzaitouziyonghu\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.pageTitle\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"HelloActivity\"]}],\"relation\":\"and\"}}],\"unit\":\"day\",\"filter\":{\"relation\":\"or\",\"conditions\":[{\"type\":\"event.channel\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"Baidu\",\"Offline\"]},{\"type\":\"userGroup.fenxijieguo8\",\"function\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[]},{\"type\":\"user.Province\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"北京市\",\"上海市\"]}]},\"by_fields\":[\"user.Province\",\"userGroup.fenxijieguo8\",\"event.country\"],\"from_date\":\"2019-05-20\",\"to_date\":\"2019-05-26\",\"productId\":\"11001\"}";
        //inOr && outOr
        //String jsonMessage = "{ \"action\": [{ \"eventOriginal\": \"$appClick\", \"eventType\": \"userid\", \"childFilterParam\": { \"conditions\": [{ \"type\": \"event.country\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"中国\",\"美国\"] }, { \"type\": \"event.wifi\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }, { \"type\": \"event.age\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"11\",\"22\"] }, { \"type\": \"event.fenqun27\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }], \"relation\": \"or\" } }, { \"eventOriginal\": \"$appClick\", \"eventType\": \"userid\", \"childFilterParam\": { \"conditions\": [{ \"type\": \"event.country\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"中国\",\"美国\"] }, { \"type\": \"event.wifi\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }, { \"type\": \"user.age\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"11\",\"22\"] }, { \"type\": \"userGroup.fenqun27\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }], \"relation\": \"or\" } }, { \"eventOriginal\": \"$appClick\", \"eventType\": \"acc\", \"childFilterParam\": { \"conditions\": [{ \"type\": \"user.utm_source\", \"function\": \"equal\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"36kr\",\"今日头条\",\"地推\"] }, { \"type\": \"userGroup.benyueqianzaitouziyonghu\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [] }, { \"type\": \"event.pageTitle\", \"function\": \"isTrue\", \"isNumber\": \"isFalse\", \"inputForInt\": \"\", \"isRegion\": \"isFalse\", \"param1\": \"\", \"param2\": \"\", \"params\": [\"HelloActivity\"] }], \"relation\": \"and\" } }], \"unit\": \"day\", \"filter\": { \"relation\": \"or\", \"conditions\": [{ \"type\": \"event.channel\", \"function\": \"equal\", \"isRegion\": \"isFalse\", \"params\": [\"Baidu\",\"Offline\"] }, { \"type\": \"userGroup.fenxijieguo8\", \"function\": \"isFalse\", \"isRegion\": \"isFalse\", \"params\": [] }, { \"type\": \"user.Province\", \"function\": \"equal\", \"isRegion\": \"isFalse\", \"params\": [\"北京市\", \"上海市\"] }] }, \"by_fields\": [\"user.Province\",\"userGroup.fenxijieguo8\",\"event.country\"], \"from_date\": \"2019-05-20\", \"to_date\": \"2019-05-26\", \"productId\": \"11001\" }";

//        String jsonMessage = "{\"action\":[{\"eventOriginal\":\"$appClick\",\"eventType\":\"userid\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.country\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"中国\",\"美国\"]},{\"type\":\"event.wifi\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.age\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"11\",\"22\"]},{\"type\":\"user.fenqun27\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]}],\"relation\":\"and\"}},],\"unit\":\"day\",\"filter\":{\"relation\":\"or\",\"conditions\":[{\"type\":\"user.channel\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"Baidu\",\"Offline\"]},{\"type\":\"userGroup.fenxijieguo8\",\"function\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[]},{\"type\":\"user.Province\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"北京市\",\"上海市\"]}]},\"by_fields\":[\"user.Province\",\"userGroup.fenxijieguo8\",\"event.country\"],\"from_date\":\"2019-05-20\",\"to_date\":\"2019-05-26\",\"productId\":\"11001\"}";

        // Only action
        String jsonMessage = "{\"action\":[{\"eventOriginal\":\"$appClick\",\"eventType\":\"userid\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.country\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"中国\",\"美国\"]},{\"type\":\"event.wifi\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.age\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"11\",\"22\"]},{\"type\":\"event.fenqun27\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]}],\"relation\":\"or\"}},{\"eventOriginal\":\"$appClick\",\"eventType\":\"userid\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.country\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"中国\",\"美国\"]},{\"type\":\"event.wifi\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.age\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"11\",\"22\"]},{\"type\":\"event.fenqun27\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]}],\"relation\":\"or\"}},{\"eventOriginal\":\"$appClick\",\"eventType\":\"acc\",\"childFilterParam\":{\"conditions\":[{\"type\":\"event.utm_source\",\"function\":\"equal\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"36kr\",\"今日头条\",\"地推\"]},{\"type\":\"event.benyueqianzaitouziyonghu\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[]},{\"type\":\"event.pageTitle\",\"function\":\"isTrue\",\"isNumber\":\"isFalse\",\"inputForInt\":\"\",\"isRegion\":\"isFalse\",\"param1\":\"\",\"param2\":\"\",\"params\":[\"HelloActivity\"]}],\"relation\":\"and\"}}],\"unit\":\"day\",\"filter\":{\"relation\":\"or\",\"conditions\":[{\"type\":\"event.channel\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"Baidu\",\"Offline\"]},{\"type\":\"event.fenxijieguo8\",\"function\":\"isFalse\",\"isRegion\":\"isFalse\",\"params\":[]},{\"type\":\"event.Province\",\"function\":\"equal\",\"isRegion\":\"isFalse\",\"params\":[\"北京市\",\"上海市\"]}]},\"by_fields\":[\"event.Province\",\"event.fenxijieguo8\",\"event.country\"],\"from_date\":\"2019-05-20\",\"to_date\":\"2019-05-26\",\"productId\":\"11001\"}";

        JSONObject jsonObject = JSONObject.parseObject(jsonMessage);


        String productId = jsonObject.getString(Constants.PRODUCTID);
        String from = jsonObject.getString(Constants.FROM_DATE);
        String to = jsonObject.getString(Constants.TO_DATE);
        String unit = jsonObject.getString(Constants.UNIT);

        StringJoiner commWhere = new StringJoiner(" AND ");
//        String prdCon = String.format("( )", productId);
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
        Tuple4 selectAndGroupBy = byFieldOp(fields);


        /**
         * 外部查询 where 条件
         * return ( userWhere,actionWhere)
         */
        Tuple2<StringJoiner, StringJoiner> filter = queryConditionOp(conditions, relation);


        Map map = new HashMap();
        map.put("relation", relation);
        map.put("commWhere", commWhere);
        map.put("productId", productId);

        /**
         * 结合 {@link filter}、{@link selectAndGroupBy}  拼接 单指标 SQL
         */
        actionToSingleIndicatorSQLs(actions, selectAndGroupBy, filter, map);

        //sqlSplice(actions, filters);

        return null;
    }

    /**
     * @param actions          actions
     * @param selectAndGroupBy Tuple4(outGroupByUser, outGroupByAction, outSelectFieldUser, outSelectFieldAction)
     * @param filter           Tuple2 ( outUserWhere,outActionWhere)
     * @param map
     */
    void actionToSingleIndicatorSQLs(JSONArray actions,
                                     Tuple4 selectAndGroupBy,
                                     Tuple2 filter,
                                     Map map) {
        List<String> sqlList = new ArrayList<>();


        final StringJoiner outGroupByUser = (StringJoiner) selectAndGroupBy._1();
        final StringJoiner outGroupByAction = (StringJoiner) selectAndGroupBy._2();
        final StringJoiner outSelectFieldUser = (StringJoiner) selectAndGroupBy._3();
        final StringJoiner outSelectFieldAction = (StringJoiner) selectAndGroupBy._4();

        final StringJoiner outUserWhere = (StringJoiner) filter._1;
        final StringJoiner outActionWhere = (StringJoiner) filter._2;


        StringJoiner groupByJoiner = new StringJoiner(",");//
        if (!outGroupByAction.toString().isEmpty()) {
            groupByJoiner.add(outGroupByAction.toString());
        }
        if (!outGroupByUser.toString().isEmpty()) {
            groupByJoiner.add(outGroupByUser.toString());
        }

        String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action", "day");
        String parquetSQL = "SELECT  %s  FROM parquetTmpTable WHERE %s  GROUP BY %s";
        // actionSelect ,actionWhere , userSelect , userWhere  productId, joinWhere joinGroupBy(action ... )
        String joinSQL = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE   %s  ) tu " +
                "ON concat_ws('_', '%s', ta.global_user_id) = tu.pk where %s ";
        String partialAggSQLFormat = "select %s from %s group by %s"; // (groupBy + action + indicatorType) , joinSQL ,groupBy
        String joinSqlNoWhere = "(SELECT %s  FROM parquetTmpTable WHERE %s )  ta JOIN ( SELECT %s FROM usersTable WHERE %s ) tu ON concat_ws('_', '%s', ta.global_user_id) = tu.pk ";

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
            String groupBy = String.join(",", groupByJoiner.toString(), eventCol);
//            String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action");
            commWhere1.add(commWhere).add(String.format("(category = '%s')", category));
//            System.out.println("commWhere1----------------------------------:               " + commWhere1.toString());
            JSONObject childFilterParam = action.getJSONObject(Constants.CHILDFILTERPARAM);
            String relate = childFilterParam.getString("relation");
            JSONArray conditions = childFilterParam.getJSONArray("conditions");
            /**
             * (userWhere, actionWhere)
             */
            Tuple2<StringJoiner, StringJoiner> inCondition = queryConditionOp(conditions, relate);
            StringJoiner inUserWhere = inCondition._1;
            StringJoiner inActionWhere = inCondition._2;
            Boolean inOr = Constants.OR.equalsIgnoreCase(relate) && (!inActionWhere.toString().isEmpty()) && (!inUserWhere.toString().isEmpty());
//            Boolean outOr = !(outActionWhere.toString().isEmpty() || outUserWhere.toString().isEmpty());
            String singleSQL = "";


            // actionSelect : global_user_id, 分组字段，action，day
            String actionSelect = String.join(", ", "global_user_id", "action", "day", eventCol, outSelectFieldAction.toString());//todo outSelectFieldAction 为空
            String userSelect = String.join(", ", "pk", outSelectFieldUser.toString());//userSelect : pk, 分组字段 todo outSelectFieldUser 为空
            //joinselect : 分组字段，action，day, 指标 , 'A' AS an
            String joinSelect = String.join(", ", groupByJoiner.toString(), "action", "day", String.format("%s AS an", (char) (i.getAndAdd(1))), indicatorType);
//            String.join(",", joinSelect, indicatorType);
//            vvv= vvv+1;
            //String partialAggGroupBy = String.join(", ", groupByJoiner.toString(), "action","day");

            if (outGroupByUser.toString().isEmpty() && inUserWhere.toString().isEmpty() && outUserWhere.toString().isEmpty()) {
                //分组、内部条件、外部条件中只有事件属性，只查 parquet 表
//                String select = String.join(", ", "global_user_id", "action", outSelectFieldAction.toString());
//                String groupBy = String.join(", ", "action", outGroupByAction.toString()); // todo action
                String where = String.join(" AND ", outActionWhere.toString(), inActionWhere.toString());
                StringJoiner actionWhere = new StringJoiner(Constants.AND);
                actionWhere.add(commWhere1.toString());
                if (!outActionWhere.toString().isEmpty()) {
                    actionWhere.add(outActionWhere.toString());
                }
                if (!inActionWhere.toString().isEmpty()) {
                    actionWhere.add(inActionWhere.toString());

                }


//                String select = String.join(", ", "global_user_id", "action", outSelectFieldAction.toString());
                singleSQL = String.format(parquetSQL, actionSelect, actionWhere.toString(), partialAggGroupBy);
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


                    if (inOr && outOr) {

                        //outActionWhere,outUserWhere ,inActionWhere,inUserWhere 都不为空，且inOr,outOr 都是 OR
                        // (outActionWhere OR outUserWhere)  and (inActionWhere OR inUserWhere) 放到join where 后
                        // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接

                        String outWhere = new StringJoiner(Constants.OR, "(", ")").add(outActionWhere.toString()).add(outUserWhere.toString()).toString();
                        String inWhere = new StringJoiner(Constants.OR, "(", ")").add(inActionWhere.toString()).add(inUserWhere.toString()).toString();
                        String joinWhere = new StringJoiner(Constants.AND).add(outWhere).add(inWhere).toString();

                        userWhere1 = userCommWhere;
                        actionWhere1 = commWhere1.toString();
                        String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);

                    } else if (inOr && (!outOr)) {
                        // inWhere 条件   放到  usersTable join parquetTmpTable 后,
                        // outWhere 放到 对应 usersTable 和 parquetTmpTable 表 的where 后
//                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));
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
                        String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
//                        String partialAggSQLFormat = "select %s from %s group by %s";

                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);
//
//
                    } else if (!inOr && outOr) {
                        //1. outActionWhere  outUserWhere  都是非空 ，且 or 关系，放到join where 后
                        //2. inActionWhere inUserWhere 可能为空 或 为 and 关系
                        // outWhere 条件   放到  usersTable join parquetTmpTable 后
                        // ,inWhere 放到 对应的 usersTable 和 parquetTmpTable 表 的where 后
//                        System.out.println("inOr: " + inOr + " outOr:   " + outOr + "  " + (inOr && (!outOr)));
                        String joinWhere = String.join(Constants.OR, outActionWhere.toString(), outUserWhere.toString());
//                        String actionWhere = String.join(Constants.AND, commWhere1.toString());
                        if (!inActionWhere.toString().isEmpty()) {
                            actionWhere1 = actionWhere.join(relate, inActionWhere.toString());
                        }
//                        String userWhere = String.join(Constants.AND, userCommWhere);
                        if (!inUserWhere.toString().isEmpty()) {
                            userWhere1 = userWhere.join(relate, inUserWhere.toString());
                        }

                        String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
                        String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);


                    }
//                    String joinSQL1 = String.format(joinSQL, actionSelect, actionWhere1, userSelect, userWhere1, productId, joinWhere);
//                    String partialAggSQL = String.format(partialAggSQLFormat, String.join(",", joinSelect, indicatorType), joinSQL1, groupBy);
//                    System.out.println("partialAggSQL........................." + partialAggSQL);

                } else {
                    // 所有条件均分别下推到 userTable 和 parquetTmpTable 表的where  后
                    // join 后没有where

                    //
                    //
                    // inWhere 和 outWhere 条件 均 放到  usersTable join parquetTmpTable 后 ，且用and连接
                    // actionWhere: outActionWhere and inActionWhere -> todo 判断是否为空，
                    // userWhere ：outUserWhere and inUserWhere --> todo 判断是否为空
                    //
                    StringJoiner actionWhere = new StringJoiner(Constants.AND);
                    actionWhere.add(commWhere1.toString());
                    StringJoiner userWhere = new StringJoiner(Constants.AND);
                    userWhere.add(userCommWhere);
                    if (!outActionWhere.toString().isEmpty()) {
                        actionWhere.add(outActionWhere.toString());
                    }
                    if (!inActionWhere.toString().isEmpty()) {
                        actionWhere.add(inActionWhere.toString());
                    }
                    if (!outUserWhere.toString().isEmpty()) {
                        userWhere.add(outUserWhere.toString());
                    }
                    if (!inUserWhere.toString().isEmpty()) {
                        userWhere.add(inUserWhere.toString());
                    }
                    actionWhere.toString();
                    String joinSQL1 = String.format(joinSqlNoWhere, actionSelect, actionWhere.toString(), userSelect, userWhere.toString(), productId);
                    String partialAggSQL = String.format(partialAggSQLFormat, joinSelect, joinSQL1, partialAggGroupBy);


                }


            }
            sqlList.add(singleSQL);

        });


    }


}
