package com.chua.common.support.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基本接口配置
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseRpcInterfaceConfig extends BaseRpcConfig{

    /**
     * The interface name of the exported service
     */
    protected String interfaceName;

    /**
     * The classLoader of interface belong to
     */
    protected ClassLoader interfaceClassLoader;

    /**
     * The remote service version the customer/provider side will reference
     */
    protected String version;

    /**
     * The remote service group the customer/provider side will reference
     */
    protected String group;

}
