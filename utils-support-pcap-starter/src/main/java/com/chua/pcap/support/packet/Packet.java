package com.chua.pcap.support.packet;

import lombok.Data;

import java.util.List;


/**
 * 包
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/20
 */
@Data
public class Packet {
    /**
     * 原始包
     */
    private org.pcap4j.packet.Packet originalPackage;

    private int originalLength;
    /**
     * 报文
     */
    private byte[] rawData;
    /**
     * 报文头
     */
    private Header header;

    private List<Payload> payloads;

    /**
     *
     */
    @Data
    public static class Header {
        byte[] rawData;
        int length;
    }

    /**
     *
     */
    @Data
    public static class Payload {
        byte[] rawData;
        int length;
        String[] hexData;

        @Override
        public String toString() {
            if (null == hexData) {
                return "Payload{}";
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String datum : hexData) {
                stringBuilder.append(" ").append(datum);
            }
            return stringBuilder.substring(1);
        }
    }

    @Override
    public String toString() {
        return originalLength + "";
    }
}
