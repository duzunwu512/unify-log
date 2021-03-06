package com.gzl.log.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import lombok.Builder;
import lombok.Data;

/**
 * @author Artur Konczak
 * @author Oliver Gierke
 * @auhtor Christoph Strobl
 */
@Data
@Builder
@Document(indexName = "conference-index", shards = 5, replicas = 2,
        refreshInterval = "-1")
public class Conference {


    private @Id String id;
    private String name;
    private @Field(type = Date) String date;
    private GeoPoint location;
    private List<String> keywords;

    // do not remove it
    public Conference() {}

    // do not remove it - work around for lombok generated constructor for all params
    public Conference(String id, String name, String date, GeoPoint location, List<String> keywords) {

        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.keywords = keywords;
    }
}
