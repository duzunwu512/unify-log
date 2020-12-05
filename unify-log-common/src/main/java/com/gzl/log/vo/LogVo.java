package com.gzl.log.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class LogVo implements Serializable {
    private String id;
    private Date createDate;

    private String startDate;

    private String endDate;

    LogVo(String id){
        this();
        this.id = id;
    }

    LogVo(){
        this.createDate = new Date();
    }
}
