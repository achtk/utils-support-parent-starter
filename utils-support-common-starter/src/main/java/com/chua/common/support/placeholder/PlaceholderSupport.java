package com.chua.common.support.placeholder;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 佔位符
 *
 * @author CH
 */
@Setter
@Getter
public class PlaceholderSupport {

    /**
     * 默认前缀占位符
     */

    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * 默认后缀占位符
     */

    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    public static final String DEFAULT_VALUE_SEPARATOR = ":";

    /**
     * 默认单例解析器
     */

    private static final PlaceholderSupport PLACEHOLDER_SUPPORT = new PlaceholderSupport();

    /**
     * 占位符前缀
     */

    private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;
    /**
     * 是否忽略位置值
     */
    private boolean ignoreUnresolvablePlaceholders;
    /**
     * 占位符解析器
     */
    @Setter
    @Accessors(chain = true)
    private PlaceholderResolver resolver = new SystemPropertyPlaceholderResolver();

    /**
     * 占位符后缀
     */

    private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;
    /**
     * 分隔符
     */
    private String valueSeparator = DEFAULT_VALUE_SEPARATOR;

    /**
     * 去除空格
     */
    private boolean trimValues = true;

    public PlaceholderSupport() {
    }

    public PlaceholderSupport(String placeholderPrefix, String placeholderSuffix, String valueSeparator) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        this.valueSeparator = valueSeparator;
    }

}
