package com.gzl.log.entity;

import com.gzl.log.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.UUID;

@org.springframework.data.mongodb.core.mapping.Document
@org.springframework.data.elasticsearch.annotations.Document(
        indexName = Constants.ES_BIZ_LOG_INDEX, shards = 5, replicas = 2, refreshInterval = "1s")
@Data
@ToString(callSuper = false)
//@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("bizLog")
public class BizLog {

    @Id
    private String id;

    //create user id
    private String createrId;

    //creater username
    private String createrUserName;

    //creater realName
    private String createrRealName;

    private Date createDate;

    //业务
    private String business;

    //业务id
    private String bizId;

    //日志内容
    private String content;

    //操作人所属部门ID
    private String createDeptId ;

    //操作人所属部门名称
    private String createrDeptName ;

    //操作类别
    private String operateType ;

    //更改人
    private String updateUserName;

    //更改日期
    private Date update;

    //所在（子）公司ID
    private String companyId;

    //所在（子）公司名称
    private String companyName;

    private Long version;

    public BizLog(){
        this(UUID.randomUUID().toString().replace("-", ""));
    }

    public BizLog(String id){
        this.id = id;
        this.version = 1l;
        this.createDate = new Date();
    }

}
