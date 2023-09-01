package com.chua.common.support.objects.environment;

import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.PropertyResolver;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 环境配置
 *
 * @author CH
 * @date 2023/09/01
 */
@Data
@Builder
@Accessors(fluent = true)
public class EnvironmentConfiguration {
    /**
     * 占位符处理器
     */
    private PlaceholderSupport placeholderSupport;
    /**
     * 属性处理器
     */
    private PropertyResolver propertyResolver;

    /**
     * 扫描路径
     */
    @Builder.Default
    private String componentScan = "/";

}
