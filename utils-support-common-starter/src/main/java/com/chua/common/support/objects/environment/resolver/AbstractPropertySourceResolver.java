package com.chua.common.support.objects.environment.resolver;

import java.io.InputStream;

/**
 * @author CH
 */
public abstract class AbstractPropertySourceResolver implements PropertySourceResolver{

    protected final String name;

    protected final InputStream inputStream;

    public AbstractPropertySourceResolver(String name, InputStream inputStream) {
        this.name = name;
        this.inputStream = inputStream;
    }
}
