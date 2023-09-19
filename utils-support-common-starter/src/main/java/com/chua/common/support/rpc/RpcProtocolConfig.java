package com.chua.common.support.rpc;

import com.chua.common.support.net.NetAddress;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * rpc协议配置
 *
 * @author CH
 */
@Data
public class RpcProtocolConfig {
    /**
     * Protocol name
     */
    private String name;

    /**
     * Service ip address (when there are multiple network cards available)
     */
    private String host;

    /**
     * Service port
     */
    private Integer port;

    /**
     * 创建协议
     *
     * @param url url
     * @return {@link List}<{@link RpcProtocolConfig}>
     */
    public static List<RpcProtocolConfig> createProtocol(String... url) {
        List<RpcProtocolConfig> rs = new ArrayList<>(url.length);
        for (String s : url) {
            NetAddress netAddress = NetAddress.of(s);
            RpcProtocolConfig rpcProtocolConfig = new RpcProtocolConfig();
            rpcProtocolConfig.setHost(netAddress.getHost());
            rpcProtocolConfig.setPort(netAddress.getPort());
            rpcProtocolConfig.setName(netAddress.getProtocol());

            rs.add(rpcProtocolConfig);
        }
        return rs;
    }
}
