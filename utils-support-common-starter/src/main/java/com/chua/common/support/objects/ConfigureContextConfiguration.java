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
     * 外侧
     */
    private String[] outSide;
}
