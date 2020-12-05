package com.gzl.log.service.elasticsearch;

import com.gzl.log.common.Constants;
import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.entity.RunLog;
import com.gzl.log.repository.elasticsearch.ElasticsearchRunLogDao;
import com.gzl.log.service.RunLogService;
import com.gzl.log.util.DateUtil;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.RunLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(ElasticsearchConfig.class)
public class ElasticsearchRunLogService implements RunLogService, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ElasticsearchRunLogDao elasticsearchRunLogDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;

     @Override
     public Iterable<RunLog> findAll() {
         return elasticsearchRunLogDao.findAll();
     }

     @Override
     public RunLog findById(String id) {
         Optional<RunLog> optionalUser = elasticsearchRunLogDao.findById(id);
         return optionalUser.orElse(null);
     }

     @Override
     public List<RunLog> findByApp(String app) {
         Optional<List<RunLog>> optionalUser = Optional.ofNullable(elasticsearchRunLogDao.findByApp(app));
         return optionalUser.orElse(null);
     }

     @Override
     public RunLog insert(RunLog optLog) {
         return elasticsearchRunLogDao.save(optLog);
     }

     @Override
     public void insert(List<RunLog> optLogs) {
         elasticsearchRunLogDao.saveAll(optLogs);
     }

     @Override
     public void delete(String id) {
         Optional<RunLog> optional = elasticsearchRunLogDao.findById(id);
         if (!optional.isPresent()) {
             return;
         }
         elasticsearchRunLogDao.delete(optional.get());
     }

     @Override
     public RunLog update(RunLog mongoUser) {
         return elasticsearchRunLogDao.save(mongoUser);
     }

     @Override
     public PageInfo<RunLog> page(RunLogVo runLogVo, Pageable pageable) {
         if(pageable==null){
             pageable = PageRequest.of(0, 10,Sort.Direction.ASC,"createDate");
         }


        long total = elasticsearchTemplate.count(new CriteriaQuery(createCriteria(runLogVo)), RunLog.class, IndexCoordinates.of(Constants.ES_RUN_LOG_INDEX));
        //query.skip(pageable.getOffset());
        //query.limit(pageable.getPageSize());

        SearchHits<RunLog> datas = elasticsearchTemplate.search(new CriteriaQuery(createCriteria(runLogVo), pageable), RunLog.class, IndexCoordinates.of(Constants.ES_RUN_LOG_INDEX));
        List<RunLog> list = new ArrayList<>(pageable.getPageSize());

        List<SearchHit<RunLog>> ll = datas.getSearchHits();
        for(SearchHit<RunLog> s: ll){
            list.add(s.getContent());
        }

        PageInfo<RunLog> page = new PageInfo<RunLog>(pageable.getPageNumber(), pageable.getPageSize(), total,  list);
        return page;
    }

    private Criteria createCriteria(RunLogVo runLogVo){
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(runLogVo.getApp())){
            criteria.and(Criteria.where("app").is(runLogVo.getApp()));
        }
        if(!StringUtils.isEmpty(runLogVo.getLevel())){
            criteria.and(Criteria.where("level").is(runLogVo.getLevel()));
        }
        if(!StringUtils.isEmpty(runLogVo.getNodeIp())){
            criteria.and(Criteria.where("nodeIp").contains(runLogVo.getNodeIp()));
        }
        if(!StringUtils.isEmpty(runLogVo.getContent())){
            criteria.and(Criteria.where("content").contains(runLogVo.getContent()));
        }

        if(!StringUtils.isEmpty(runLogVo.getStartDate()) && !StringUtils.isEmpty(runLogVo.getEndDate())){
            criteria.and(Criteria.where("createDate").greaterThanEqual(DateUtil.parseDate(runLogVo.getStartDate(), DateUtil.LONG_FORMAT) ).lessThanEqual(DateUtil.parseDate(runLogVo.getEndDate(), DateUtil.LONG_FORMAT) ));
        }else if(!StringUtils.isEmpty(runLogVo.getStartDate())){
            criteria.and(Criteria.where("createDate").greaterThanEqual(DateUtil.parseDate(runLogVo.getStartDate(), DateUtil.LONG_FORMAT) ));
        }else  if(!StringUtils.isEmpty(runLogVo.getEndDate())){
            criteria.and(Criteria.where("createDate").lessThanEqual(DateUtil.parseDate(runLogVo.getEndDate(), DateUtil.LONG_FORMAT) ));
        }
        return  criteria;
    }

    @Override
    public List<RunLog> page(RunLogVo runLogVo, long skip, int size) {
        Pageable pageable = PageRequest.of(0, 10,Sort.Direction.ASC,"createDate");

        SearchHits<RunLog> datas = elasticsearchTemplate.search(new CriteriaQuery(createCriteria(runLogVo), pageable), RunLog.class, IndexCoordinates.of(Constants.ES_RUN_LOG_INDEX));
        List<RunLog> list = new ArrayList<>(pageable.getPageSize());

        List<SearchHit<RunLog>> ll = datas.getSearchHits();
        for(SearchHit<RunLog> s: ll){
            list.add(s.getContent());
        }

        return list;
    }

   @Override
   public void afterPropertiesSet() throws Exception {
       System.out.println("-----------ElasticsearchRunLogService start---------------------");
   }
}
