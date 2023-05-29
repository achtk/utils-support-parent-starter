package com.chua.common.support.resource.resource;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 多文件
 * @author CH
 */
public class MultiFileResource extends MultiResource {

    public MultiFileResource() {
    }

    public MultiFileResource(Resource... resources) {
        super(resources);
    }

    public MultiFileResource(Collection<Resource> resources) {
        super(resources);
    }

    public MultiFileResource(File[] files) {
        this(Arrays.stream(files).map(FileSystemResource::new).collect(Collectors.toList()));
    }
}
