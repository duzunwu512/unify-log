package com.gzl.log.service;

import com.gzl.log.entity.BizLog;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.BizLogVo;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BizLogService {

    public Iterable<BizLog> findAll() ;

    public BizLog findById(String id) ;

    public List<BizLog> findByBizId(String bizId);

    public BizLog insert(BizLog optLog) ;

    public void insert(List<BizLog> optLogs) ;

    public void delete(String id) ;

    public void delete(String ids[]);

    public boolean update(BizLogVo vo);

    public PageInfo<BizLog> page(BizLogVo bizLog, Pageable pageable) ;

}
