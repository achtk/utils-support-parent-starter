package com.chua.nacos.support.robin;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.lang.robin.RobinConfig;
import com.chua.common.support.lang.robin.WeightedRoundRobin;
import com.chua.common.support.net.NetAddress;

import java.util.List;

/**
 * 负载均衡
 *
 * @author CH
 */
public class NacosRobin implements Robin, InitializingAware {

    private final RobinConfig config;
    private final String root;
    private NamingService namingService;

    private final Robin robin = new WeightedRoundRobin();

    public NacosRobin(RobinConfig config) {
        this.config = config;
        this.root = config.getRoot();
        afterPropertiesSet();
    }

    @Override
    public Node selectNode() {
        List<Instance> instances = null;
        try {
            instances = namingService.getAllInstances(root);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        Robin roundRobin = robin.create();
        for (Instance instance : instances) {
            roundRobin.addNode(instance);
        }
        Node robin = roundRobin.selectNode();
        Instance perceptual = robin.getValue(Instance.class);
        return new Node(perceptual.getIp() + ":" + perceptual.getPort());
    }

    @Override
    public Robin create() {
        return this;
    }

    @Override
    public Robin clear() {
        try {
            List<Instance> allInstances = namingService.getAllInstances(root);
            for (Instance allInstance : allInstances) {
                namingService.deregisterInstance(root, allInstance);
            }
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Robin addNode(Node node) {
        NetAddress netAddress = NetAddress.of(node.getString());
        Instance instance = new Instance();
        instance.setIp(netAddress.getIp());
        instance.setPort(netAddress.getPort());
        instance.setServiceName(netAddress.getAddress());
        instance.setWeight(node.getWeight());

        instance.setHealthy(true);
        instance.setClusterName(this.root);
        try {
            namingService.registerInstance(root, instance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void close() throws Exception {
        this.namingService.shutDown();
    }

    @Override
    public void afterPropertiesSet() {
        try {
            this.namingService = NacosFactory.createNamingService(config.getHost() + ":" + config.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }
}
