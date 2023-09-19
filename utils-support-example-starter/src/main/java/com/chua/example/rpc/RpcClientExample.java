package com.chua.example.rpc;

import com.chua.common.support.rpc.RpcClient;
import com.chua.common.support.rpc.RpcRegistryConfig;

import java.io.IOException;

/**
 * @author CH
 */
public class RpcClientExample {

    public static void main(String[] args) throws IOException {
        RpcClient rpcClient = RpcClient.createClient("zbus",
                RpcRegistryConfig.createRegister("zookeeper://127.0.0.1:2181"),
                "rpc"
        );

        RpcService rpcService = rpcClient.get(RpcService.class);
        System.out.println(rpcService.uuid());
    }
}
