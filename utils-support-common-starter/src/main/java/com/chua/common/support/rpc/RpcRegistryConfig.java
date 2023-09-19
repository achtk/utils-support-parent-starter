package com.chua.common.support.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author CH
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcRegistryConfig extends BaseRpcConfig{


    /**
     * Register center address
     */
    private String address;

    /**
     * Username to login register center
     */
    private String username;

    /**
     * Password to login register center
     */
    private String password;

    /**
     * Default port for register center
     */
    private Integer port;

    /**
     * Protocol for register center
     */
    private String protocol;

    /**
     * The region where the registry belongs, usually used to isolate traffics
     */
    private String zone;

    /**
     * The group that services registry in
     */
    private String group;

    private String version;

    /**
     * Connect timeout in milliseconds for register center
     */
    private Integer timeout;
    /**
     * Wait time before stop
     */
    private Integer wait;

    /**
     * Whether to check if register center is available when boot up
     */
    private Boolean check;

    /**
     * Whether to allow dynamic service to register on the register center
     */
    private Boolean dynamic;

    /**
     * Whether to allow exporting service on the register center
     */
    private Boolean register;

    /**
     * Whether to allow subscribing service on the register center
     */
    private Boolean subscribe;

    /**
     * The customized parameters
     */
    private Map<String, String> parameters;

    /**
     * Simple the registry. both useful for provider and consumer
     *
     * @since 2.7.0
     */
    private Boolean simplified;

    /**
     * Affects traffic distribution among registries, useful when subscribe to multiple registries
     * Take effect only when no preferred registry is specified.
     */
    private Integer weight;
}
