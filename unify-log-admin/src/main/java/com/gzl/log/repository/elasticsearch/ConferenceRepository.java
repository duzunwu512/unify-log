package com.gzl.log.repository.elasticsearch;

import com.gzl.log.config.ElasticsearchConfig;
import com.gzl.log.entity.Conference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Artur Konczak
 * @author Oliver Gierke
 */
@Repository
@ConditionalOnBean(ElasticsearchConfig.class)
public
interface ConferenceRepository extends ElasticsearchRepository<Conference, String> {}