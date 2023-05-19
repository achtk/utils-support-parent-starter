package com.chua.nacos.support;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;

/**
 * nacos name
 * @author CH
 */
public class NacosNamingClientProvider extends AbstractClientProvider<NamingService> {

    public NacosNamingClientProvider(ClientOption option) {
        super(option);
    }

    @Override
    protected Class<?> clientType() {
        return NacosNamingClient.class;
    }

    public static class NacosNamingClient extends AbstractClient<NamingService> {

        private NamingService namingService;

        protected NacosNamingClient(ClientOption clientOption) {
            super(clientOption);
        }

        @Override
        public NamingService getClient() {
            return namingService;
        }

        @Override
        public void closeClient(NamingService client) {
            try {
                client.shutDown();
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public void connectClient() {
            try {
                this.namingService = NacosFactory.createNamingService(url);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() {
            try {
                namingService.shutDown();
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterPropertiesSet() {

        }
    }
}
