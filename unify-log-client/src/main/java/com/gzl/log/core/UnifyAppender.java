package com.gzl.log.core;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;

/**
 * LogbackAppenderConfig
 *
 * @author Genesis
 * @since 1.0
 */
public interface UnifyAppender {
    /**
     * 日志来源应用
     */
    String appName = null;

    String node = null;
    /**
     * 字符编码
     */
    String charset = Consts.DEFAULT_CHARSET;


}
