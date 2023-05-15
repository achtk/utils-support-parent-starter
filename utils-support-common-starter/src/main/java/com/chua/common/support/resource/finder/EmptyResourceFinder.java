package com.chua.common.support.resource.finder;

import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;

import java.util.Collections;
import java.util.Set;

/**
 * 空查找器
 * @author CH
 */
public class EmptyResourceFinder extends AbstractResourceFinder{

    public static final ResourceFinder DEFAULT = new EmptyResourceFinder(ResourceConfiguration.DEFAULT);

    public EmptyResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        return Collections.emptySet();
    }
}
