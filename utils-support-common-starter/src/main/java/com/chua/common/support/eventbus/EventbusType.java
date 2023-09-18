package com.chua.common.support.eventbus;

/**
 * 订阅类型
 *
 * @author CH
 * @version 1.0.0
 */
public enum EventbusType {
    /**
     * oracle
     */
    ORACLE,
    /**
     * mysql
     */
    BINLOG,
    /**
     * 本地
     */
    DEFAULT,
    /**
     * guava
     */
    GUAVA,
    /**
     * redis
     */
    REDIS,
    /**
     * kafka
     */
    KAFKA,
    /**
     * rabbit
     */
    RABBIT,
    /**
     * zbus
     */
    ZBUS
}
