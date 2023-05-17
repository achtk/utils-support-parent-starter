package com.chua.pcap.support.listener;

import com.chua.common.support.monitor.session.Session;
import com.chua.pcap.support.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 会话
 *
 * @author CH
 */
public class PcapSession implements Session {
    @Getter
    private final byte[] rawData;
    @Getter
    private final Packet.Header header;
    @Getter
    private Packet packet;

    public PcapSession(Packet packet) {
        this.packet = packet;
        this.rawData = packet.getRawData();
        this.header = packet.getHeader();
    }

    @Override
    public List<Serializable[]> getBeforeData() {
        return null;
    }

    @Override
    public List<Serializable[]> getModifyData() {
        return null;
    }

    @Override
    public String root() {
        return null;
    }

    @Override
    public String change() {
        return null;
    }

    @Override
    public Session getSession() {
        return this;
    }

    @Override
    public String toString() {
        return packet.getOriginalPackage().toString();
    }
}
