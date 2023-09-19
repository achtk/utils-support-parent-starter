package com.chua.rpc.support.annotation.dubbo;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.rpc.RpcClient;
import com.chua.common.support.rpc.RpcProtocolConfig;
import com.chua.common.support.rpc.RpcRegistryConfig;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * dubbo-rpc客户端
 *
 * @author CH
 */
@Spi("dubbo")
@SuppressWarnings("ALL")
public class DubboRpcClient implements RpcClient {

    private final List<RegistryConfig> registryConfigs;
    private List<RpcRegistryConfig> rpcRegistryConfigs;
    private List<RpcProtocolConfig> rpcProtocolConfigs;
    private String name;

    private final AtomicBoolean state = new AtomicBoolean(false);
    ApplicationConfig applicationConfig = new ApplicationConfig();
    public DubboRpcClient(List<RpcRegistryConfig> rpcRegistryConfigs, String name) {
        this.rpcRegistryConfigs = rpcRegistryConfigs;
        this.name = name;
        applicationConfig.setName(name);
        applicationConfig.setQosEnable(false);
        this.registryConfigs = BeanUtils.copyPropertiesList(rpcRegistryConfigs, RegistryConfig.class);
    }


    @Override
    public <T> T get(Class<T> targetType) {
        ReferenceConfig<T> refrence= new ReferenceConfig<>();
        refrence.setApplication(applicationConfig);
        refrence.setRegistries(registryConfigs);
        refrence.setInterface(targetType);
        return refrence.get();
    }

    @Override
    public void close() throws Exception {

    }
}
