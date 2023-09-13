package com.chua.example.gateway;

import com.chua.proxy.support.Gateway;
import com.chua.proxy.support.config.ProxyConfig;

/**
 * 网关示例
 *
 * @author CH
 * @since 2023/09/13
 */
public class GatewayExample {

    public static void main(String[] args) throws InterruptedException {
        Gateway gateway = new Gateway(ProxyConfig.builder().port(3333).build());
        gateway.start();
    }
}
