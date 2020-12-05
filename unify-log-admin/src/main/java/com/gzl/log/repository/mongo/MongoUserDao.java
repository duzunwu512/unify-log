package com.gzl.log.repository.mongo;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.MongoUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@ConditionalOnBean(MongoConfig.class)
public interface MongoUserDao extends MongoRepository<MongoUser, Long> {

    /**
     * 根据字字查用户
     *
     * @param userName
     * @return
     */
    //MongoUser findByUserName(String userName);
    List<MongoUser> findByUserName(String userName);
    List<MongoUser> findByUserNameLike(String userName);

}
