package com.chua.common.support.objects;

import com.chua.common.support.objects.environment.EnvironmentConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 配置
 * @author CH
 */
@Builder
@Data
@Accessors(fluent = true)
public class ConfigureContextConfiguration {

    @Builder.Default
    private EnvironmentConfiguration environmentConfiguration = EnvironmentConfiguration.builder().build();


    /**
     * 包装
     */
    private String[] packages;
    /**
     * 外部数据地址
     */
    private String[] outSide;
    /**
     * 外部数据以注解方式注入
     * 1.false 所有数据注入
     */
    private boolean outSideInAnnotation;
    /**
     * 依赖包位置
     */
    @Builder.Default
    private String repository = ".repository";
}
