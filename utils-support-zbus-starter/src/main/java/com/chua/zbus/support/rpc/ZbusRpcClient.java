package com.chua.zbus.support.rpc;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.rpc.RpcClient;
import com.chua.common.support.rpc.RpcProtocolConfig;
import com.chua.common.support.rpc.RpcRegistryConfig;
import com.chua.common.support.utils.IoUtils;
import org.zbus.broker.ZbusBroker;
import org.zbus.net.http.Message;
import org.zbus.rpc.RpcFactory;
import org.zbus.rpc.mq.MqInvoker;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * dubbo-rpc客户端
 *
 * @author CH
 */
@Spi("zbus")
@SuppressWarnings("ALL")
public class ZbusRpcClient implements RpcClient {

    private final ZbusBroker broker;
    private final RpcFactory factory;
    private List<RpcRegistryConfig> rpcRegistryConfigs;
    private List<RpcProtocolConfig> rpcProtocolConfigs;
    private String name;

    private final AtomicBoolean state = new AtomicBoolean(false);
    public ZbusRpcClient(List<RpcRegistryConfig> rpcRegistryConfigs, String name) {
        this.rpcRegistryConfigs = rpcRegistryConfigs;
        this.name = name;
        try {
            this.broker = new ZbusBroker(rpcRegistryConfigs.stream()
                    .map(RpcRegistryConfig::getAddress)
                    .map(NetAddress::of)
                    .map(NetAddress::getAddress)
                    .collect(Collectors.joining(";")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message.MessageInvoker invoker = new MqInvoker(broker, name);
        this.factory = new RpcFactory(invoker);
    }


    @Override
    public <T> T get(Class<T> targetType) {
        try {
            return factory.getService(targetType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(broker);
    }
}
