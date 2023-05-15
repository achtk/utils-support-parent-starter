package com.chua.common.support.resource.finder;

import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.FileSystemResource;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 系统资源查找器
 * @author CH
 */
public class FileSystemResourceFinder extends AbstractResourceFinder{

    public FileSystemResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        Set<Resource> rs = new LinkedHashSet<>();
        String fullPath = getFullPath(name);
        String matchPath = getMatchPath(name);
        Set<String> dir = getDir(fullPath);
        (configuration.isParallel() ? dir.stream().parallel() : dir.stream()).forEach(path -> {
            File file = new File(path);
            File[] files = file.listFiles();
            if(null == files) {
                return;
            }

            for (File file1 : files) {
                String realName = getRealName(file1.getAbsolutePath(), fullPath);
                if(isExclude(realName)) {
                    continue;
                }

                if(isMatch(realName, matchPath)) {
                    rs.add(new FileSystemResource(file1));
                }
            }
        });
        return rs;
    }


    protected Set<String> getDir(String fullPath) {
        Set<String> rs = new LinkedHashSet<>();
        if(StringUtils.isEmpty(fullPath)) {
            File[] files = File.listRoots();
            for (File file : files) {
                rs.add(file.getAbsolutePath());
            }
        } else {
            rs.add(fullPath);
        }
        return rs;
    }
}
