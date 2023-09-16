package com.chua.proxy.support.factory;

import com.chua.common.support.request.WebServerRequest;

import java.util.List;

/**
 * 渠道工厂链
 *
 * @author CH
 * @since 2023/09/16
 */
public interface ChannelFactoryChain {

    /**
     * do链
     *
     * @param request 要求
     */
    void doChain(WebServerRequest request);

    public class ChannelFactoryChainImpl implements ChannelFactoryChain {

        private int index = 0;
        private List<ChannelFactory> factories;

        public ChannelFactoryChainImpl(List<ChannelFactory> factories) {
            this.factories = factories;
        }

        @Override
        public void doChain(WebServerRequest request) {
            for (int i = 0; i < factories.size(); i++) {
                ChannelFactory channelFactory = factories.get(i);
                if (null != channelFactory) {
                    channelFactory.handle(request);
                }
            }
        }
    }
}
