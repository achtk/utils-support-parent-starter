package com.chua.common.support.resource.finder;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.ClassUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * 系统资源查找器
 * @author CH
 */
public class FileSystemAnyResourceFinder extends AbstractResourceFinder{

    public static Class<? extends ResourceFinder> WIN_FINDER = ClassUtils.forName("com.chua.enhance.support.matcher.FileSystemWindowResourceFinder", ResourceFinder.class);
    public static Class<? extends ResourceFinder> LINUX_FINDER = FileSystemLinuxResourceFinder.class;

    public FileSystemAnyResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        if(Projects.isWindows()) {
            if(null == WIN_FINDER) {
                return Collections.emptySet();
            }
            return Optional.ofNullable((ResourceFinder)ClassUtils.forObject(WIN_FINDER, configuration)).orElse(EmptyResourceFinder.DEFAULT).find(name);
        }
        return Optional.ofNullable((ResourceFinder)ClassUtils.forObject(LINUX_FINDER, configuration)).orElse(EmptyResourceFinder.DEFAULT).find(name);
    }
}
