package com.gzl.log.core.config;

import com.gzl.log.core.Consts;
import lombok.Getter;
import lombok.Setter;

/**
 * Kafka配置类 {@link org.apache.kafka.clients.producer.ProducerConfig}
 *
 * @author Genesis
 * @since 1.0
 */
@Getter
@Setter
public class HttpConfig {
    /**
     * 服务器地址
     */
    private String logServer = null;

}
