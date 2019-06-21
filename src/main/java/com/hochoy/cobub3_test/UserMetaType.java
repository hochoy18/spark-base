package com.hochoy.cobub3_test;

import java.util.Date;

/**
 * 用户属性类型
 */
public class UserMetaType {

    private Long typeid;

    private String type;

    private String datatype;

    private String display;

    private Short configurable;

    private Short enabled;

    private Short captureValues;

    private Date createtime;

    private Date lastUpdateTime;

    private String description;

    private Short enabledGroup;

    private Short enabledFilter;

    private Short isGroup;

    private Short isInsert;

    private Long productId;

    private Page page;

    public Long getTypeid() {
        return typeid;
    }

    public void setTypeid(Long typeid) {
        this.typeid = typeid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype == null ? null : datatype.trim();
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display == null ? null : display.trim();
    }


    public Short getConfigurable() {
        return configurable;
    }

    public void setConfigurable(Short configurable) {
        this.configurable = configurable;
    }

    public Short getEnabled() {
        return enabled;
    }

    public void setEnabled(Short enabled) {
        this.enabled = enabled;
    }

    public Short getCaptureValues() {
        return captureValues;
    }

    public void setCaptureValues(Short captureValues) {
        this.captureValues = captureValues;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getEnabledGroup() {
        return enabledGroup;
    }

    public void setEnabledGroup(Short enabledGroup) {
        this.enabledGroup = enabledGroup;
    }

    public Short getEnabledFilter() {
        return enabledFilter;
    }

    public void setEnabledFilter(Short enabledFilter) {
        this.enabledFilter = enabledFilter;
    }

    public Short getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Short isGroup) {
        this.isGroup = isGroup;
    }

    public Short getIsInsert() {
        return isInsert;
    }

    public void setIsInsert(Short isInsert) {
        this.isInsert = isInsert;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "MetaType{" +
                "typeid=" + typeid +
                ", type='" + type + '\'' +
                ", datatype='" + datatype + '\'' +
                ", display='" + display + '\'' +
                ", configurable=" + configurable +
                ", enabled=" + enabled +
                ", captureValues=" + captureValues +
                ", createtime=" + createtime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", description='" + description + '\'' +
                ", isGroup='" + isGroup + '\'' +
                ", isInsert='" + isInsert + '\'' +
                ", productId='" + productId + '\'' +
                ", page='" + page + '\'' +
                '}';
    }
}