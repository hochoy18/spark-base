package com.hochoy.cobub3_test;

import java.util.Date;

/**
 * 事件分析report
 */
public class ActionReport {
    private Integer reportId;

    private String reportName;

    private String dateRange;

    private String taskSql;

    private String description;

    private Date createTime;

    private String unit;

    private Integer productId;

    private String taskConditions;

    private String tableName;

    private Integer maxLine;

    private Integer taskEnable;

    private Integer resultNum;

    private String userProp;

    private String userGroupid;


    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName == null ? null : reportName.trim();
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange == null ? null : dateRange.trim();
    }

    public String getTaskSql() {
        return taskSql;
    }

    public void setTaskSql(String taskSql) {
        this.taskSql = taskSql == null ? null : taskSql.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getTaskConditions() {
        return taskConditions;
    }

    public void setTaskConditions(String taskConditions) {
        this.taskConditions = taskConditions == null ? null : taskConditions.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public Integer getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(Integer maxLine) {
        this.maxLine = maxLine;
    }

    public Integer getTaskEnable() {
        return taskEnable;
    }

    public void setTaskEnable(Integer taskEnable) {
        this.taskEnable = taskEnable;
    }

    public Integer getResultNum() {
        return resultNum;
    }

    public void setResultNum(Integer resultNum) {
        this.resultNum = resultNum;
    }

    public String getUserProp() {
        return userProp;
    }

    public void setUserProp(String userProp) {
        this.userProp = userProp;
    }

    public String getUserGroupid() {
        return userGroupid;
    }

    public void setUserGroupid(String userGroupid) {
        this.userGroupid = userGroupid;
    }


    @Override
    public String toString() {
        return "ActionReport{" +
                "reportId=" + reportId +
                ", reportName='" + reportName + '\'' +
                ", dateRange='" + dateRange + '\'' +
                ", taskSql='" + taskSql + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", unit='" + unit + '\'' +
                ", productId=" + productId +
                ", taskConditions='" + taskConditions + '\'' +
                ", tableName='" + tableName + '\'' +
                ", maxLine=" + maxLine +
                ", taskEnable=" + taskEnable +
                ", resultNum=" + resultNum +
                ", userProp='" + userProp + '\'' +
                ", userGroupid='" + userGroupid + '\'' +
                '}';
    }


}