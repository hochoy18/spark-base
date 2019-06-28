package com.hochoy.cobub3_test;

import java.util.Date;

/**
 * Created by yfyuan on 2017/2/16.
 */
public class CobubJobHistory {

    private String jobId;
    private String taskId;
    private String contextName;
    private String binId;
    private String classpath;
    private String startTime;
    private String endTime;
    private String error;
    private String errorClass;
    private String errorStackTrace;
    private String sqlDetail;

    private Date startDate;
    private Date endDate;
    private String jobType;
    private String jobStatus;



    public CobubJobHistory() {

    }

    public CobubJobHistory(String jobId, String taskId, String contextName, String binId, String classpath, String startTime, String endTime, String error, String errorClass, String errorStackTrace, String sqlDetail, Date startDate, Date endDate, String jobType, String jobStatus) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.contextName = contextName;
        this.binId = binId;
        this.classpath = classpath;
        this.startTime = startTime;
        this.endTime = endTime;
        this.error = error;
        this.errorClass = errorClass;
        this.errorStackTrace = errorStackTrace;
        this.sqlDetail = sqlDetail;

        this.startDate = startDate;
        this.endDate = endDate;
        this.jobType = jobType;
        this.jobStatus = jobStatus;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorClass() {
        return errorClass;
    }

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }

    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    public String getSqlDetail() {
        return sqlDetail;
    }

    public void setSqlDetail(String sqlDetail) {
        this.sqlDetail = sqlDetail;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
