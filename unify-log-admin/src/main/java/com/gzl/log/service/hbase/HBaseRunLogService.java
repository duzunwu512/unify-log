package com.gzl.log.service.hbase;

import com.gzl.log.common.Constants;
import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.config.HBaseConfig;
import com.gzl.log.entity.RunLog;
import com.gzl.log.repository.elasticsearch.ElasticsearchRunLogDao;
import com.gzl.log.repository.hbase.HBaseRunLogDao;
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
@ConditionalOnBean(HBaseConfig.class)
public class HBaseRunLogService implements RunLogService, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HBaseRunLogDao hbaseRunLogDao;


     @Override
     public Iterable<RunLog> findAll() {
         return null;
     }

     @Override
     public RunLog findById(String id) {
         return null;
     }

     @Override
     public List<RunLog> findByApp(String app) {
         return null;
     }

     @Override
     public RunLog insert(RunLog optLog) {
         return null;
     }

     @Override
     public void insert(List<RunLog> optLogs) {

     }

     @Override
     public void delete(String id) {

     }

     @Override
     public RunLog update(RunLog mongoUser) {
         return null;
     }

     @Override
     public PageInfo<RunLog> page(RunLogVo runLogVo, Pageable pageable) {
         return null;
    }


    @Override
    public List<RunLog> page(RunLogVo runLogVo, long skip, int size) {
        return null;
    }

   @Override
   public void afterPropertiesSet() throws Exception {
       System.out.println("-----------ElasticsearchRunLogService start---------------------");
   }
}
