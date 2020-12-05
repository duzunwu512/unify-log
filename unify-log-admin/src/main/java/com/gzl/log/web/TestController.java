package com.gzl.log.web;

import com.gzl.log.config.AppConfig;
import com.gzl.log.config.KafkaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@ConditionalOnBean(KafkaConfig.class)
@RequestMapping("test")
public class TestController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 发送消息
    @GetMapping("/kafka/send/{message}")
    public void sendMessage1(@PathVariable("message") String normalMessage) {
        kafkaTemplate.send("kafka-test-topic", normalMessage);
    }



}
