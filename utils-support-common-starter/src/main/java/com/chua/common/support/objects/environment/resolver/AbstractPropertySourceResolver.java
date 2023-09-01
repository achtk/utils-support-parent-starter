package com.chua.common.support.objects.environment.resolver;

import java.io.InputStream;

/**
 * @author CH
 */
public abstract class AbstractPropertySourceResolver implements PropertySourceResolver{

    private final String name;

    private final InputStream inputStream;

    public AbstractPropertySourceResolver(String name, InputStream inputStream) {
        this.name = name;
        this.inputStream = inputStream;
    }
}
