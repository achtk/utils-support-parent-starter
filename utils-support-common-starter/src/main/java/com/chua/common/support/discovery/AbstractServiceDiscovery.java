package com.chua.common.support.discovery;


import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.Splitter;
import com.chua.common.support.utils.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 服务发现
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
public abstract class AbstractServiceDiscovery implements ServiceDiscovery {

    protected DiscoveryOption discoveryOption;

    public AbstractServiceDiscovery(DiscoveryOption discoveryOption) {
        this.discoveryOption = discoveryOption;
        afterPropertiesSet();
    }


    /**
     * 获取
     *
     * @param path 路径
     * @return {@link Set}<{@link Discovery}>
     */
    protected abstract Set<Discovery> get(String path);

    /**
     * 获取路径
     *
     * @param path 路径
     */
    public Set<Discovery> getPath(String path) {
        Set<Discovery> netAddresses = get(path);
        List<String> strings = Splitter.on('/').trimResults().omitEmptyStrings().splitToList(path);
        if (strings.size() <= 1 && CollectionUtils.isEmpty(netAddresses)) {
            return null;
        }

        if (!CollectionUtils.isEmpty(netAddresses)) {
            return netAddresses;
        }
        int index = strings.size() - 1;
        String newPath = "/" + Joiner.on('/').join(strings.subList(0, index));
        while (null == netAddresses && index < strings.size()) {
            netAddresses = get(newPath);
            if (!CollectionUtils.isEmpty(netAddresses)) {
                return netAddresses;
            }
            index--;
            if (index < 0) {
                break;
            }
            if (index < strings.size()) {
                newPath = "/" + Joiner.on('/').join(strings.subList(0, index));
                continue;
            }
            break;
        }

        return null;
    }
}
