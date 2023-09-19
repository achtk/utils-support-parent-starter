package com.chua.rpc.support.annotation.dubbo;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.rpc.RpcProtocolConfig;
import com.chua.common.support.rpc.RpcRegistryConfig;
import com.chua.common.support.rpc.RpcServer;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.ThreadUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 可疑rpc服务器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class DubboRpcServer implements RpcServer {

    private final List<RegistryConfig> registryConfigs;
    private final List<ProtocolConfig> protocolConfigs;
    private List<RpcRegistryConfig> rpcRegistryConfigs;
    private List<RpcProtocolConfig> rpcProtocolConfigs;
    private String name;

    private ExecutorService runService = ThreadUtils.newFixedThreadExecutor(2, "rpc-server");

    private final AtomicBoolean state = new AtomicBoolean(false);
    ApplicationConfig applicationConfig = new ApplicationConfig();
    public DubboRpcServer(List<RpcRegistryConfig> rpcRegistryConfigs, List<RpcProtocolConfig> rpcProtocolConfigs, String name) {
        this.rpcRegistryConfigs = rpcRegistryConfigs;
        this.rpcProtocolConfigs = rpcProtocolConfigs;
        this.name = name;
        applicationConfig.setName(name);
        this.registryConfigs = BeanUtils.copyPropertiesList(rpcRegistryConfigs, RegistryConfig.class);
        this.protocolConfigs = BeanUtils.copyPropertiesList(rpcProtocolConfigs, ProtocolConfig.class);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() throws IOException {
        state.set(true);
        runService.execute(() -> {
            while (state.get()) {
                ThreadUtils.sleepSecondsQuietly(1);
            }
        });
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Server register(Object bean) {
        Set<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(bean.getClass());
        for (Class<?> allInterface : allInterfaces) {
            register(allInterface.getTypeName(), bean);
        }
        return this;
    }

    @Override
    public Server register(String name, Object bean) {
        ServiceConfig serviceConfig = new ServiceConfig();

        serviceConfig.setRegistries(registryConfigs);
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setProtocols(protocolConfigs);
        serviceConfig.setInterface(name);
        serviceConfig.setRef(bean);
        serviceConfig.export();
        return this;
    }
}
