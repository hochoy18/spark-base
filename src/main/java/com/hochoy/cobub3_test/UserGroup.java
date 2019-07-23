package com.hochoy.cobub3_test;

import java.math.BigDecimal;
import java.util.Date;

public class UserGroup {

    private Integer id;

    private String groupName;

    private String type;

    private BigDecimal userCount;

    private String groupId;

    private String userName;

    private Date crateTime;

    private Date modifyTime;

    private Short enabled;

    private String crateType;

    private String modifyType;

    private Long productId;

    private Short enabledGroup;

    private Short enabledFilter;

    private Page page;

    private String queryJson;

    public String getQueryJson() {
        return queryJson;
    }

    public void setQueryJson(String queryJson) {
        this.queryJson = queryJson;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BigDecimal getUserCount() {
        return userCount;
    }

    public void setUserCount(BigDecimal userCount) {
        this.userCount = userCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(Date crateTime) {
        this.crateTime = crateTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Short getEnabled() {
        return enabled;
    }

    public void setEnabled(Short enabled) {
        this.enabled = enabled;
    }

    public String getCrateType() {
        return crateType;
    }

    public void setCrateType(String crateType) {
        this.crateType = crateType;
    }

    public String getModifyType() {
        return modifyType;
    }

    public void setModifyType(String modifyType) {
        this.modifyType = modifyType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", type='" + type + '\'' +
                ", userCount=" + userCount +
                ", groupId='" + groupId + '\'' +
                ", userName='" + userName + '\'' +
                ", crateTime=" + crateTime +
                ", modifyTime=" + modifyTime +
                ", enabled=" + enabled +
                ", crateType='" + crateType + '\'' +
                ", modifyType='" + modifyType + '\'' +
                ", productId=" + productId +
                ", enabledGroup=" + enabledGroup +
                ", enabledFilter=" + enabledFilter +
                ", page=" + page +
                ", queryJson='" + queryJson + '\'' +
                '}';
    }
}