package com.chua.example.discovery;

import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ThreadUtils;

/**
 * 发现服务例子
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public class ServiceDiscoveryExample {

    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = ServiceProvider.of(ServiceDiscovery.class).getNewExtension("multicast", new DiscoveryOption().setAddress("224.2.2.4:6379"));
        serviceDiscovery.start();
        serviceDiscovery.registerService("/danbin", Discovery.builder().build());
        serviceDiscovery.registerService("/danbin", Discovery.builder().build());
        serviceDiscovery.registerService("/webrtc", Discovery.builder().build());

        System.out.println();
        while (true) {
            Discovery discovery1 = serviceDiscovery.getService("danbin");
            Discovery discovery2 = serviceDiscovery.getService("danbin");
            Discovery discovery3 = serviceDiscovery.getService("webrtc");
            System.out.println(discovery1);
            System.out.println(discovery2);
            System.out.println(discovery3);
            ThreadUtils.sleepSecondsQuietly(1);
        }
    }
}
