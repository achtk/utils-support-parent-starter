package com.chua.nacos.support.discovery;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryBoundType;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.utils.MapUtils;

import java.util.*;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public class NacosServiceDiscovery implements ServiceDiscovery {
    private static final String DEFAULT_DISCOVERY = "discovery";
    private String discovery = DEFAULT_DISCOVERY;
    private NamingService namingService;
    private Robin robin;

    @Override
    public ServiceDiscovery robin(Robin robin) {
        this.robin = robin;
        return this;
    }

    @Override
    public Discovery discovery(String discovery, DiscoveryBoundType strategy) throws Exception {
        strategy = null == strategy ? DiscoveryBoundType.ROUND_ROBIN : strategy;

        List<Instance> instances = namingService.getAllInstances(discovery);
        Robin<Instance> roundRobin = robin.create();
        for (Instance instance : instances) {
            roundRobin.addNode(instance);
        }
        Node<Instance> robin = roundRobin.selectNode();
        Instance perceptual = robin.getContent();
        return Discovery.builder()
                .address(perceptual.getIp())
                .port(perceptual.getPort())
                .uriSpec(MapUtils.getString(perceptual.getMetadata(), "uriSpec", "http://" + perceptual.getIp() + ":" + perceptual.getPort()))
                .weight(perceptual.getWeight())
                .build();
    }


    @Override
    public ServiceDiscovery register(Discovery discovery) {
        Map<String, String> value = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        value.put("uriSpec", discovery.getUriSpec());
        value.putAll(Optional.ofNullable(discovery.getMetadata()).orElse(Collections.emptyMap()));

        Instance instance = new Instance();
        instance.setIp(discovery.getAddress());
        instance.setPort(discovery.getPort());
        instance.setMetadata(value);
        instance.setServiceName(discovery.getDiscovery());
        if (null != discovery.getId()) {
            instance.setInstanceId(discovery.getId());
        }
        if (discovery.getWeight() > 0) {
            instance.setWeight(discovery.getWeight());
        }

        instance.setHealthy(true);
        instance.setClusterName(this.discovery);
        try {
            namingService.registerInstance(discovery.getDiscovery(), instance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ServiceDiscovery start(DiscoveryOption discoveryOption) throws Exception {
        try {
            this.namingService = NacosFactory.createNamingService(discoveryOption.getAddress());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ServiceDiscovery stop() throws Exception {
        this.namingService.shutDown();
        return this;
    }
}
