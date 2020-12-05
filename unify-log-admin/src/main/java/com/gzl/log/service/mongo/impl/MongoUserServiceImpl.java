package com.gzl.log.service.mongo.impl;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.MongoUser;
import com.gzl.log.repository.mongo.MongoUserDao;
import com.gzl.log.service.mongo.MongoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 把今天最好的表现当作明天最新的起点．．～
 * いま 最高の表現 として 明日最新の始発．．～
 * Today the best performance  as tomorrow newest starter!

 *
 * @author : xiaomo
 * github: https://github.com/houko
 * email: xiaomo@xiaomo.info
 * <p>
 * Date: 2016/11/15 15:45
 * Copyright(©) 2015 by xiaomo.
 **/

@Service
@ConditionalOnBean(MongoConfig.class)
public class MongoUserServiceImpl implements MongoUserService {
    private final MongoUserDao dao;

    @Autowired
    public MongoUserServiceImpl(MongoUserDao dao) {
        this.dao = dao;
    }

    @Override
    public List<MongoUser> findAll() {
        return dao.findAll();
    }

    @Override
    public MongoUser findById(Long id) {
        Optional<MongoUser> optionalUser = dao.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    public List<MongoUser>  findByName(String userName) {
        return dao.findByUserNameLike(userName);
    }

    @Override
    public MongoUser add(MongoUser mongoUser) {
        return dao.save(mongoUser);
    }

    @Override
    public void add(List<MongoUser> mongoUser){
        dao.insert(mongoUser);
    }

    @Override
    public void delete(Long id) {
        Optional<MongoUser> optional = dao.findById(id);
        if (!optional.isPresent()) {
            return;
        }
        dao.delete(optional.get());
    }

    @Override
    public MongoUser update(MongoUser mongoUser) {
        return dao.save(mongoUser);
    }
}
