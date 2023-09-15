package com.chua.proxy.support.global;

import com.chua.proxy.support.config.HttpClientProperties;
import com.chua.proxy.support.ssl.HttpClientSslConfigurer;
import lombok.Data;

/**
 * @author CH
 */
@Data
public class GlobalConfig {

    public static final GlobalConfig INSTANCE = new GlobalConfig();

    private final HttpClientProperties properties = new HttpClientProperties();

    private final HttpClientProperties.Ssl sslProperties = new HttpClientProperties.Ssl();
    private final HttpClientSslConfigurer sslConfigurer = new HttpClientSslConfigurer(sslProperties, properties);


    /**
     * 获取实例
     *
     * @return {@link GlobalConfig}
     */
    public GlobalConfig getInstance() {
        return INSTANCE;
    }


}
