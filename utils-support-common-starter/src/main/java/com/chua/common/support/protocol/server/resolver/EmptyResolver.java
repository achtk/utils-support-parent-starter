package com.chua.common.support.protocol.server.resolver;

import com.chua.common.support.annotations.SpiIgnore;

/**
 * 解析器
 * @author CH
 */
@SpiIgnore
public class EmptyResolver implements Resolver{
    static final EmptyResolver INSTANCE = new EmptyResolver();

    public static EmptyResolver empty() {
        return INSTANCE;
    }

    @Override
    public byte[] resolve(Object obj) {
        return new byte[0];
    }

    @Override
    public String getContentType() {
        return "text/pain";
    }
}
