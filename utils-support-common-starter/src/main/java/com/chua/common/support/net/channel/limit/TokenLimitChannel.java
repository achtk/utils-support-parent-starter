package com.chua.common.support.net.channel.limit;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.net.frame.Frame;

/**
 * 限流通道
 *
 * @author CH
 * @since 2023/09/13
 */
@Spi("token")
public class TokenLimitChannel implements LimitChannel {

    private LimitConfig limitConfig;

    public TokenLimitChannel(LimitConfig limitConfig) {
        this.limitConfig = limitConfig;
    }

    @Override
    public boolean tryAcquire(Frame frame) {
        return false;
    }
}