package com.gzl.log.util;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

public class HttpClientUtils {

	private static PoolingHttpClientConnectionManager cm = null;

	private static CloseableHttpClient httpClient = null;

	/*最大连接数*/
	private static Integer MAX_HTTP_CONNECTION = 500;
	 /*每个路由的最大连接数*/
	private static Integer HTTP_LIMIT_PER_ROUTE = 50;
	
	//private static final int timeOut = 10 * 1000;
	
    /**
     * 向服务端请求超时时间设置(单位:毫秒)
     */
    private static int SERVER_REQUEST_TIME_OUT = 5000;
    /**
     * 服务端响应超时时间设置(单位:毫秒)
     */
    private static int SERVER_RESPONSE_TIME_OUT = 5000;

    /*从连接池获取连接的超时时间*/
    private static int CONNECTION_REQUEST_TIME_OUT = 5000;

    static {
		LayeredConnectionSocketFactory sslsf = null;
		try {
			sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("https", sslsf)
				.register("http", new PlainConnectionSocketFactory())
				.build();
		cm =new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		cm.setMaxTotal(MAX_HTTP_CONNECTION);
		cm.setDefaultMaxPerRoute(HTTP_LIMIT_PER_ROUTE);
	}

	public static CloseableHttpClient getDefaultHttpClient() {
		return HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler())
				.setConnectionManager(cm)
				.build();
	}
	
	/**
	 * 该方法主要是对HttpPost & HttpGet 设置连接、请求等过期时间的， 主要配合 getDefaultHttpClient()的使用
	 * 例如：
	 * HttpGet httpget = new HttpGet(url);
	 * config(httpget);
	 */
	public static void config(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
                .setConnectTimeout(SERVER_RESPONSE_TIME_OUT)
                .setSocketTimeout(SERVER_REQUEST_TIME_OUT).build();
        httpRequestBase.setConfig(requestConfig);
    }
    
    /**
     * 对httpClient设置过期时间
     * @return
     */
    public static RequestConfig getDefaultRequestConfig() {
    	RequestConfig requestConfig = RequestConfig.custom()
    			.setSocketTimeout(SERVER_REQUEST_TIME_OUT)
    			.setConnectTimeout(SERVER_RESPONSE_TIME_OUT)
    			.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
    			.build();
    	return requestConfig;
    }
    
    public static CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = getDefaultRequestConfig();
        if(null !=httpClient){
            return httpClient;
        }

        synchronized(cm){
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(cm)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                        @Override
                        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                            HeaderElementIterator it = new BasicHeaderElementIterator
                                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                            while (it.hasNext()) {
                                HeaderElement he = it.nextElement();
                                String param = he.getName();
                                String value = he.getValue();
                                if (value != null && param.equalsIgnoreCase("timeout")) {
                                    return Long.parseLong(value) * 1000;
                                }
                            }
                            return 60 * 1000;//如果没有约定，则默认定义时长为60s
                        }
                    })
                    .build();
        }

        return httpClient;
    }
    
    
    public static CloseableHttpClient getHttpClient(int SERVER_REQUEST_TIME_OUT, int SERVER_RESPONSE_TIME_OUT, int CONNECTION_REQUEST_TIME_OUT) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SERVER_REQUEST_TIME_OUT)
                .setConnectTimeout(SERVER_RESPONSE_TIME_OUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(cm)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build();
        return httpClient;
    }
    
    
    public static String doGet(String url) throws Exception {
        if (StringUtils.isEmpty(url)) {
            throw new Exception("url can not be null");
        }

        //    url = validateUrl(url);

        CloseableHttpResponse response = null;
        String body = null;
        try {
            HttpGet get = new HttpGet(url);

            get.addHeader("Content-type", "application/json; charset=utf-8");
            get.setHeader("Accept", "application/json");

            response = getHttpClient().execute(get);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(response.getEntity());
            } else {
                throw new Exception(String.format("doGet网络请求失败，error code [%s]", statusCode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }
    /**
     * 执行post方法
     * @param url
     * @param parameter
     * @return
     */
    public static String doPost(String url, String parameter) throws Exception {
        if(StringUtils.isEmpty(url)){
            throw new Exception("url can not be null");
        }

        //    url = validateUrl(url);
        long s = System.currentTimeMillis();
        String body = null;
        CloseableHttpResponse response = null;
        try {

            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            if(!StringUtils.isEmpty(parameter)) {
                httpPost.setEntity(new StringEntity(parameter, Charset.forName("UTF-8")));
            }

            response = getHttpClient().execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(response.getEntity(),"utf-8");
            } else {
                throw new Exception(String.format("doPost网络请求失败，error code [%s]", statusCode));
            }
            System.out.println("HTTP: USE : TIME: "+(System.currentTimeMillis()-s));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=response){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }

    public static String doPut(String url, String parameter) throws Exception {
//    if(DataSongStringUtils.isEmpty(parameter) || DataSongStringUtils.isEmpty(url)){
//       throw new Exception(Status.PARAM_ERROR, "param can not be null");
//    }

        //    url = validateUrl(url);

        String body = null;
        CloseableHttpResponse response = null;
        try {
            HttpPut httpPost = new HttpPut(url);

            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            if(null!=parameter){
                httpPost.setEntity(new StringEntity(parameter, Charset.forName("UTF-8")));
            }
            response = getHttpClient().execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(response.getEntity());
            } else {
                throw new Exception(String.format("doPut网络请求失败，error code [%s]", statusCode));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=response){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }

    public static String doDelete(String url) throws Exception {
        if(StringUtils.isEmpty(url)){
            throw new Exception("url can not be null");
        }

        //    url = validateUrl(url);

        String body = null;
        CloseableHttpResponse response = null;
        try {
            HttpDelete delete = new HttpDelete(url);

            delete.addHeader("Content-type", "application/json; charset=utf-8");
            delete.setHeader("Accept", "application/json");

            response = getHttpClient().execute(delete);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                // Read the response body
                body = EntityUtils.toString(response.getEntity());
            } else {
                throw new Exception(String.format("doDelete网络请求失败，error code [%s]", statusCode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=response){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return body;
    }

    protected static String validateUrl(String url){
        if(url.contains("?")){
            url = url.replace("?","%26");
        }

        if(url.contains("&")){
            url = url.replace("&","%3F");
        }

        if(url.contains("|")){
            url = url.replace("|","%124");
        }

        if(url.contains("=")){
            url = url.replace("=","%3D");
        }

        if(url.contains("#")){
            url = url.replace("#","%23");
        }

        if(url.contains("/")){
            url = url.replace("/","%2F");
        }

        if(url.contains("+")){
            url = url.replace("+","%2B");
        }

        if(url.contains("%")){
            url = url.replace("%","%25");
        }

        if(url.contains(" ")){
            url = url.replace(" ","%20");
        }

        return url;
    }
}

