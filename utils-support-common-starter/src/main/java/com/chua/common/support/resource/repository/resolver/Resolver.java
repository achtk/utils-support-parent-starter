package com.chua.common.support.resource.repository.resolver;

import com.chua.common.support.repository.Metadata;

import java.net.URL;
import java.util.List;

/**
 * 解释器
 *
 * @author CH
 */
public interface Resolver {
    /**
     * 查找资源
     *
     * @param root 路径
     * @param name 名称
     * @return 路径
     */
    List<Metadata> resolve(URL root, String name);
}
