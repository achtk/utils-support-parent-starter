package com.chua.common.support.resource.finder;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.CmdUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统资源查找器
 * @author CH
 */
@Slf4j
public class FileSystemLinuxResourceFinder extends AbstractResourceFinder{

    public FileSystemLinuxResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        //获取根目录
        String classPathRoot = findPathRootPath(name);
        //待匹配的文件
        String subPath = name.substring(classPathRoot.length());

        long startTime = System.currentTimeMillis();
        String exec = CmdUtils.exec("locate " + subPath);
        List<File> result = StringUtils.isEmpty(exec) ? Collections.emptyList() : Splitter.on("\r\n").omitEmptyStrings().trimResults().splitToList(exec).stream().map(item -> new File(item)).collect(Collectors.toList());
        Set<Resource> rs = new LinkedHashSet<>();
        try {
            for (File file : result) {
                Resource resource = Resource.create(file);
                consumer.accept(resource);
                rs.add(resource);
            }
        } finally {
            long time = System.currentTimeMillis() - startTime;
            int matchSize = result.size();
            log.debug("表达式: {}, [{}/{}] 总共被检索, 耗时: {}ms, 扫描{}个URL. 平均: {}/s", name, matchSize, "∞", time, matchSize, matchSize * 1000 / time);
        }
        return rs;
    }
}
