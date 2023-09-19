package com.chua.example.rpc;

import java.util.UUID;

/**
 * @author CH
 */
public class DemoRpcService implements RpcService{
    @Override
    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
