package com.gzl.log.logback.appender;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.CommandArgsAccessor;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.RedisCommand;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class RedisGetTest {


    private static StatefulRedisConnection<String, String> connection;
    private static RedisClient redisClient;

    public static void main(String args[]){
        RedisURI redisUri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        TimeoutOptions timeoutOptions = TimeoutOptions.builder().timeoutSource(new TimeoutOptions.TimeoutSource() {
            @Override
            public long getTimeout(RedisCommand<?, ?, ?> command) {

                if (command.getType() == CommandType.BLPOP) {
                    return TimeUnit.MILLISECONDS.toNanos(CommandArgsAccessor.getFirstInteger(command.getArgs()));
                }

                // -1 indicates fallback to the default timeout
                return -1;
            }
        }).build();

        ClientOptions options = ClientOptions.builder().timeoutOptions(timeoutOptions).build();


        redisClient = RedisClient.create(redisUri);
        redisClient.setOptions(options);

        connection  = redisClient.connect();


        RedisCommands<String, String> commands = connection.sync();
        boolean t = true;
        //while (t){
            System.out.println("--------------------------------");
            KeyValue kv = commands.brpop(5,"qwer");
            if(kv!=null){
                System.out.println("key:"+kv.getKey()+"="+kv.getValue());
            }

            //if(!t){
            //    break;
           // }
        //}



        //int i=0;
        /*while(true){
            if(i==1000){
                break;
            }

            //String s = commands.get("name");
            //System.out.println("Value["+i+"]:"+s);

            //System.out.println("V:"+commands.rpop("two"));

            System.out.println("--------------------------------");
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        connection.close();
        redisClient.shutdown();

    }
}
