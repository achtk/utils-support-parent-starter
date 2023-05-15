package com.chua.common.support.placeholder;

/**
 * 占位符信息
 *
 * @author CH
 */
public class PropertyPlaceholderConfigurer extends PlaceholderSupport {

    /**
     * Never check system properties.
     */
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

    /**
     * Check system properties if not resolvable in the specified properties.
     * This is the default.
     */
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

    /**
     * Check system properties first, before trying the specified properties.
     * This allows system properties to override any other property source.
     */
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
}
