package com.gzl.log.core.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.gzl.log.core.FastjsonCodec;
import com.gzl.log.core.config.RedisConfig;
import com.gzl.log.vo.LogVo;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Setter
@Getter
@NoArgsConstructor
public class RedisSender implements MessageSender<LogVo> {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    RedisAsyncCommands<String, String> commands = null;

    private RedisConfig config;

    private boolean asynch = true;
    private long timeout=0;

    @Override
    public boolean send(LogVo event) {
        System.out.println("send asynch::"+asynch+" topic::"+config.getTopic());
        if(asynch){
            return sendAsync(config.getTopic(), event);
        }else{
            return send(config.getTopic(), event);
        }
    }


    //@Override
    public boolean send(String topic, LogVo event) {
        try {
            RedisFuture<Long> rf = commands.lpush(topic, JSONObject.toJSONString(event));
            if (timeout > 0L)
                rf.get(timeout, TimeUnit.MILLISECONDS);
            else /*if (timeout == 0) */{
                rf.get();
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException | TimeoutException e) {
            System.out.println("Redis 同步发送日志失败 : " + e.getMessage());
            return false;
        }

    }

    //@Override
    public boolean sendAsync(String topic, LogVo event) {
        RedisFuture<Long> rf = commands.lpush(topic, JSONObject.toJSONString(event));
        return true;
    }

    @Override
    public void init() throws ExceptionInInitializerError{
        if(redisClient==null){
            if(StringUtils.isNotEmpty(config.getAddress())){
                //"redis://password@localhost:6379/0"
                redisClient = RedisClient.create(config.getAddress());
            }else if(StringUtils.isNotEmpty(config.getHost()) && config.getPort()>0){
                RedisURI.Builder builder = RedisURI.builder().withHost(config.getHost())
                        .withPort(config.getPort())
                        .withTimeout(Duration.ofMillis(config.getTimeout()))
                        .withDatabase(config.getDatabase());
                if(StringUtils.isNotEmpty(config.getUsername()) && StringUtils.isNotEmpty(config.getPassword())){
                    builder.withAuthentication(config.getUsername() ,config.getPassword());
                }
                RedisURI redisUri = builder.build();
                redisClient = RedisClient.create(redisUri);
            }
        }

        if(redisClient==null){
            throw new ExceptionInInitializerError("无法创建redisClient...");
        }

        connection = redisClient.connect();
        //connection = redisClient.connect(new FastjsonCodec());
        commands = connection.async();
        // init

    }

    @Override
    public void destroy() {
        if (redisClient == null ) {
            return;
        }
        if(connection!=null){
            connection.close();
            connection = null;
        }
        if (redisClient != null ) {
            redisClient.shutdown();
            redisClient = null;
        }

    }
}
