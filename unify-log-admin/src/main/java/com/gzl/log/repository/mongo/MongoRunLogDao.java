package com.gzl.log.repository.mongo;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.RunLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
@ConditionalOnBean(MongoConfig.class)
public interface MongoRunLogDao extends MongoRepository<RunLog, String> {

    List<RunLog> findByApp(String app);
    List<RunLog> findByNodeIp(String nodeIp);
    List<RunLog> findByRequestId(String requestId);
    List<RunLog> findByLevel(String level);
    List<RunLog> findByCreateDate(Date date);


}
