package com.gzl.log.logback.appender;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAppenderTest {
    private static final Logger log = LoggerFactory.getLogger(LogbackAppenderTest.class);

    @Test
    public void test() {
        log.debug("{} debug OK", "logback");
        log.info("{} info OK", "logback");
        log.warn("{} warn Not OK", "logback");
        log.error("logback ERROR OCCUR!", new IllegalStateException("xxx"));


        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*int i=0;

        while (true){

            try {
                Thread.sleep(100);N
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
            if(i%5==0){
                log.debug("DEBUG--中国疾病预防控制中心首席流行病学专家吴尊友出席东南科技论坛，并做了题为“新冠疫情形势与防控策略”的专题报告{}..{}", "logback", i);
            }
            if(i%5==1){
                log.info("INFO--中国疾病预防控制中心首席流行病学专家吴尊友出席东南科技论坛，并做了题为“新冠疫情形势与防控策略”的专题报告{}..{}", "logback", i);
            }
            if(i%5==2){
                log.warn("WARN--中国疾病预防控制中心首席流行病学专家吴尊友出席东南科技论坛，并做了题为“新冠疫情形势与防控策略”的专题报告{}..{}", "logback", i);
            }
            if(i%5==3){
                log.trace("TRANCE--中国疾病预防控制中心首席流行病学专家吴尊友出席东南科技论坛，并做了题为“新冠疫情形势与防控策略”的专题报告{}..{}", "logback", i);
            }
            if(i%5==4){
                log.error("ERROR-- OCCUR!", new IllegalStateException("用户名不可为空"));
            }

        }*/

    }
}