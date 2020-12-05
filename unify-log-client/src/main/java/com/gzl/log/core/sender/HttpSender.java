package com.gzl.log.core.sender;

import com.alibaba.fastjson.JSON;
import com.gzl.log.core.LogClient;
import com.gzl.log.core.config.HttpConfig;
import com.gzl.log.core.config.KafkaConfig;
import com.gzl.log.vo.LogVo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Kafka消息发送者
 */
@Setter
@Getter
public class HttpSender implements MessageSender<LogVo> {

    private HttpConfig config;

    private LogClient logClient;

    private long timeout;
    private boolean asynch = true;

    public HttpSender(){}


    @Override
    public boolean send(LogVo event) {

        if(!asynch){
            return logClient.saveLog(event);
        }else{
            return logClient.asyncSaveLog(event);
        }
    }

    @Override
    public void init() {
        if(StringUtils.isEmpty(config.getLogServer())){
            throw new NullPointerException("LogServer can't be null.");
        }
        logClient = LogClient.getInstance(config.getLogServer());
        // init
    }

    @Override
    public void destroy() {
        logClient = null;
    }
}
