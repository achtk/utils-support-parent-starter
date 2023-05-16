package com.chua.common.support.lang.profile.value;

import java.util.Properties;

/**
 * profile value
 *
 * @author CH
 */
public class PropertiesProfileValue extends MapProfileValue {
    public PropertiesProfileValue(String resourceUrl, Properties properties) {
        super(resourceUrl, properties);
    }
}
