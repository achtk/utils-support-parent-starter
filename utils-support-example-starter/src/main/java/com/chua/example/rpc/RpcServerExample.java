package com.chua.example.rpc;

import com.chua.common.support.rpc.RpcProtocolConfig;
import com.chua.common.support.rpc.RpcRegistryConfig;
import com.chua.common.support.rpc.RpcServer;

import java.io.IOException;

/**
 * @author CH
 */
public class RpcServerExample {

    public static void main(String[] args) throws IOException {
        zbus(args);
    }

    public static void dubbo(String[] args) throws IOException {
        RpcServer rpcServer = RpcServer.createService("zbus",
                RpcRegistryConfig.createRegister("zookeeper://127.0.0.1:2181"),
                RpcProtocolConfig.createProtocol("dubbo://127.0.0.1:20880"),
                "rpc"
        );

        rpcServer.start();

        rpcServer.register(new DemoRpcService());
    }
    public static void zbus(String[] args) throws IOException {
        RpcServer rpcServer = RpcServer.createService("zbus",
                RpcRegistryConfig.createRegister("zookeeper://127.0.0.1:2181"),
                RpcProtocolConfig.createProtocol("dubbo://127.0.0.1:20880"),
                "rpc"
        );

        rpcServer.start();

        rpcServer.register(new DemoRpcService());
    }
}
