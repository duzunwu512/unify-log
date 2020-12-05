package com.gzl.log.logback.appender;

import ch.qos.logback.classic.spi.*;
import com.gzl.log.core.sender.MessageSender;
import com.gzl.log.vo.RunLogVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class HandleAppender {

    private String appName = null;

    private String node = null;

    public void handle(MessageSender sender, ILoggingEvent event){
        if (event == null || event.getMessage() == null) {
            return;
        }
        if (sender == null) {
            return;
        }

        String level = event.getLevel().toString();
        String loggerName = event.getLoggerName();
        String msg = event.getFormattedMessage();
        String threadName = event.getThreadName();
        String lineNumber = convert(event);

        IThrowableProxy tp = event.getThrowableProxy();
        RunLogVo vo = RunLogVo.builder()
                .app(appName)
                .level(level)
                .content(msg)
                .nodeIp(node)
                .thread(threadName)
                .logger(loggerName)
                .lineNumber(lineNumber)
                .hasStackTrace(tp!=null?true: false)
                .throwable(tp == null ? null : ThrowableProxyUtil.asString(tp))
                .build();

        sender.send(vo);
    }


    private String convert(ILoggingEvent le) {
        StackTraceElement[] cda = le.getCallerData();
        StringBuffer sb = new StringBuffer("");
        if (cda != null && cda.length > 0) {
            sb.append("[").append(cda[0].getFileName()).append(" ").append(cda[0].getLineNumber()).append("]");
            return sb.toString();
        } else {
            return CallerData.NA;
        }
    }
}
