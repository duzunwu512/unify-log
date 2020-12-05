package com.gzl.log.util.security;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
//@ConfigurationProperties(prefix = "log.user", ignoreUnknownFields = false)
public class User {
    @Value("${log.admin.userName:admin}")
    private String userName;

    @Value("${log.admin.password:admin}")
    private String password;

}
