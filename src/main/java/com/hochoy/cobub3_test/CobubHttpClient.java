package com.hochoy.cobub3_test;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * HTTP访问类，对Apache HttpClient进行简单封装，适配器模式
 *
 * @version 1.0
 */
public class CobubHttpClient {
    private static Logger logger = LoggerFactory.getLogger("CobubHttpClient");

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static CobubHttpClient instance = null;

    private static CookieStore cookieStore;
    private static String userAgent = "User-Agent";
    private static HttpClientContext context ;


    static {
        cookieStore = new BasicCookieStore();

        // 将CookieStore设置到httpClient中
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    public static String getCookie(String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return null;

    }

    private CobubHttpClient() {

    }

    /**
     */
    public static synchronized CobubHttpClient getInstance() {
        if (instance == null) {
                instance = new CobubHttpClient();
        }
        return instance;
    }

    /**
     * 处理GET请求
     *
     * @param url
     * @param params
     * @return
     */
    public HttpEntity doGet(String url, List<BasicNameValuePair> params, boolean redirect,
                            Map<String, String> headerMap) {
        HttpEntity entity = null;
        HttpGet httpGet = new HttpGet();

        try {
            if (params != null) {
                String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
                httpGet = new HttpGet(url + "?" + paramStr);
            } else {
                httpGet = new HttpGet(url);
            }
            if (!redirect) {
                httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
            }
            httpGet.setHeader(userAgent, Config.USER_AGENT);
            if (headerMap != null) {
                Set<Entry<String, String>> entries = headerMap.entrySet();
                for (Entry<String, String> entry : entries) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            entity = response.getEntity();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return entity;
    }

    /**
     * 处理POST请求
     *
     * @param url
     * @param
     * @return
     */
    public HttpEntity doPost(String url, String paramsStr) {
        HttpEntity entity = null;
        HttpPost httpPost = new HttpPost();
        try {
            StringEntity params = new StringEntity(paramsStr, Consts.UTF_8);
            httpPost = new HttpPost(url);
            httpPost.setEntity(params);
            httpPost.setHeader("Content-type", "application/text; charset=utf-8");
            httpPost.setHeader(userAgent, Config.USER_AGENT);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            entity = response.getEntity();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        return entity;
    }




    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    class Config{
        public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
    }
}