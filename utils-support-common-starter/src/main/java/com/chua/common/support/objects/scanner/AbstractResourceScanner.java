package com.chua.common.support.objects.scanner;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
public abstract class AbstractResourceScanner implements TypeResourceScanner {

    protected String[] packages;

    public AbstractResourceScanner(String[] packages) {
        this.packages = packages;
    }
}
