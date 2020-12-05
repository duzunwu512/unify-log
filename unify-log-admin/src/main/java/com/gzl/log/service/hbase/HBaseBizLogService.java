package com.gzl.log.service.hbase;

import com.gzl.log.common.Constants;
import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.config.HBaseConfig;
import com.gzl.log.entity.BizLog;
import com.gzl.log.repository.elasticsearch.ElasticsearchBizLogDao;
import com.gzl.log.repository.hbase.HBaseBizLogDao;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(HBaseConfig.class)
public class HBaseBizLogService implements BizLogService , InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //@Autowired
    //private HbaseOperations hbaseBizLogDao;


    @Override
    public Iterable<BizLog> findAll() {
        return null;
    }

    @Override
    public BizLog findById(String id) {
        return null;
    }

    @Override
    public List<BizLog> findByBizId(String bizId) {
        return null;
    }

    @Override
    public BizLog insert(BizLog optLog) {
        return null;
    }

    @Override
    public void insert(List<BizLog> bizLogs) {
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void delete(String ids[]){


    }

    @Override
    public boolean update(BizLogVo vo) {
       return true;
    }


    @Override
    public PageInfo<BizLog> page(BizLogVo bizLog, Pageable pageable) {
        return null;
    }
   @Override
   public void afterPropertiesSet() throws Exception {
       System.out.println("-----------HBaseBizLogService start---------------------");
   }
}
