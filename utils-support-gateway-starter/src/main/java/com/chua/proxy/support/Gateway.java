package com.chua.proxy.support;

import com.chua.proxy.support.config.ProxyConfig;
import lombok.extern.slf4j.Slf4j;


/**
 * 代理服务器
 *
 * @author CH
 * @since 2023/09/13
 */
@Slf4j
public class Gateway extends AbstractProxyServer {

    public Gateway(ProxyConfig proxyConfig) {
        super(proxyConfig);
    }
}
