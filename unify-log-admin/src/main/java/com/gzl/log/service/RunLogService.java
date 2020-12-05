package com.gzl.log.service;

import com.gzl.log.entity.RunLog;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.RunLogVo;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RunLogService {

    public Iterable<RunLog> findAll();

    public RunLog findById(String id);

    public List<RunLog> findByApp(String app);

    public RunLog insert(RunLog optLog) ;

    public void insert(List<RunLog> optLogs) ;

    public void delete(String id) ;

    public RunLog update(RunLog mongoUser) ;

    public PageInfo<RunLog> page(RunLogVo runLogVo, Pageable pageable);

    public List<RunLog> page(RunLogVo runLogVo, long skip, int size) ;

}
