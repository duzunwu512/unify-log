package com.gzl.log.core.sender;

public interface MessageSender<T> {

    /**
     * 同步发送
     *
     * @param event
     * @return
     */
    boolean send(T event);

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();
}
