package com.gzl.log.repository.elasticsearch;

import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.RunLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
@ConditionalOnBean(ElasticsearchConfig.class)
public interface ElasticsearchRunLogDao extends ElasticsearchRepository<RunLog, String> {

    List<RunLog> findByApp(String app);
    List<RunLog> findByNodeIp(String nodeIp);
    List<RunLog> findByRequestId(String requestId);
    List<RunLog> findByLevel(String level);
    List<RunLog> findByCreateDate(Date date);


}
