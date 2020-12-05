package com.gzl.log.core.sender;

import com.alibaba.fastjson.JSON;
import com.gzl.log.core.LoggerEvent;
import com.gzl.log.core.config.KafkaConfig;
import com.gzl.log.vo.LogVo;
import lombok.Getter;
import lombok.Setter;
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
public class KafkaSender implements MessageSender<LogVo> {

    private KafkaProducer<Long, String> producer;

    private KafkaConfig config;

    private long timeout=0;
    private boolean asynch = true;

    public KafkaSender(){}


    @Override
    public boolean send(LogVo event) {
        System.out.println("KafkaSender.send asynch:"+asynch);
        if(asynch){
            return sendAsync(this.getConfig().getTopic(), event);
        }else{
            return send(this.getConfig().getTopic(), event);
        }
    }

    //@Override
    public boolean send(String topic, LogVo event) {
        try {
            final Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, JSON.toJSONString(event)));
            if (timeout > 0L)
                future.get(timeout, TimeUnit.MILLISECONDS);
            else /*if (timeout == 0) */{
                RecordMetadata rm = future.get();
                System.out.println(rm);
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException | TimeoutException e) {
            System.out.println("Kafka 同步发送日志失败 : " + e.getMessage());
            return false;
        }
    }

    //@Override
    public boolean sendAsync(String topic, LogVo event) {
        producer.send(new ProducerRecord<>(topic, JSON.toJSONString(event)), (metadata, exception) -> {
            if (exception != null) {
                System.out.println("Kafka 异步发送日志失败 : " + exception.getMessage());
            }
        });
        return true;
    }

    @Override
    public void init() {
        System.out.println("KafkaSender.init ");
        if(producer==null){
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getConfig().getAddresses());
            //props.put(ProducerConfig.CLIENT_ID_CONFIG, this.getConfig().getClient());
            props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.getConfig().getConnectionTimeout());
            props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, this.getConfig().getMaxBlockMs());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            this.producer = new KafkaProducer<>(props);
        }

        // init
    }

    @Override
    public void destroy() {
        producer.close();
    }
}
