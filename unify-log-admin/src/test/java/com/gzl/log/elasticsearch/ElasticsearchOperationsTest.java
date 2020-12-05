package com.gzl.log.elasticsearch;

import static org.assertj.core.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import com.gzl.log.LogApplication;
import com.gzl.log.entity.Conference;
import com.gzl.log.repository.elasticsearch.ConferenceRepository;
import org.junit.*;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.InitializingBean;
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

/**
 * Test case to show Spring Data Elasticsearch functionality.
 *
 * @author WHP
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogApplication.class)
public class ElasticsearchOperationsTest{

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    ElasticsearchOperations operations;
    @Autowired
    ConferenceRepository repository;


    @PreDestroy
    public void deleteIndex() {
        System.out.println("PreDestroy...........");
        operations.indexOps(Conference.class).delete();
    }

    @PostConstruct
    public void insertDataSample() {
        System.out.println("PostConstruct...........");
        repository.deleteAll();
        operations.indexOps(Conference.class).refresh();

        // Save data sample
        repository.save(Conference.builder().date("2014-11-06").name("Spring eXchange 2014 - London")
                .keywords(Arrays.asList("java", "spring")).location(new GeoPoint(51.500152D, -0.126236D)).build());
        repository.save(Conference.builder().date("2014-12-07").name("Scala eXchange 2014 - London")
                .keywords(Arrays.asList("scala", "play", "java")).location(new GeoPoint(51.500152D, -0.126236D)).build());
        repository.save(Conference.builder().date("2014-11-20").name("Elasticsearch 2014 - Berlin")
                .keywords(Arrays.asList("java", "elasticsearch", "kibana")).location(new GeoPoint(52.5234051D, 13.4113999))
                .build());
        repository.save(Conference.builder().date("2014-11-12").name("AWS London 2014")
                .keywords(Arrays.asList("cloud", "aws")).location(new GeoPoint(51.500152D, -0.126236D)).build());
        repository.save(Conference.builder().date("2014-10-04").name("JDD14 - Cracow")
                .keywords(Arrays.asList("java", "spring")).location(new GeoPoint(50.0646501D, 19.9449799)).build());
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