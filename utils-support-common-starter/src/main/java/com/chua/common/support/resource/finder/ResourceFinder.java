package com.chua.common.support.resource.finder;

import com.chua.common.support.resource.resource.Resource;

import java.util.Set;

/**
 * 资源查找器
 *
 * @author CH
 */
public interface ResourceFinder {

    /**
     * 资源查找器
     *
     * @param name 名称
     * @return 结果
     */
    Set<Resource> find(String name);
}
