package com.chua.common.support.crawler.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 包
 *
 * @author CH
 * @since 2021-11-23
 */
@Getter
@Setter
public class Packet {
    /**
     * 消息头
     */
    private byte[] headerRawData;
    /**
     * 报文
     */
    private byte[] payloadRawData;
    /**
     * 消息头
     */
    private String headerData;
    /**
     * 报文
     */
    private String payloadData;
    /**
     * 消息头
     */
    private byte[] packetHeaderRawData;
    /**
     * 报文
     */
    private byte[] packetPayloadRawData;
    /**
     * 消息头
     */
    private String packetHeaderData;
    /**
     * 报文
     */
    private String packetPayloadData;
    /**
     * 原地址
     */
    private String source;
    /**
     * 目标地址
     */
    private String target;
    /**
     * 协议
     */
    private String protocol;
    /**
     * ttl
     */
    private int ttl;
    /**
     * 消息
     */
    private String message;

    @Override
    public String toString() {
        return message;
    }
}
