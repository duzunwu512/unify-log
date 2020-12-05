package com.gzl.log.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.CommandArgsAccessor;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.RedisCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Configuration
@ConditionalOnProperty(prefix = "log.send" ,name="channel", havingValue = "redis")
@ConditionalOnClass({RedisClient.class})
public class RedisConfig implements InitializingBean {

    @Value("${spring.redis.address:}")
    private String address;

    @Value("${spring.redis.host:localhost}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.topic:log-stash}")
    private String topic = "";

    @Value("${spring.redis.timeout:3000}")
    private int timeout=0;

    private int database = 0;

    @Value("${spring.redis.password:}")
    private String password;
    @Value("${spring.redis.username:}")
    private String username;

    @Bean
    @ConditionalOnMissingBean(name = "redisClient")
    public RedisClient redisClient() {
        RedisClient rc;
        if(StringUtils.isNotEmpty(this.address)){
            //"redis://password@localhost:6379/0"
            rc = RedisClient.create(this.address);
        }else if(StringUtils.isNotEmpty(this.host) && this.port>0){
            RedisURI.Builder builder = RedisURI.builder().withHost(this.host)
                    .withPort(this.port)
                    .withTimeout(Duration.ofMillis(this.timeout+this.timeout))
                    .withDatabase(this.database);
            if(StringUtils.isNotEmpty(this.username) && StringUtils.isNotEmpty(this.password)){
                builder.withAuthentication(this.username ,this.password);
            }
            RedisURI redisUri = builder.build();
            rc = RedisClient.create(redisUri);
        }else{
            rc = RedisClient.create();
        }

        TimeoutOptions timeoutOptions = TimeoutOptions.builder().timeoutSource(new TimeoutOptions.TimeoutSource() {
            @Override
            public long getTimeout(RedisCommand<?, ?, ?> command) {

                if (command.getType() == CommandType.BLPOP) {
                    System.out.println("----0000----00------");
                    return TimeUnit.MILLISECONDS.toNanos(CommandArgsAccessor.getFirstInteger(command.getArgs()));
                }

                // -1 indicates fallback to the default timeout
                return -1;
            }
        }).build();

        ClientOptions options = ClientOptions.builder().timeoutOptions(timeoutOptions).build();//TimeoutOptions.enabled())
        //rc.setOptions(options);
        return rc;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisConnection")
    public StatefulRedisConnection<String, String> redisConnection(){
        RedisClient redisClient = redisClient();
        return redisClient.connect();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisCommands")
    public RedisCommands<String, String> redisCommands() {
        StatefulRedisConnection<String, String> connection = redisConnection();
        return connection.sync();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisAsyncCommands")
    public RedisAsyncCommands<String, String> redisAsyncCommands() {
        StatefulRedisConnection<String, String> connection = redisConnection();
        return connection.async();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("-----------RedisConfig start---------------------");
    }
}
