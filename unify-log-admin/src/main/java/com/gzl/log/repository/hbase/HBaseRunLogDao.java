package com.gzl.log.repository.hbase;

import com.gzl.log.config.HBaseConfig;
import com.gzl.log.entity.RunLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
@ConditionalOnBean(HBaseConfig.class)
public class HBaseRunLogDao {



}
