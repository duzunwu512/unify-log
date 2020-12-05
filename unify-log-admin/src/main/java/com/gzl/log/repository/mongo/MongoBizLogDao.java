package com.gzl.log.repository.mongo;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.BizLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@ConditionalOnBean(MongoConfig.class)
public interface MongoBizLogDao extends MongoRepository<BizLog, String> {

    List<BizLog> findByCreaterId(String createrId);
    List<BizLog> findByBizId(String bizId);



}
