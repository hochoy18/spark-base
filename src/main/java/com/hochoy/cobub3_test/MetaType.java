package com.hochoy.cobub3_test;

import java.util.Date;

/**
 * 元数据type
 */
public class MetaType {
    private Long typeid;

    private String type;

    private String datatype;

    private String display;

    private Short global;

    private Short configurable;

    private Short enabled;

    private Short captureValues;

    private Date createtime;

    private String description;

    private Short enabledGroup;

    private Short enabledFilter;

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

    public Short getGlobal() {
        return global;
    }

    public void setGlobal(Short global) {
        this.global = global;
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

    @Override
    public String toString() {
        return "MetaType{" +
                "typeid=" + typeid +
                ", type='" + type + '\'' +
                ", datatype='" + datatype + '\'' +
                ", display='" + display + '\'' +
                ", global=" + global +
                ", configurable=" + configurable +
                ", enabled=" + enabled +
                ", captureValues=" + captureValues +
                ", createtime=" + createtime +
                ", description='" + description + '\'' +
                '}';
    }
}