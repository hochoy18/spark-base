package com.hochoy.cobub3_test;


/**
 * 配置信息
 *
 * @Author: hongbing.li
 * @Date: 30/10/2018 10:35 AM
 */
public class JobServer {


    private String baseUrl;

    public String baseUrl() {
        return baseUrl;
    }

    //GET lists all current binaries
    public String listBinariesUrl() {
        return baseUrl + "/binaries";
    }

    //GET lists all current jars
    public String listJarsUrl() {
        return baseUrl + "/jars";
    }

    //GET /contexts : lists all current contexts
    public String listContextsUrl() {
        return baseUrl + "/contexts";
    }

    //GET /contexts/<contextName> : returns some info about the context (such as spark UI url)
    public String getContextUrl() {
        return baseUrl + "/contexts/%s";
    }

    //POST /contexts/<contextName> : creates a new context
    public String createContextUrl() {
        return baseUrl + "/contexts/%s?num-cpu-cores=2&memory-per-node=2G&spark.executor.instances=3&context-factory=spark.jobserver.context.SessionContextFactory";
    }

    //DELETE /contexts/<contextName> : stops a context and all jobs running in it
    public String deleteContextUrl() {
        return baseUrl + "/contexts/%s";
    }

    //GET /healthz : getting health status of job server,return OK or error message
    public String healthzUrl() {
        return baseUrl + "/healthz";
    }

    //GET /jobs/<jobId> : Returns job information in JSON
    public String jobInfoUrl() {
        return baseUrl + "/jobs/%s";
    }

    //DELETE /jobs/<jobId> : Stop the current job. All other jobs submitted with this spark context will continue to run
    public String deleteJobUrl() {
        return baseUrl + "/jobs/%s";
    }

    //GET /jobs : returns a JSON list of hashes containing job status
    public String jobsUrl() {
        return baseUrl + "/jobs";
    }

    //POST /jobs, Starts a new job
    public String loadtableUrl() {
        return baseUrl + "/jobs?appName=cobubsessionJob&classPath=com.cobub.analytics.queryengine.LoadDataJob&context=cobub-session-context&sync=true&timeout=60000";
    }

    //POST /jobs, Starts a new job
    public String startJobUrl() {
        return baseUrl + "/jobs?appName=cobubsessionJob&classPath=com.cobub.analytics.queryengine.QueryJob&context=cobub-session-context&sync=true&timeout=60000";
    }


    //分析一条sql是否正确，返回fields
    public String schemaUrl() {
        return baseUrl + "/jobs?appName=cobubsessionJob&classPath=com.cobub.analytics.queryengine.analyzeSchema&context=cobub-session-context&sync=true&timeout=60";
    }

    //schema 动态 create
    public String schemaCreateUrl() {
        return baseUrl + "/jobs?appName=cobubsessionJob&classPath=com.cobub.analytics.queryengine.LoadUserJob&context=cobub-session-context&&sync=true&timeout=600";
    }



}
