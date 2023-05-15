package com.chua.common.support.resource;

import com.chua.common.support.resource.resource.ClassResource;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 资源
 * @author CH
 */
public class DelegateStore implements Store{
    private final Set<Resource> resources;

    public DelegateStore(Set<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public Set<Resource> getResource() {
        return resources;
    }

    @Override
    public Resource getFirst() {
        return CollectionUtils.findFirst(resources);
    }

    @Override
    public Set<Class<?>> getTypes(Class<?> type) {
        Set<Class<?>> rs = new HashSet<>(resources.size());
        for (Resource resource : resources) {
            if(resource instanceof ClassResource) {
                Class<?> loader = ((ClassResource) resource).loader();
                if(null == loader) {
                    continue;
                }

                if(null == type || type.isAssignableFrom(loader)) {
                    rs.add(loader);
                }
            }
        }
        return rs;
    }
}
