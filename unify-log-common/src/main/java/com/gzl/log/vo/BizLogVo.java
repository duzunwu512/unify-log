package com.gzl.log.vo;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BizLogVo extends LogVo{

    //create user id
    private String createrId;

    //creater username
    private String createrUserName;

    //creater realName
    private String createrRealName;

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
    private String update;

    //所在（子）公司ID
    private String companyId;

    //所在（子）公司名称
    private String companyName;

    private Long version;

    public BizLogVo(){
        super();
    }

    public BizLogVo(String id){
        super(id);
    }


    @Override
    public String toString() {
        return "BizLog{" +
                "id='" + getId() + '\'' +
                ", createrId='" + createrId + '\'' +
                ", createrUserName='" + createrUserName + '\'' +
                ", createrRealName='" + createrRealName + '\'' +
                ", createDate=" + getCreateDate() +
                ", version=" + version +
                ", business='" + business + '\'' +
                ", bizId='" + bizId + '\'' +
                ", content='" + content + '\'' +
                ", createDeptId='" + createDeptId + '\'' +
                ", createrDeptName='" + createrDeptName + '\'' +
                ", operateType='" + operateType + '\'' +
                '}';
    }


}
