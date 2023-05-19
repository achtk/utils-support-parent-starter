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
        ServiceDiscovery serviceDiscovery = ServiceProvider.of(ServiceDiscovery.class).getExtension("multicast");
        serviceDiscovery.start(new DiscoveryOption().setAddress("224.2.2.4:6379"));
        serviceDiscovery.register(Discovery.builder().discovery("/danbin").address("http://qrtest.qirenit.com:81").build());
        serviceDiscovery.register(Discovery.builder().discovery("/danbin").address("http://qrtest.qirenit.com:82").build());
        serviceDiscovery.register(Discovery.builder().discovery("/webrtc").address("https://webrtc.tzbyte.com:6443").build());

        Discovery discovery1 = serviceDiscovery.discovery("danbin");
        Discovery discovery2 = serviceDiscovery.discovery("danbin");
        Discovery discovery3 = serviceDiscovery.discovery("webrtc");
        System.out.println();
        while (true) {
            ThreadUtils.sleepSecondsQuietly(1);
        }
    }
}
