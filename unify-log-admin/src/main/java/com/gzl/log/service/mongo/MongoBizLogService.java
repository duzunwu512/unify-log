package com.gzl.log.service.mongo;

import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.BizLog;
import com.gzl.log.repository.mongo.MongoBizLogDao;
import com.gzl.log.service.BizLogService;
import com.gzl.log.util.DateUtil;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.BizLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
public class MongoBizLogService implements BizLogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MongoBizLogDao mongoBizLogDao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Iterable<BizLog> findAll() {
        return mongoBizLogDao.findAll();
    }

    @Override
    public BizLog findById(String id) {
        Optional<BizLog> optionalUser = mongoBizLogDao.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    public List<BizLog> findByBizId(String bizId) {
        Optional<List<BizLog>> optionalUser = Optional.ofNullable(mongoBizLogDao.findByBizId(bizId));
        return optionalUser.orElse(null);
    }

    @Override
    public BizLog insert(BizLog optLog) {
        return mongoBizLogDao.insert(optLog);
    }

    @Override
    public void insert(List<BizLog> optLogs) {
        mongoBizLogDao.insert(optLogs);
    }

    @Override
    public void delete(String id) {
        Optional<BizLog> optional = mongoBizLogDao.findById(id);
        if (!optional.isPresent()) {
            return;
        }
        mongoBizLogDao.delete(optional.get());
    }

    @Override
    public void delete(String ids[]){
        Query query = Query.query(Criteria.where("id").in(ids));
        mongoTemplate.remove(query, BizLog.class);
    }

    @Override
    public boolean update(BizLogVo vo) {
        boolean rslt = false;
        if(null!=vo && !StringUtils.isEmpty(vo.getId())){
            Optional<BizLog> optional = mongoBizLogDao.findById(vo.getId());
            if (optional.isPresent()) {
                BizLog bizLog = optional.get();
                BeanUtils.copyProperties(vo, bizLog);
                mongoBizLogDao.save(bizLog);
                rslt= true;
            }
        }
        return rslt;
    }


    public PageInfo<BizLog> page(BizLogVo bizLog, Pageable pageable) {
        if(pageable==null){
            pageable = PageRequest.of(0, 10,Sort.Direction.DESC,"createDate");
        }

        Query query = new Query();

        //Criteria ct = new Criteria();
        if(!StringUtils.isEmpty(bizLog.getCreaterId())){
            //ct.and("createrId").is(bizLog.getCreaterId());
            query.addCriteria(Criteria.where("createrId").is(bizLog.getCreaterId()));
        }
        if(!StringUtils.isEmpty(bizLog.getCreaterUserName())){
            //ct.and("createrUserName").is(bizLog.getCreaterUserName());
            query.addCriteria(Criteria.where("createrUserName").is(bizLog.getCreaterUserName()));
        }
        if(!StringUtils.isEmpty(bizLog.getCreaterRealName())){
            //ct.and("createrRealName").regex("^.*"+bizLog.getCreaterRealName()+".*$");
            query.addCriteria(Criteria.where("createrRealName").regex("^.*"+bizLog.getCreaterRealName()+".*$"));
        }
        if(!StringUtils.isEmpty(bizLog.getBizId())){
            query.addCriteria(Criteria.where("bizId").is(bizLog.getBizId()));
        }
        if(!StringUtils.isEmpty(bizLog.getBusiness())){
            //ct.and("business").is(bizLog.getBusiness());
            query.addCriteria(Criteria.where("business").is(bizLog.getBusiness()));
        }
        if(!StringUtils.isEmpty(bizLog.getContent())){
            //ct.and("content").regex("^.*"+bizLog.getContent()+".*$");
            query.addCriteria(Criteria.where("content").regex("^.*"+bizLog.getContent()+".*$"));
        }
        if(!StringUtils.isEmpty(bizLog.getCreateDeptId())){
            //ct.and("createDeptId").is(bizLog.getCreateDeptId());
            query.addCriteria(Criteria.where("createDeptId").is(bizLog.getCreateDeptId()));
        }
        if(!StringUtils.isEmpty(bizLog.getCreaterDeptName())){
            //ct.and("createrDeptName").regex("^.*"+bizLog.getCreaterDeptName()+".*$");
            query.addCriteria(Criteria.where("createrDeptName").regex("^.*"+bizLog.getCreaterDeptName()+".*$"));
        }
        if(!StringUtils.isEmpty(bizLog.getOperateType())){
            //ct.and("operateType").is(bizLog.getOperateType());
            query.addCriteria(Criteria.where("operateType").is(bizLog.getOperateType()));
        }
        if(!StringUtils.isEmpty(bizLog.getCompanyId())){
            //ct.and("companyId").is(bizLog.getCompanyId());
            query.addCriteria(Criteria.where("companyId").is(bizLog.getCompanyId()));
        }
        if(!StringUtils.isEmpty(bizLog.getCompanyName())){
            //ct.and("companyName").regex("^.*"+bizLog.getCompanyName()+".*$");
            query.addCriteria(Criteria.where("companyName").regex("^.*"+bizLog.getCompanyName()+".*$"));
        }

        if(!StringUtils.isEmpty(bizLog.getStartDate()) && !StringUtils.isEmpty(bizLog.getEndDate())){
            query.addCriteria(Criteria.where("createDate").gte(DateUtil.parseDate(bizLog.getStartDate(), DateUtil.LONG_FORMAT) ).lte(DateUtil.parseDate(bizLog.getEndDate(), DateUtil.LONG_FORMAT) ));
        }else if(!StringUtils.isEmpty(bizLog.getStartDate())){
            query.addCriteria(Criteria.where("createDate").gte(DateUtil.parseDate(bizLog.getStartDate(), DateUtil.LONG_FORMAT) ));
        }else  if(!StringUtils.isEmpty(bizLog.getEndDate())){
            query.addCriteria(Criteria.where("createDate").lte(DateUtil.parseDate(bizLog.getEndDate(), DateUtil.LONG_FORMAT) ));
        }

        long total = mongoTemplate.count(query, BizLog.class);
        query.skip(pageable.getPageSize()*(pageable.getPageNumber()-1));
        query.limit(pageable.getPageSize());

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "createDate"));
        query.with(Sort.by(orders));

        List<BizLog> datas = mongoTemplate.find(query, BizLog.class);
        PageInfo<BizLog> page = new PageInfo<BizLog>(pageable.getPageNumber(), pageable.getPageSize(), total,  datas);

        return page;
    }

}
