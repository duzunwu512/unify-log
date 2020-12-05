package com.gzl.log.logback.appender;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisListCommands;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public class RedisTest {


    private static StatefulRedisConnection<String, String> connection;
    private static RedisClient redisClient;

    public static void main1(String args[]){
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        redisClient = RedisClient.create(redisUri);
        connection  = redisClient.connect();

        RedisCommands<String, String> commands = connection.sync();
        //commands.set("name", "有肉");
        commands.lpush("qwer", "1");
        commands.lpush("qwer",  "2");
        commands.lpush("qwer",  "3");
        commands.lpush("qwer",  "4");
        commands.lpush("qwer",  "5");
        //String v = commands.rpop("two");
        //System.out.println("Value::"+v);
        System.out.println("Value::"+commands.rpop("qwer"));

        connection.close();
        redisClient.shutdown();

    }

    public static void main(String args[]){
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withDatabase(0)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        redisClient = RedisClient.create(redisUri);
        connection  = redisClient.connect();

        RedisAsyncCommands<String, String> commands = connection.async();
        //commands.set("name", "有肉");


        for(int i=0; i<1000; i++){
            System.out.println("put Value::"+"100---000"+i);
            RedisFuture<Long> rf = commands.lpush("log-stash", "100---000"+i);
            try {
                Long value = rf.get(1, TimeUnit.MINUTES);
                //Long value = rf.get();
                System.out.println("-----------------Value::"+value);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            catch (TimeoutException e) {
                e.printStackTrace();
            }
        }


        //System.out.println("Value::"+commands.rpop("qwer"));

        connection.close();
        redisClient.shutdown();

    }
}
