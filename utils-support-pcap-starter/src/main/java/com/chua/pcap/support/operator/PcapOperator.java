package com.chua.pcap.support.operator;


import org.pcap4j.core.PcapNetworkInterface;

import java.util.List;

/**
 * 操作器
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/20
 */
public interface PcapOperator {
    /**
     * 获取网卡
     *
     * @param name 网卡名称
     * @return 网卡
     * @throws Exception Exception
     */
    PcapNetworkInterface selectNetwork(String name) throws Exception;

    /**
     * 报文长度
     *
     * @param messageLength 报文长度
     */
    void setMessageLength(int messageLength);

    /**
     * 报文长度
     *
     * @return int
     */
    int getMessageLength();

    /**
     * 抓包次数
     *
     * @param packetCount 抓包次数
     */
    void setPacketCount(int packetCount);

    /**
     * 抓包次数
     *
     * @return int
     */
    int getPacketCount();

    /**
     * 报文模式
     *
     * @param promiscuousMode 模式
     */
    void setPromiscuousMode(String promiscuousMode);

    /**
     * 报文模式
     *
     * @return String
     */
    String getPromiscuousMode();

    /**
     * 报文超时时间
     *
     * @param timeout
     */
    void setTimeout(int timeout);

    /**
     * 报文超时时间
     *
     * @return int
     */
    int getTimeout();

    /**
     * 获取网卡信息
     *
     * @return 网卡信息
     * @throws Exception Exception
     */
    List<String> getInterfaceNetworks() throws Exception;
}
