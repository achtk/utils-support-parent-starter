package com.chua.common.support.reflection.reflections.scanners;

import javassist.bytecode.ClassFile;

import java.util.List;
import java.util.Map;

@Deprecated
class AbstractResourceScanner implements ResourceScanner {
    protected final ResourceScanner resourceScanner;

    AbstractResourceScanner(ResourceScanner resourceScanner) {
        this.resourceScanner = resourceScanner;
    }

    @Override
    public String index() {
        return resourceScanner.index();
    }

    @Override
    public List<Map.Entry<String, String>> scan(final ClassFile cls) {
        return resourceScanner.scan(cls);
    }
}
