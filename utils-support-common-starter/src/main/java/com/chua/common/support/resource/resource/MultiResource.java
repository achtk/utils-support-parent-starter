package com.chua.common.support.resource.resource;

import com.chua.common.support.utils.FileUtils;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 多文件
 * @author CH
 */
@NoArgsConstructor
public class MultiResource implements Resource, Iterable<Resource>, Iterator<Resource> {

    private final List<Resource> resources = new LinkedList<>();
    private int cursor;

    /**
     * 构造
     *
     * @param resources 资源数组
     */
    public MultiResource(Resource... resources) {
        this(Arrays.asList(resources));
    }

    /**
     * 构造
     *
     * @param resources 资源列表
     */
    public MultiResource(Collection<Resource> resources) {
        this.resources.addAll(resources);
    }
    @Override
    public InputStream openStream() throws IOException {
        return getUrl().openStream();
    }

    @Override
    public String getUrlPath() {
        return getUrl().toExternalForm();
    }

    @Override
    public URL getUrl() {
        return resources.get(cursor).getUrl();
    }

    @Override
    public long lastModified() {
        return 0;
    }


    @Override
    public Iterator<Resource> iterator() {
        return resources.iterator();
    }

    @Override
    public boolean hasNext() {
        return cursor < resources.size();
    }

    @Override
    public synchronized Resource next() {
        if (cursor >= resources.size()) {
            throw new ConcurrentModificationException();
        }
        this.cursor++;
        return this;
    }

    /**
     * 重置游标
     */
    public synchronized void reset() {
        this.cursor = 0;
    }
}
