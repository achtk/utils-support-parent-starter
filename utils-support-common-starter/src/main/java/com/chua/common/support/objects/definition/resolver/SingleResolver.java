package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Spi;

/**
 * 单例解析器
 * @author CH
 */
@Spi("default")
public interface SingleResolver {
    /**
     * 是否单例
     * @return 是否单例
     */
    boolean isSingle();


    @Spi("default")
    public class DefaultSingleResolver implements SingleResolver {

        @Override
        public boolean isSingle() {
            return false;
        }
    }
}
