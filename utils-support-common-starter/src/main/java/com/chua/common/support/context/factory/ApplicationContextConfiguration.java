package com.chua.common.support.context.factory;

import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.context.process.BeanPostProcessor;
import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.PropertyResolver;
import com.chua.common.support.placeholder.StringValuePropertyResolver;
import lombok.Data;

import java.util.List;

/**
 * 上下文配置
 *
 * @author CH
 */
@Data
public final class ApplicationContextConfiguration {
    /**
     * 占位符处理器
     */
    private PlaceholderSupport placeholderSupport;
    /**
     * 属性处理器
     */
    private PropertyResolver propertyResolver;

    /**
     * 配置处理器
     */
    private Environment environment;

    /**
     * 加载器
     */
    private List<BeanPostProcessor> processors;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        if (null == environment) {
            return;
        }

        environment.afterPropertiesSet();
        setPlaceholderSupport(new PlaceholderSupport());
    }

    /**
     * 扫描包
     */
    private String[] packageScan;
    /**
     * 是否扫描 aggregate下的bean注入到上下文中
     */
    private boolean openAggregateScanner = true;
    /**
     * 是否只扫描原始包
     */
    private boolean onlyOriginal = true;

    private GlobalConfiguration global;

    public void setPlaceholderSupport(PlaceholderSupport placeholderSupport) {
        this.placeholderSupport = placeholderSupport;
        propertyResolver = new StringValuePropertyResolver(placeholderSupport);
    }
}
