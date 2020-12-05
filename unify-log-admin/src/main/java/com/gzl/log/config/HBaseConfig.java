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

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(prefix = "log.db" ,name="type", havingValue = "hbase")
@ConditionalOnClass({HBaseConfiguration.class})
public class HBaseConfig implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /*@Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Value("${zookeeper.znode.parent}")
    private String znodeParent;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
        conf.set("hbase.dir", clientPort);
        conf.set("zookeeper.znode.parent", znodeParent);
        return new HbaseTemplate(conf);
    }*/

    @Value("${hbase.zookeeper.znode.parent}")
    public String zkParent;

    @Value("${hbase.zookeeper.quorum}")
    public String zkQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    public String zkPort;

    @Value("${hbase.hadoop.home.dir}")
    public String hbaseHadoopHome;

    @Bean(name="hbaseConfig")
    public org.apache.hadoop.conf.Configuration hbaseConfig(){
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("zookeeper.znode.parent",zkParent); //与 hbase-site-xml里面的配置信息 zookeeper.znode.parent 一致
        conf.set("hbase.zookeeper.quorum",zkQuorum);  //hbase 服务地址
        conf.set("hbase.zookeeper.property.clientPort",zkPort); //端口号
        conf.set("hbase.client.scanner.caching","100");
        return conf;
    }

    @Bean(name="executor")
    public ExecutorService getExecutor() {
        return Executors.newFixedThreadPool(20);
    }

    @Bean(name="hbaseConnection")
    public Connection createConnection(org.apache.hadoop.conf.Configuration hbaseConfig,ExecutorService executor){
        try {
            String osName = System.getProperty("os.name");
            if (osName.toLowerCase().startsWith("windows")) {
                System.setProperty("hadoop.home.dir", hbaseHadoopHome);
            }
            return ConnectionFactory.createConnection(hbaseConfig, executor);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("-----------HBaseConfig start---------------------");
    }
}
