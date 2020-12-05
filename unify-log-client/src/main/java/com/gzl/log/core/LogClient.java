package com.gzl.log.core;

import com.alibaba.fastjson.JSON;
import com.gzl.log.util.HttpClientUtils;
import com.gzl.log.vo.LogVo;
import com.gzl.log.vo.RunLogVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LogClient {

   // private Logger logger = LoggerFactory.getLogger(this.getClass());

    //异步保存
    private static String asyncSaveUrl="runlog/saveAll";

    //同步保存
    private static String saveUrl="runlog/save";

    //存保存日志 List结果集
    private static String searchUrl="runlog/findAll";

    //分页取日志
    private static String pageUrl;

    private String logServer;

    private static LogClient instance;

    public LogClient(){
        //this(null, null,null, null);
    }

    private LinkedBlockingQueue<LogVo> entityQueue = new LinkedBlockingQueue<LogVo>();
    private Thread saveEntityThread;
    private volatile boolean toStop = false;

    public void start(){
        saveEntityThread = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("开启线程异步处理日志请求...");
                // normal callback
                List<LogVo> logs = null;
                while(!toStop){
                    try {
                        LogVo log = entityQueue.take();
                        System.out.println("1111111111111111111111111111111...");
                        if (log != null) {

                            // callback list param
                            logs = new ArrayList<LogVo>();
                            int drainToNum = entityQueue.drainTo(logs);
                            logs.add(log);
                            System.out.println("处理｛｝个请求..."+logs.size());
                            doSaveLog(logs);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    System.out.println("toStop...::"+toStop);
                }

                // last callback
                try {
                    logs = new ArrayList<LogVo>();
                    System.out.println("兜底处理{}个请求..."+ logs.size());
                    int drainToNum = entityQueue.drainTo(logs);
                    if (null !=logs) {
                        doSaveLog(logs);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        //saveEntityThread.setDaemon(true);
        saveEntityThread.start();
    }

    public LogClient(String asyncSaveUrl, String saveUrl,String searchUrl, String pageUrl){
        this.asyncSaveUrl = asyncSaveUrl;
        this.saveUrl = saveUrl;
        this.searchUrl = searchUrl;
        this.pageUrl = pageUrl;
    }

    public LogClient(String logServer){
        if(StringUtils.isNotEmpty(logServer)){
            if(!logServer.endsWith("/")){
                logServer+="/";
            }
            this.asyncSaveUrl = logServer+asyncSaveUrl;
            this.saveUrl = logServer+saveUrl;
            this.searchUrl = logServer+searchUrl;
            this.pageUrl = logServer+pageUrl;
        }
    }

    public static LogClient getInstance(){

        synchronized (asyncSaveUrl){
            instance = new LogClient();
            instance.start();
        }
        return instance;
    }

    public static LogClient getInstance(String logServer){

        synchronized (asyncSaveUrl){
            instance = new LogClient(logServer);
            instance.start();
        }
        return instance;
    }

    public boolean saveLog(LogVo runlog){
        if(StringUtils.isEmpty(saveUrl)){
            System.out.println("asyncSaveUrl不能为空");
            throw new NullPointerException("asyncSaveUrl为空");
        }
        boolean rslt = false;
        if(null!=runlog){
            String reqSoapData = JSON.toJSONString(runlog);
            System.out.println("发送数据：：：："+reqSoapData);
            try {
                String body = HttpClientUtils.doPost(saveUrl.toString(), reqSoapData);
                System.out.println("body:::{}"+body);
                rslt = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return rslt;
    }

    public boolean asyncSaveLog(LogVo runlog){
        entityQueue.add(runlog);
        return true;
    }

    private void doSaveLog(List<LogVo> vos){
        if(StringUtils.isEmpty(asyncSaveUrl)){
            throw new NullPointerException("asyncSaveUrl为空");
        }
        if(null!=vos && vos.size()>0){
            System.out.println("LogVo的个数："+vos.size());
            try {
                String reqSoapData = JSON.toJSONString(vos);
                String rslt = HttpClientUtils.doPost(asyncSaveUrl.toString(), reqSoapData);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void setLogServer(String logServer) {
        if(StringUtils.isNotEmpty(logServer)){
            if(!logServer.endsWith("/")){
                logServer +="/";
            }
            asyncSaveUrl = logServer+asyncSaveUrl;
            saveUrl = logServer+saveUrl;
            searchUrl = logServer+searchUrl;
        }
        this.logServer = logServer;
    }

    //停止
    public void stop(boolean stop){
        this.toStop = stop;
    }
}
