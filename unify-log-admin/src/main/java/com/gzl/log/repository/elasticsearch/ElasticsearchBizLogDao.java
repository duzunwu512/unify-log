package com.gzl.log.repository.elasticsearch;

import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.BizLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@ConditionalOnBean(ElasticsearchConfig.class)
public interface ElasticsearchBizLogDao extends ElasticsearchRepository<BizLog, String> {

    List<BizLog> findByCreaterId(String createrId);
    List<BizLog> findByBizId(String bizId);



}
