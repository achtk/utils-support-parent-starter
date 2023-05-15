package com.chua.common.support.placeholder;

/**
 * 系统信息
 *
 * @author CH
 */
public class SystemPropertyPlaceholderResolver implements PlaceholderResolver {
    @Override
    public String resolvePlaceholder(String placeholderName) {
        try {
            String propVal = System.getProperty(placeholderName);
            if (propVal == null) {
                // Fall back to searching the system environment.
                propVal = System.getenv(placeholderName);
            }
            return propVal;
        } catch (Throwable ex) {
            return null;
        }
    }
}
