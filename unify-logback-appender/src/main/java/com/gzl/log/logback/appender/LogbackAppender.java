package com.gzl.log.logback.appender;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.gzl.log.core.JSONEvent;
import com.gzl.log.core.UnifyAppender;
import com.gzl.log.core.sender.MessageSender;
import com.gzl.log.util.NetUtils;
import com.gzl.log.vo.RunLogVo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Date;
import java.util.List;
import java.util.Properties;

@Getter
@Setter
public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private MessageSender sender;

    private String appName = null;

    private String node = null;

    private HandleAppender handleAppender;

    public LogbackAppender() {
        super();
    }

    @Override
    protected void append(ILoggingEvent event) {
        System.out.println("LogbackAppender.append .................."+event.getMessage());
        if (event == null || event.getMessage() == null) {
            return;
        }
        if (sender != null) {
            handleAppender.handle(sender, event);
        }
    }

    @Override
    public void start() {
        addInfo("LogbackAppender---- 启动");

        sender.init();

        handleAppender = HandleAppender.builder().appName(this.appName).node(this.appName).build();

        if (StringUtils.isEmpty(node)) {
            List<String> ll = NetUtils.getLocalIPS();
            if(ll!=null && ll.size()>0){
                this.node = ll.toString();
                addError("LogbackAppender... node is null, will use default value: "+node);
            }
        }

        super.start();
    }

    @Override
    public void stop() {
        addInfo("LogbackAppender 停止");
        super.stop();
        if (sender != null) {
            sender.destroy();
        }
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
