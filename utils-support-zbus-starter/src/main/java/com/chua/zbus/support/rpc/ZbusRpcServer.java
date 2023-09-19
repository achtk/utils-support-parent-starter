package com.chua.zbus.support.rpc;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.rpc.RpcProtocolConfig;
import com.chua.common.support.rpc.RpcRegistryConfig;
import com.chua.common.support.rpc.RpcServer;
import com.chua.common.support.utils.ThreadUtils;
import org.zbus.broker.ZbusBroker;
import org.zbus.rpc.RpcProcessor;
import org.zbus.rpc.mq.Service;
import org.zbus.rpc.mq.ServiceConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * zbus rpc服务器
 *
 * @author CH
 */
@Spi("zbus")
public class ZbusRpcServer implements RpcServer {
    private final ZbusBroker broker;
    private List<RpcRegistryConfig> rpcRegistryConfigs;
    private List<RpcProtocolConfig> rpcProtocolConfigs;
    private String name;

    private ExecutorService runService = ThreadUtils.newFixedThreadExecutor(2, "rpc-server");
    RpcProcessor processor = new RpcProcessor();
    ServiceConfig config = new ServiceConfig();

    private final AtomicBoolean state = new AtomicBoolean(false);
    public ZbusRpcServer(List<RpcRegistryConfig> rpcRegistryConfigs, List<RpcProtocolConfig> rpcProtocolConfigs, String name) {
        this.rpcRegistryConfigs = rpcRegistryConfigs;
        this.rpcProtocolConfigs = rpcProtocolConfigs;
        this.name = name;
        try {
            this.broker = new ZbusBroker(rpcRegistryConfigs.stream().map(RpcRegistryConfig::getAddress).collect(Collectors.joining(";")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.setConsumerCount(2);
        config.setMq(name);
        config.setBroker(broker);
        config.setMessageProcessor(processor);
        config.setVerbose(true);

    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public Server register(Object bean) {
        processor.addModule(bean);
        return this;
    }

    @Override
    public Server register(String name, Object bean) {
        processor.addModule(name, bean);
        return this;
    }

    @Override
    public void start() throws IOException {
        state.set(true);
        runService.execute(() -> {
            Service svc = new Service(config);
            try {
                svc.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() throws IOException {
        state.set(false);
        ThreadUtils.closeQuietly(runService);
    }
}
