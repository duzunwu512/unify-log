package com.gzl.log.service.elasticsearch;

import com.gzl.log.common.Constants;
import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.entity.BizLog;
import com.gzl.log.repository.elasticsearch.ElasticsearchBizLogDao;
import com.gzl.log.service.BizLogService;
import com.gzl.log.util.DateUtil;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.BizLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(ElasticsearchConfig.class)
public class ElasticsearchBizLogService implements BizLogService , InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ElasticsearchBizLogDao elasticsearchBizLogDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public Iterable<BizLog> findAll() {
        return elasticsearchBizLogDao.findAll();
    }

    @Override
    public BizLog findById(String id) {
        Optional<BizLog> optionalUser = elasticsearchBizLogDao.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    public List<BizLog> findByBizId(String bizId) {
        Optional<List<BizLog>> optionalUser = Optional.ofNullable(elasticsearchBizLogDao.findByBizId(bizId));
        return optionalUser.orElse(null);
    }

    @Override
    public BizLog insert(BizLog optLog) {
        return elasticsearchBizLogDao.save(optLog);
    }

    @Override
    public void insert(List<BizLog> bizLogs) {
        elasticsearchBizLogDao.saveAll(bizLogs);
    }

    @Override
    public void delete(String id) {
        Optional<BizLog> optional = elasticsearchBizLogDao.findById(id);
        if (!optional.isPresent()) {
            return;
        }
        elasticsearchBizLogDao.delete(optional.get());
    }

    @Override
    public void delete(String ids[]){

        //elasticsearchBizLogDao.deleteAll(elasticsearchBizLogDao.findAllById(Arrays.asList(ids)));

        Criteria criteria = new Criteria("id").in(ids);
        Query query = new CriteriaQuery(criteria);
        elasticsearchTemplate.delete(query, BizLog.class, IndexCoordinates.of(Constants.ES_BIZ_LOG_INDEX));
    }

    @Override
    public boolean update(BizLogVo vo) {
        boolean rslt = false;
        if(null!=vo && !StringUtils.isEmpty(vo.getId())){
            Optional<BizLog> optional = elasticsearchBizLogDao.findById(vo.getId());
            if (optional.isPresent()) {
                BizLog bizLog = optional.get();
                BeanUtils.copyProperties(vo, bizLog);
                elasticsearchBizLogDao.save(bizLog);
                rslt= true;
            }
        }
        return rslt;
    }


    @Override
    public PageInfo<BizLog> page(BizLogVo bizLog, Pageable pageable) {
        if(pageable==null){
            pageable = PageRequest.of(0, 10,Sort.Direction.DESC,"createDate");
        }

        Criteria ct = new Criteria();
        if(null!=bizLog){
            if(!StringUtils.isEmpty(bizLog.getCreaterId())){
                ct.and(Criteria.where("createrId").is(bizLog.getCreaterId()));
            }
            if(!StringUtils.isEmpty(bizLog.getCreaterUserName())){
                ct.and(Criteria.where("createrUserName").is(bizLog.getCreaterUserName()));
            }
            if(!StringUtils.isEmpty(bizLog.getCreaterRealName())){
                ct.and(Criteria.where("createrRealName").is(bizLog.getCreaterRealName()));
            }
            if(!StringUtils.isEmpty(bizLog.getBizId())){
                ct.and(Criteria.where("bizId").is(bizLog.getBizId()));
            }
            if(!StringUtils.isEmpty(bizLog.getBusiness())){
                ct.and(Criteria.where("business").is(bizLog.getBusiness()));
            }
            if(!StringUtils.isEmpty(bizLog.getContent())){
                ct.and(Criteria.where("content").contains(bizLog.getContent()));
            }
            if(!StringUtils.isEmpty(bizLog.getCreateDeptId())){
                ct.and(Criteria.where("createDeptId").is(bizLog.getCreateDeptId()));
            }
            if(!StringUtils.isEmpty(bizLog.getCreaterDeptName())){
                ct.and(Criteria.where("createrDeptName").startsWith(bizLog.getCreaterDeptName()));
            }
            if(!StringUtils.isEmpty(bizLog.getOperateType())){
                ct.and(Criteria.where("operateType").is(bizLog.getOperateType()));
            }
            if(!StringUtils.isEmpty(bizLog.getCompanyId())){
                ct.and(Criteria.where("companyId").is(bizLog.getCompanyId()));
            }
            if(!StringUtils.isEmpty(bizLog.getCompanyName())){
                ct.and(Criteria.where("companyName").startsWith(bizLog.getCompanyName()));
            }

            if(!StringUtils.isEmpty(bizLog.getStartDate()) && !StringUtils.isEmpty(bizLog.getEndDate())){
                ct.and(Criteria.where("createDate").greaterThanEqual(DateUtil.parseDate(bizLog.getStartDate(), DateUtil.LONG_FORMAT) ).lessThanEqual(DateUtil.parseDate(bizLog.getEndDate(), DateUtil.LONG_FORMAT) ));
            }else if(!StringUtils.isEmpty(bizLog.getStartDate())){
                ct.and(Criteria.where("createDate").greaterThanEqual(DateUtil.parseDate(bizLog.getStartDate(), DateUtil.LONG_FORMAT) ));
            }else  if(!StringUtils.isEmpty(bizLog.getEndDate())){
                ct.and(Criteria.where("createDate").lessThanEqual(DateUtil.parseDate(bizLog.getEndDate(), DateUtil.LONG_FORMAT) ));
            }
        }

        long total = elasticsearchTemplate.count(new CriteriaQuery(ct), BizLog.class, IndexCoordinates.of(Constants.ES_BIZ_LOG_INDEX));

        SearchHits<BizLog> datas = elasticsearchTemplate.search(new CriteriaQuery(ct, pageable), BizLog.class, IndexCoordinates.of(Constants.ES_BIZ_LOG_INDEX));

        List<BizLog> list = new ArrayList<>(pageable.getPageSize());
        List<SearchHit<BizLog>> ll = datas.getSearchHits();
        for(SearchHit<BizLog> s: ll){
            list.add(s.getContent());
        }
        PageInfo<BizLog> page = new PageInfo<BizLog>(pageable.getPageNumber(), pageable.getPageSize(), total,  list);
        return page;
    }
   @Override
   public void afterPropertiesSet() throws Exception {
       System.out.println("-----------ElasticsearchBizLogService start---------------------");
   }
}
