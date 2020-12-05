package com.gzl.log.logback.appender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class Test {


    public static void main(String[] args) {
        System.out.println("dddddddddddddddddd");
        //1. 准备Kafka生产者配置信息
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        // string 序列化（Object ---> byte[]）器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        //2. 创建kafka生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //3. 生产记录并将其发布
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("kafka-test-topic", UUID.randomUUID().toString(),"Hello9999999 Kafka");

        producer.send(record);

        //4. 释放资源
        producer.flush();
        producer.close();
        System.out.println("end...............");
    }
}
