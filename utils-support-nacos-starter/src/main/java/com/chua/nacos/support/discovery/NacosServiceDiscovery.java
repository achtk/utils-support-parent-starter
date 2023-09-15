package com.chua.nacos.support.discovery;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.discovery.AbstractServiceDiscovery;
import com.chua.common.support.discovery.Discovery;
import com.chua.common.support.discovery.DiscoveryOption;
import com.chua.common.support.discovery.ServiceDiscovery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.MapUtils;

import java.util.List;

/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
@Spi("nacos")
public class NacosServiceDiscovery extends AbstractServiceDiscovery {


    private NamingService namingService;

    public NacosServiceDiscovery(DiscoveryOption discoveryOption) {
        super(discoveryOption);
    }

    @Override
    public ServiceDiscovery registerService(String path, Discovery discovery) {
        Instance instance = new Instance();
        instance.setIp(discovery.getAddress());
        instance.setPort(discovery.getPort());
        instance.setMetadata(MapUtils.asStringMap(BeanMap.create(discovery)));
        instance.setServiceName(discovery.getUriSpec());
        if (null != discovery.getId()) {
            instance.setInstanceId(discovery.getId());
        }
        if (discovery.getWeight() > 0) {
            instance.setWeight(discovery.getWeight());
        }

        instance.setHealthy(true);
        try {
            namingService.registerInstance(discovery.getUriSpec(), instance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Discovery getService(String path, String balance) {
        List<Instance> allInstances = null;
        try {
            allInstances = namingService.getAllInstances(path);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        Robin robin = ServiceProvider.of(Robin.class).getNewExtension(balance);
        Robin robin1 = robin.create();
        robin1.addNode(allInstances);
        Node selectNode = robin1.selectNode();
        if(null == selectNode) {
            return null;
        }
        Instance selectNodeValue = selectNode.getValue(Instance.class);
        return Discovery.builder()
                .protocol(MapUtils.getString(selectNodeValue.getMetadata(), "protocol"))
                .uriSpec(path)
                .port(selectNodeValue.getPort())
                .weight(selectNodeValue.getWeight())
                .address(selectNodeValue.getIp())
                .build();
    }

    @Override
    public void start() {
        try {
            this.namingService = NacosFactory.createNamingService(discoveryOption.getAddress());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            namingService.shutDown();
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {

    }
}
