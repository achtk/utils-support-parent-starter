package com.chua.example.gateway;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.MulticastServiceDiscovery;
import com.chua.common.support.discovery.ServiceDiscovery;

import java.io.IOException;

/**
 * @author CH
 */
public class DemoServer {

    public static void main(String[] args) throws IOException {
        ServiceDiscovery serviceDiscovery = new MulticastServiceDiscovery(new DiscoveryOption().setAddress("224.0.0.1:2111"));
        serviceDiscovery.start();
        serviceDiscovery.registerService("/", Discovery.builder()
                .ip("127.0.0.1")
                .port(5173)
                        .uriSpec("/")
                .build());

    }
}
