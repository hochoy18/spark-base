package com.hochoy.cobub3_test;

/**
 * 用户属性
 */
public class UserProperty {

    /**
     * 属性名称
     */
    private String metaType;

    /**
     * 属性别名
     */
    private String display;

    /**
     * 属性数据类型
     */
    private String dataType;

    /**
     * 属性值是否保存
     */
    private Boolean isSavePropertyValue;

    public UserProperty(){}

    public UserProperty(String metaType, String display, String dataType, Boolean isSavePropertyValue) {
        this.metaType = metaType;
        this.display = display;
        this.dataType = dataType;
        this.isSavePropertyValue = isSavePropertyValue;
    }

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Boolean getSavePropertyValue() {
        return isSavePropertyValue;
    }

    public void setSavePropertyValue(Boolean savePropertyValue) {
        isSavePropertyValue = savePropertyValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "UserProperty{" +
                "metaType='" + metaType + '\'' +
                ", display='" + display + '\'' +
                ", dataType='" + dataType + '\'' +
                ", isSavePropertyValue=" + isSavePropertyValue +
                '}';
    }
}
