package com.chua.proxy.support.factory;

import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.request.WebServerRequest;
import com.chua.proxy.support.constant.Constants;
import com.chua.proxy.support.route.Route;

import java.io.IOException;

/**
 * 发现服务通道工厂
 *
 * @author CH
 * @since 2023/09/16
 */
public class DiscoveryServiceChannelFactory implements ChannelFactory {

    private final ServiceDiscovery serviceDiscovery;

    public DiscoveryServiceChannelFactory(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        try {
            serviceDiscovery.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handle(WebServerRequest request) {
        request.getAttribute().put(Constants.DISCOVERY, Route.create(serviceDiscovery.getService(request.uri())));
    }
}
