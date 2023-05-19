package com.chua.nacos.support;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

/**
 * nacos config
 * @author CH
 */
public class NacosConfigClientProvider extends AbstractClientProvider<ConfigService> {

    public NacosConfigClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return NacosConfigClient.class;
    }


    public static class NacosConfigClient extends AbstractClient<ConfigService> {

        private ConfigService configService;

        protected NacosConfigClient(ClientOption clientOption) {
            super(clientOption);
        }

        @Override
        public ConfigService getClient() {
            return configService;
        }

        @Override
        public void closeClient(ConfigService client) {
            try {
                client.shutDown();
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public void connectClient() {
            try {
                this.configService = NacosFactory.createConfigService(url);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() {
            try {
                configService.shutDown();
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterPropertiesSet() {

        }
    }
}
