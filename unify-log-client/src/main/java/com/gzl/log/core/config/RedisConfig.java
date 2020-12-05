package com.gzl.log.core.config;

import com.gzl.log.core.Consts;
import lombok.Getter;
import lombok.Setter;

/**
 * Redis配置类 部分注释参考{@link org.redisson.config.BaseConfig}
 *
 * @author Genesis
 * @since 1.0
 */
@Getter
@Setter
public class RedisConfig {

    /**
     * address 与 host & port 二者有其中一个即可
     */
    private String address;

    private String host;
    private int port;

    private String topic = Consts.DEFAULT_TOPIC;

    private int timeout=3000;

    private int database = 0;
    private String password;
    private String username;

    private int retryAttempts = 3;
}
