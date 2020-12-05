package com.gzl.log.elasticsearch;

import com.gzl.log.LogApplication;
import com.gzl.log.entity.Conference;
import com.gzl.log.repository.elasticsearch.ConferenceRepository;
import com.gzl.log.repository.elasticsearch.ElasticsearchBizLogDao;
import com.gzl.log.repository.elasticsearch.ElasticsearchRunLogDao;
import com.gzl.log.service.elasticsearch.ElasticsearchBizLogService;
import com.gzl.log.service.elasticsearch.ElasticsearchRunLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case to show Spring Data Elasticsearch functionality.
 *
 * @author WHP
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogApplication.class)
public class ElasticsearchRunLogServiceTest {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    ElasticsearchRunLogService elasticsearchRunLogService;

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    private ElasticsearchRunLogDao elasticsearchRunLogDao;


    @PreDestroy
    public void deleteIndex() {
        System.out.println("PreDestroy...........");
        operations.indexOps(Conference.class).delete();
    }

    @Test
    public void textSearch() throws ParseException {
        System.out.println("textSearch...........");
        String expectedDate = "2014-10-29";
        String expectedWord = "java";
        CriteriaQuery query = new CriteriaQuery(
                new Criteria("keywords").contains(expectedWord).and(new Criteria("date").greaterThanEqual(expectedDate)));

        SearchHits<Conference> result = operations.search(query, Conference.class, IndexCoordinates.of("conference-index"));

        assertThat(result).hasSize(3);

        for (SearchHit<Conference> conference : result) {
            assertThat(conference.getContent().getKeywords()).contains(expectedWord);
            assertThat(format.parse(conference.getContent().getDate())).isAfter(format.parse(expectedDate));
        }
    }

    @Test
    public void geoSpatialSearch() {
        System.out.println("geoSpatialSearch...........");
        GeoPoint startLocation = new GeoPoint(50.0646501D, 19.9449799D);
        String range = "330mi"; // or 530km
        CriteriaQuery query = new CriteriaQuery(new Criteria("location").within(startLocation, range));

        SearchHits<Conference> result = operations.search(query, Conference.class, IndexCoordinates.of("conference-index"));

        assertThat(result).hasSize(2);
    }
}