package com.gzl.log.vo;

import lombok.*;

import java.util.Date;

@Data
@ToString(callSuper = false)
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunLogVo extends LogVo{

    private String app;

    private String nodeIp;

    private String requestId;

    private String level;//日志级别

    private String content;

    private String throwable;//异常堆栈

    private boolean hasStackTrace = false;

    private String thread;//日志输出线程

    private String lineNumber;

    private String logger;//日志输出类名

    public RunLogVo(String id){
        super(id);
    }

    public RunLogVo(){
        super();
    }

}
