package com.gzl.log.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(prefix = "log.db" ,name="type", havingValue = "mongodb")
@ConditionalOnClass({MongoTemplate.class})
@EnableMongoRepositories(basePackages = {"com.gzl.log"})
public class MongoConfig implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.data.mongodb.uri}")
    String mongodbUrl;

    @Bean
    public ConnectionString connectionString() {
        logger.info("mongodb#connectionString");
        return new ConnectionString(mongodbUrl);
    }

    @Bean
    public MongoClient mongoClient() {
        logger.info("mongodb#mongoClient");
        return MongoClients.create(connectionString());
    }

     @Bean
     public MongoTemplate mongoTemplate() {
        logger.info("mongodb#mongoTemplate");
        if(!StringUtils.isEmpty(connectionString().getDatabase())){
            return new MongoTemplate(mongoClient(), connectionString().getDatabase());
        }else{
            logger.error("mongodb uri配置错误，没有数据库信息");
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("-----------MongoConfig start---------------------");
    }
}
