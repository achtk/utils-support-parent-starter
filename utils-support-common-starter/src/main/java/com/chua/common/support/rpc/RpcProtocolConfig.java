package com.chua.common.support.rpc;

import lombok.Data;

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
}
