package com.gzl.log.entity;

import com.gzl.log.common.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data
@ToString(callSuper = false)
//@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document
@org.springframework.data.elasticsearch.annotations.Document(
        indexName = Constants.ES_RUN_LOG_INDEX, shards = 5, replicas = 2, refreshInterval = "-1")
@TypeAlias("runLog")
public class RunLog {

    @Id
    private String id;

    @ApiModelProperty(value = "应用名称或id")
    private String app;

    @ApiModelProperty(value = "应用节点")
    private String nodeIp;

    @ApiModelProperty(value = "请求ID")
    private String requestId;

    @ApiModelProperty(value = "日志生成日期")
    private Date createDate;

    @ApiModelProperty(value = "日志级别")
    private String level;

    @ApiModelProperty(value = "日志内容")
    private String content;

    @ApiModelProperty(value = "是否有异常信息栈 true or false")
    private boolean hasStackTrace = false;

    @ApiModelProperty(value = "异常信息栈")
    private String throwable;

    @ApiModelProperty(value = "线程名称")
    private String thread;

    @ApiModelProperty(value = "打印日志的行数")
    private String lineNumber;

    @ApiModelProperty(value = "log名称")
    private String logger;

    public RunLog(String id){
        this.id = id;
        this.createDate = new Date();
    }

    public RunLog(){
        this(UUID.randomUUID().toString().replace("-", ""));
    }

}
