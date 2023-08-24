package com.chua.common.support.message;

import com.chua.common.support.eventbus.EventbusProvider;
import com.chua.common.support.extra.api.MessageRequest;
import com.chua.common.support.extra.api.MessageResponse;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.utils.IoUtils;

import java.util.Set;

/**
 * 内部消息推送
 *
 * @author CH
 */
public class SubscribeMessageSender implements MessageSender{

    private final EventbusProvider provider;

    public SubscribeMessageSender(EventbusProvider provider) {
        this.provider = provider;
    }

    @Override
    public MessageResponse send(MessageRequest request) {
        Set<String> strings = Splitter.on(",").omitEmptyStrings().trimResults().splitToSet(request.toUser());
        for (String string : strings) {
            provider.post(string, request.data());
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(provider);
    }
}
