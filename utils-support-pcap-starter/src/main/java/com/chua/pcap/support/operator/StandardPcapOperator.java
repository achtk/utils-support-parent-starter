package com.chua.pcap.support.operator;

import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NetUtils;
import lombok.Getter;
import lombok.Setter;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * 标准操作器
 *
 * @author CH
 * @version 1.0.0
 * @since 2020/11/20
 */
@Getter
@Setter
public class StandardPcapOperator implements PcapOperator {

    private int messageLength = 65536;
    private int timeout = 10;
    private int packetCount = 1000;
    private static final String LOOP = "LoopBack Driver";
    private String promiscuousMode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS.name();


    @Override
    public PcapNetworkInterface selectNetwork(String name) throws Exception {
        if (null == name) {
            List<PcapNetworkInterface> devs = Pcaps.findAllDevs();
            PcapNetworkInterface random = null;

            for (PcapNetworkInterface dev : devs) {
                String description = dev.getDescription();
                if (description.contains(LOOP)) {
                    random = dev;
                    break;
                }
            }
            return null == random ? CollectionUtils.findFirst(devs) : random;
        }
        if (!NetUtils.isIpv4Host(name)) {
            PcapNetworkInterface devByName = Pcaps.getDevByName(name);
            if (null != devByName) {
                return devByName;
            }
        }
        Inet4Address inet4Address = (Inet4Address) Inet4Address.getByName(name);
        return Pcaps.getDevByAddress(inet4Address);
    }

    @Override
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    @Override
    public List<String> getInterfaceNetworks() throws Exception {
        List<PcapNetworkInterface> devs = Pcaps.findAllDevs();
        List<String> result = new ArrayList<>(devs.size());
        for (PcapNetworkInterface dev : devs) {
            result.add(dev.toString());
        }
        return result;
    }

}
