package com.hochoy.cobub3_test;
public class Constants {

    private Constants() {}

    public static final int SCANCACHING = 20;
    public static final boolean CACHEBLOCKS = false;
    public static final String SEPARATOR = "\001";
    public static final String CODE = "UTF-8";
    public static final String CUSTOM_SEPARATOR = ".\\$";
    public static final String JOINER = ".$";
    public static final String TASK_DATE_RANGE_JOINER = "_";
    public static final String TASK_ACTION_TABLE_NAME = ":action_";
    public static final String REPORT_TYPE_ACTION = "事件分析";
    public static final String REPORT_TYPE_FUNNEL = "漏斗分析";
    public static final String REPORT_CHAR_TYPE_NUMBER = "number";
    public static final String SEPARATOR_U0001 = "\\u0001";
    public static final String ACTION_VALUE = "action_value";
    public static final String DATATYPE = "dataType";
    public static final String STRING = "string";
    public static final String METADATALIST = "metadataList";


    /**
     * 以下为事件分析
     */
    public static final String BY_FIELDS = "by_fields";
    public static final String BY_VALUES = "by_values";
    public static final String ACTION_REPORT_VALUE = "value";
    public static final String TOTAL_ROWS = "total_rows";
    public static final String NUM_ROWS = "num_rows";
    public static final String COUNT = "count";
    public static final String ACTION_RESULT_SEPARATOR = "#_#";
    public static final int FIELD_GROUP_COUNT = 10000;
    public static final String PRODUCTID = "productId";
    public static final String FROM_DATE = "from_date";
    public static final String TO_DATE = "to_date";
    public static final String FILTER = "filter";
    public static final String RELATION = "relation";
    public static final String CONDITIONS = "conditions";
    public static final String BY_FIELDVALUE = "by_fieldValue";
    public static final String ACTION = "action";
    public static final String UNIT = "unit";
    public static final String AND = " AND ";
    public static final String ANDS = "AND";
    public static final String OR = "or";
    public static final String REPORT_NAME = "report_name";
    public static final String EVENT_START = "event.";
    public static final String USER_START = "user.";
    public static final String USERGROUP_START = "userGroup.";
    public static final String CHILDFILTERPARAM = "childFilterParam";
    public static final String BOOLEAN = "boolean";
    public static final String ALL = "all";
    public static final String OTHER = "其他";
    public static final String NULL = "null";
    public static final String CN_TRUE = "真";
    public static final String TRUE = "true";
    public static final String EQUAL_TRUE = " = true ";
    public static final String EQUAL_FALSE = " = false ";
    public static final String IS_NOT_NULL_AND = " is not null ) AND (";
    public static final String REGION = "region";
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String ISTRUE = "isTrue";
    public static final String ISFALSE = "isFalse";
    public static final String LOGINUSER = "loginUser";
    public static final String USERID = "userid";
    public static final String ORDER_BY_USERID  = " order by userid ";




    /**
     * 以下为漏斗分析
     */
    public static final String ACTION_ID = "actionId";
    public static final String ELEMENT_ID = "elementId";

    /**
     * 以下为用户分群创建方式和更新方式
     */
    public static final String CREATE_TYPE_FILE = "文件导入";
    public static final String CREATE_TYPE_RESULT = "结果生成";
    public static final String MODIFY_TYPE_MODUAL = "手动更新";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String USERIDS = "userIds";
    public static final String GROUP_SCHEMA_ERR = "上传用户分群动态创建schema出错";
    public static final String USER_SCHEMA_ERR = "上传用户属性动态创建schema出错";
    /**
     * 导入用户属性
     */
    public static final int MAX_LINE_PER_BATCH = 500000;
    /**
     * 导入用户属性模板第一列title
     */
    public static final String USERID_COL_NAME = "userid";

    public static final String UNKNOWN = "unknown";

}
