package com.chua.common.support.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * rpc应用程序配置
 *
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcApplicationConfig extends BaseRpcConfig{

    /**
     * Application name
     */
    private String name;

    /**
     * The application version
     */
    private String version;


    /**
     * The type of the log access
     */
    private String logger;
}
