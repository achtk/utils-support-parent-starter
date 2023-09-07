package com.chua.common.support.objects;

import com.chua.common.support.lang.expression.parser.DelegateExpressionParser;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.objects.environment.EnvironmentConfiguration;
import com.chua.common.support.objects.environment.properties.PropertySource;
import com.chua.common.support.objects.scanner.BaseAnnotationResourceScanner;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;

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


    @Singular("propertySource")
    public List<PropertySource> propertySources;
    /**
     * 算式解析器
     */
    @Builder.Default
    private ExpressionParser expressionParser = new DelegateExpressionParser();
    /**
     * 掃描包
     */
    private String[] packages;
    /**
     * 外部数据地址
     */
    private String[] outSide;
    /**
     * 外部数据以注解方式注入
     * 1.false 所有数据注入
     * @see BaseAnnotationResourceScanner
     */
    @Builder.Default
    private boolean outSideInAnnotation = true;
    /**
     * 依赖包位置
     */
    @Builder.Default
    private String repository = ".repository";



    public static class ConfigureContextConfigurationBuilder {
        /**
         * 登记
         *
         * @param propertySource 数据
         * @return {@link ConfigureContextConfigurationBuilder}
         */
        public ConfigureContextConfigurationBuilder register(PropertySource propertySource) {
            this.propertySource(propertySource);
            return this;
        }

        /**
         * 登记
         *
         * @param propertySource 数据
         * @param name           名称
         * @return {@link ConfigureContextConfigurationBuilder}
         */
        public ConfigureContextConfigurationBuilder register(String name, PropertySource propertySource) {
            this.propertySource(new PropertySource() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Object getProperty(String name) {
                    return propertySource.getProperty(name);
                }
            });
            return this;
        }
    }
}
