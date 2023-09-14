package com.chua.common.support.net.channel.limit;

import lombok.Data;

/**
 * 限流
 * @author CH
 */
@Data
public class LimitConfig {

    /**
     * 名称
     */
    private String name = "token";

    private double num = 10D;
}
