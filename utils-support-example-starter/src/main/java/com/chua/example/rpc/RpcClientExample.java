package com.chua.example.rpc;

import com.chua.common.support.rpc.RpcClient;
import com.chua.common.support.rpc.RpcRegistryConfig;

import java.io.IOException;

/**
 * @author CH
 */
public class RpcClientExample {

    public static void main(String[] args) throws IOException {
        RpcClient rpcClient = RpcClient.createClient("dubbo",
                RpcRegistryConfig.createRegister("nacos://127.0.0.1:8848"),
                "rpc-c"
        );

        RpcService rpcService = rpcClient.get(RpcService.class);
        System.out.println(rpcService.uuid());
    }
}
