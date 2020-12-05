package com.gzl.log.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@ConditionalOnProperty(prefix = "log.db" ,name="type", havingValue = "elasticsearch")
@ConditionalOnClass({ElasticsearchRestTemplate.class})
@EnableElasticsearchRepositories(basePackages = {"com.gzl.log"})
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Value("${spring.elasticsearch.rest.uris}")
    private String dburl;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(dburl)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("-----------ElasticsearchConfig start---------------------");
    }
}
