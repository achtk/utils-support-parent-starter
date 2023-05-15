package com.chua.common.support.placeholder;

import com.chua.common.support.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置解析器
 *
 * @author CH
 */
@Slf4j
public class StringValuePropertyResolver implements PropertyResolver {

    @Getter
    private final PlaceholderSupport placeholderSupport;

    private static final Map<String, String> STRING_STRING_HASH_MAP = new HashMap<>(4);

    static {
        STRING_STRING_HASH_MAP.put("}", "{");
        STRING_STRING_HASH_MAP.put("]", "[");
        STRING_STRING_HASH_MAP.put(")", "(");
    }

    private final String simplePrefix;
    private Set<String> visitedPlaceholders;

    public StringValuePropertyResolver(PlaceholderSupport placeholderSupport) {
        this.placeholderSupport = placeholderSupport;
        String simplePrefixForSuffix = STRING_STRING_HASH_MAP.get(placeholderSupport.getPlaceholderPrefix());
        if (simplePrefixForSuffix != null && placeholderSupport.getPlaceholderPrefix().endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        } else {
            this.simplePrefix = placeholderSupport.getPlaceholderPrefix();
        }
    }

    @Override
    public String resolvePlaceholders(String value) {
        int startIndex = value.indexOf(placeholderSupport.getPlaceholderPrefix());
        if (startIndex == -1) {
            return value;
        }
        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + placeholderSupport.getPlaceholderPrefix().length(), endIndex);
                String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = parseStringValue(placeholder, placeholderSupport.getResolver(), visitedPlaceholders);
                // Now obtain the value for the fully resolved key...
                String propVal = placeholderSupport.getResolver().resolvePlaceholder(placeholder);
                if (propVal == null && placeholderSupport.getValueSeparator() != null) {
                    int separatorIndex = placeholder.indexOf(placeholderSupport.getValueSeparator());
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + placeholderSupport.getValueSeparator().length());
                        propVal = placeholderSupport.getResolver().resolvePlaceholder(actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal = parseStringValue(propVal, placeholderSupport.getResolver(), visitedPlaceholders);
                    result.replace(startIndex, endIndex + placeholderSupport.getPlaceholderSuffix().length(), propVal);
                    if (log.isTraceEnabled()) {
                        log.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = result.indexOf(placeholderSupport.getPlaceholderPrefix(), startIndex + propVal.length());
                } else if (placeholderSupport.isIgnoreUnresolvablePlaceholders()) {
                    // Proceed with unprocessed value.
                    startIndex = result.indexOf(placeholderSupport.getPlaceholderPrefix(), endIndex + placeholderSupport.getPlaceholderSuffix().length());
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" +
                            placeholder + "'" + " in value \"" + value + "\"");
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    @Override
    public void add(String name, Object value) {
        PlaceholderResolver resolver = placeholderSupport.getResolver();
        if (resolver instanceof PlaceholderDynamicResolver) {
            ((PlaceholderDynamicResolver) resolver).add(name, value);
        }
    }

    protected String parseStringValue(
            String value, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {

        int startIndex = value.indexOf(placeholderSupport.getPlaceholderPrefix());
        if (startIndex == -1) {
            return value;
        }

        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + placeholderSupport.getPlaceholderPrefix().length(), endIndex);
                String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                // Now obtain the value for the fully resolved key...
                String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                if (propVal == null && placeholderSupport.getValueSeparator() != null) {
                    int separatorIndex = placeholder.indexOf(placeholderSupport.getValueSeparator());
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + placeholderSupport.getValueSeparator().length());
                        propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                    result.replace(startIndex, endIndex + placeholderSupport.getPlaceholderSuffix().length(), propVal);
                    if (log.isTraceEnabled()) {
                        log.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = result.indexOf(placeholderSupport.getPlaceholderPrefix(), startIndex + propVal.length());
                } else if (placeholderSupport.isIgnoreUnresolvablePlaceholders()) {
                    // Proceed with unprocessed value.
                    startIndex = result.indexOf(placeholderSupport.getPlaceholderPrefix(), endIndex + placeholderSupport.getPlaceholderSuffix().length());
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" +
                            placeholder + "'" + " in value \"" + value + "\"");
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + placeholderSupport.getPlaceholderPrefix().length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, placeholderSupport.getPlaceholderSuffix())) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + placeholderSupport.getPlaceholderSuffix().length();
                } else {
                    return index;
                }
            } else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
                withinNestedPlaceholder++;
                index = index + this.simplePrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }
}
