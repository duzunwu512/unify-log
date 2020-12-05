package com.gzl.log.repository.hbase;

import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.config.HBaseConfig;
import com.gzl.log.entity.BizLog;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;


@Repository
@ConditionalOnBean(HBaseConfig.class)
public class HBaseBizLogDao {






}
