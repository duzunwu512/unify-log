package com.gzl.log.listener;

import com.gzl.log.config.RedisConfig;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnBean(RedisConfig.class)
public class RedisLogListener implements DisposableBean, InitializingBean{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;
    @Autowired
    private RedisCommands<String, String> redisCommands;

    @Autowired
    private RedisConfig redisConfig;

    private boolean isListening = true;

    private Thread redisListenerThread = null;

    // 消费监听
    public void onMessage(){
        // 消费的哪个topic、partition的消息,打印出消息内容
        logger.info("启动Redis对队列 {} 的监听...", redisConfig.getTopic());
        if(redisListenerThread==null){
            redisListenerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isListening){
                        logger.info("******************************{}*************{}",redisConfig.getTimeout(), redisConfig.getTopic());
                        try{
                            KeyValue kv = redisCommands.brpop(2000, redisConfig.getTopic());
                            if(kv!=null){
                                System.out.println("key:"+kv.getKey()+"="+kv.getValue());
                            }
                           //TimeUnit.MILLISECONDS.sleep(300);
                        }catch (RedisCommandTimeoutException re){
                            //re.printStackTrace();
                        }//catch (InterruptedException e){}
                    }
                }
            });
        }
        redisListenerThread.start();
    }

    public void restartListener(){
        isListening = true;
        onMessage();
    }

    public void stopListener(){
        isListening = false;
    }


    public void destroy() throws Exception{
        /*if (redisCommands != null ) {
            redisCommands.shutdown(true);
        }*/
        if(redisConnection!=null){
            redisConnection.close();
            redisConnection = null;
        }
        if (redisClient != null ) {
            redisClient.shutdown();
            redisClient = null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}
