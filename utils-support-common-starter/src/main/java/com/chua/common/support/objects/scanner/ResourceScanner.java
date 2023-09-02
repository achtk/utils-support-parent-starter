package com.chua.common.support.objects.scanner;

import java.util.Set;

/**
 * 资源扫描器
 *
 * @author CH
 * @since 2023/09/02
 */
public interface ResourceScanner<T> {

    /**
     * 扫描
     *
     * @return {@link Set}<{@link T}>
     */
    Set<T> scan();
}
