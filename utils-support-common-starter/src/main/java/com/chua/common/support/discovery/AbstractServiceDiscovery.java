package com.chua.common.support.discovery;


/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public abstract class AbstractServiceDiscovery implements ServiceDiscovery {

    protected DiscoveryOption discoveryOption;

    public AbstractServiceDiscovery(DiscoveryOption discoveryOption) {
        this.discoveryOption = discoveryOption;
        afterPropertiesSet();
    }
}
