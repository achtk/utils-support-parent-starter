package com.chua.pcap.support.template;


import com.chua.pcap.support.packet.Packet;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * pcap模板
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/20
 */
public interface PcapTemplate extends AutoCloseable {
    /**
     * 发送包
     *
     * @param rawData 数据
     * @throws Exception Exception
     */
    void sendPacket(byte[] rawData) throws Exception;

    /**
     * 发送包
     *
     * @param packet 包
     * @throws Exception Exception
     */
    void sendPacket(org.pcap4j.packet.Packet packet) throws Exception;

    /**
     * 打开dump
     *
     * @param filter  过滤表达式
     * @param file    dump
     * @param matcher 匹配器
     * @throws Exception Exception
     */
    void dumpOpen(String filter, File file, Consumer<Packet> matcher) throws Exception;

    /**
     * 打开dump
     *
     * @param file    dump
     * @param matcher 匹配器
     * @throws Exception Exception
     */
    default void dumpOpen(File file, Consumer<Packet> matcher) throws Exception {
        dumpOpen("", file, matcher);
    }

    /**
     * 过滤信息
     *
     * @param dumpFile dump文件
     * @throws Exception Exception
     */
    default void dumpSave(File dumpFile) throws Exception {
        dumpSave("", dumpFile);
    }

    /**
     * 过滤信息
     *
     * @param filter   过滤表达式
     * @param dumpFile dump文件
     * @throws Exception Exception
     */
    void dumpSave(String filter, File dumpFile) throws Exception;

    /**
     * 过滤信息
     *
     * @param filter  过滤表达式
     * @param matcher 匹配器
     * @throws Exception Exception
     */
    void filter(String filter, Consumer<Packet> matcher) throws Exception;

    /**
     * 过滤信息
     *
     * @param matcher 匹配器
     * @throws Exception Exception
     */
    default void filter(Consumer<Packet> matcher) throws Exception {
        filter("", matcher);
    }

    /**
     * 获取网卡信息
     *
     * @return 网卡信息
     * @throws Exception Exception
     */
    List<String> getInterfaceNetworks() throws Exception;

}
