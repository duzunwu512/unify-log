package com.gzl.log.service.mongo;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.RunLog;
import com.gzl.log.repository.mongo.MongoRunLogDao;
import com.gzl.log.service.RunLogService;
import com.gzl.log.util.DateUtil;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.RunLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(MongoConfig.class)
public class MongoRunLogService implements RunLogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MongoRunLogDao mongoRunLogDao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Iterable<RunLog> findAll() {
        return mongoRunLogDao.findAll();
    }

    @Override
    public RunLog findById(String id) {
        Optional<RunLog> optionalUser = mongoRunLogDao.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    public List<RunLog> findByApp(String app) {
        Optional<List<RunLog>> optionalUser = Optional.ofNullable(mongoRunLogDao.findByApp(app));
        return optionalUser.orElse(null);
    }

    @Override
    public RunLog insert(RunLog optLog) {
        return mongoRunLogDao.save(optLog);
    }

    @Override
    public void insert(List<RunLog> optLogs) {
        mongoRunLogDao.insert(optLogs);
    }

    @Override
    public void delete(String id) {
        Optional<RunLog> optional = mongoRunLogDao.findById(id);
        if (!optional.isPresent()) {
            return;
        }
        mongoRunLogDao.delete(optional.get());
    }

    @Override
    public RunLog update(RunLog mongoUser) {
        return mongoRunLogDao.save(mongoUser);
    }

    @Override
    public PageInfo<RunLog> page(RunLogVo runLogVo, Pageable pageable) {
        if(pageable==null){
            pageable = PageRequest.of(0, 10,Sort.Direction.ASC,"createDate");
        }

        Query query = findQuery(runLogVo);

        long total = mongoTemplate.count(query, RunLog.class);
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<RunLog> datas = mongoTemplate.find(query, RunLog.class);
        logger.info("sql:::{}", query.toString());
        PageInfo<RunLog> page = new PageInfo<RunLog>(pageable.getPageNumber(), pageable.getPageSize(), total,  datas);

        return page;
    }

    private Query findQuery(RunLogVo runLogVo){
        Query query = new Query();
        if(!StringUtils.isEmpty(runLogVo.getApp())){
            query.addCriteria(Criteria.where("app").is(runLogVo.getApp()));
        }
        if(!StringUtils.isEmpty(runLogVo.getLevel())){
            query.addCriteria(Criteria.where("level").is(runLogVo.getLevel()));
        }
        if(!StringUtils.isEmpty(runLogVo.getNodeIp())){
            query.addCriteria(Criteria.where("nodeIp").regex("^.*"+runLogVo.getNodeIp()+".*$"));
        }
        if(!StringUtils.isEmpty(runLogVo.getContent())){
            query.addCriteria(Criteria.where("content").regex("^.*"+runLogVo.getContent()+".*$"));
        }

        if(!StringUtils.isEmpty(runLogVo.getStartDate()) && !StringUtils.isEmpty(runLogVo.getEndDate())){
            query.addCriteria(Criteria.where("createDate").gte(DateUtil.parseDate(runLogVo.getStartDate(), DateUtil.LONG_FORMAT) ).lte(DateUtil.parseDate(runLogVo.getEndDate(), DateUtil.LONG_FORMAT) ));
        }else if(!StringUtils.isEmpty(runLogVo.getStartDate())){
            query.addCriteria(Criteria.where("createDate").gte(DateUtil.parseDate(runLogVo.getStartDate(), DateUtil.LONG_FORMAT) ));
        }else  if(!StringUtils.isEmpty(runLogVo.getEndDate())){
            query.addCriteria(Criteria.where("createDate").lte(DateUtil.parseDate(runLogVo.getEndDate(), DateUtil.LONG_FORMAT) ));
        }

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "createDate"));
        query.with(Sort.by(orders));

        return  query;
    }

    @Override
    public List<RunLog> page(RunLogVo runLogVo, long skip, int size) {
        Query query = findQuery(runLogVo);
        query.skip(skip);
        query.limit(size);

        query.fields().exclude("stackTrace");
        List<RunLog> datas = mongoTemplate.find(query, RunLog.class);
        logger.info("sql:::{}", query.toString());

        return datas;
    }

}
