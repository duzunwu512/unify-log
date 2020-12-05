package com.gzl.log.logback.appender;

import com.alibaba.fastjson.JSON;
import com.gzl.log.core.LogClient;
import com.gzl.log.vo.RunLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogbackAppenderTest0 {
    private static final Logger log = LoggerFactory.getLogger(LogbackAppenderTest0.class);


    public static void saveRunLog(){
        log.debug("{} OK", "logback");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("{} OK", "logback");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.warn("Not OK", "logback");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0; i<20; i++){
            log.warn("--- OK", "logback++"+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.error("logback ERROR OCCUR!", new IllegalStateException("xxx"));
    }

    public static void save(){
        LogClient logClient = LogClient.getInstance();
        RunLogVo vo = new RunLogVo();
        for(int i=0; i<20; i++){
            vo = new RunLogVo();


            vo.setApp("appName:"+i);
            vo.setLevel("level:"+i);
            vo.setContent("msg"+i);
            vo.setNodeIp("node");
            vo.setLogger("loggerName");
            vo.setThread("threadName");


            logClient.saveLog(vo);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String arg[]) {

        //System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
        //System.setProperty("org.apache.commons.logging.simplelog.showdatetime","true");
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http","debug");

        Map<String, String> map =new HashMap<String, String>();
        map.put("userId", "userId");
        map.put("message", "msg");

        String reqSoapData = JSON.toJSONString(map);
        System.out.println("===========::"+reqSoapData);

        saveRunLog();
        //save();
    }
}