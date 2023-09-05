package com.chua.common.support.objects.definition.resolver;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.objects.scanner.annotations.AutoService;
import com.chua.common.support.utils.StringUtils;

/**
 * 优先级解析器
 *
 * @author CH
 */
@Spi
public interface NameResolver {
    /**
     * 名称
     * 优先级
     *
     * @param type 类型
     * @return 优先级
     */
    String name(Class<?> type);

    /**
     * 名称解析器
     */
    @Spi("default")
    public class DefaultOrderResolver implements NameResolver {

        @Override
        public String name(Class<?> type) {
            AutoService autoService = type.getDeclaredAnnotation(AutoService.class);
            if(null != autoService && StringUtils.isNotBlank(autoService.value())) {
                return autoService.value();
            }

            Spi spi = type.getDeclaredAnnotation(Spi.class);
            if(null != spi && spi.value().length > 0) {
                return spi.value()[0];
            }

            Extension extension = type.getDeclaredAnnotation(Extension.class);
            if(null != extension && StringUtils.isNotBlank(extension.value())) {
                return extension.value();
            }
            return null;
        }
    }
}
