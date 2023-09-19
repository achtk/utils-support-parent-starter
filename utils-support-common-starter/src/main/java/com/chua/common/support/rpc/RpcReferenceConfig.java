package com.chua.common.support.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * rpc参考配置
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcReferenceConfig extends BaseRpcInterfaceConfig {


    /**
     * The interface class of the reference service
     */
    protected Class<?> interfaceClass;


    /**
     * The url for peer-to-peer invocation
     */
    protected String url;

    /**
     * The consumer config (default)
     */
    protected RpcConsumerConfig consumer;
}
