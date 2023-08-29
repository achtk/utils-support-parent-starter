package com.chua.common.support.file.xml;

/*
Public Domain.
*/

import com.alibaba.fastjson2.JSONException;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Converts a Property file data into JSONObject and back.
 *
 * @author JSON.org
 * @version 2015-05-05
 */
public class XmlProperty {
    /**
     * Converts a property file object into a JSONObject. The property file object is a table of name value pairs.
     *
     * @param properties java.util.Properties
     * @return JSONObject
     * @throws JSONException if a called function has an error
     */
    public static XmlToJsonObject toJSONObject(Properties properties) throws JSONException {
        // can't use the new constructor for Android support
        XmlToJsonObject jo = new XmlToJsonObject();
        if (properties != null && !properties.isEmpty()) {
            Enumeration<?> enumProperties = properties.propertyNames();
            while (enumProperties.hasMoreElements()) {
                String name = (String) enumProperties.nextElement();
                jo.put(name, properties.getProperty(name));
            }
        }
        return jo;
    }

    /**
     * Converts the JSONObject into a property file object.
     *
     * @param jo JSONObject
     * @return java.util.Properties
     * @throws JSONException if a called function has an error
     */
    public static Properties toProperties(XmlToJsonObject jo) throws JSONException {
        Properties properties = new Properties();
        if (jo != null) {
            // Don't use the new entrySet API to maintain Android support
            for (final String key : jo.keySet()) {
                Object value = jo.opt(key);
                if (!XmlToJsonObject.NULL.equals(value)) {
                    properties.put(key, value.toString());
                }
            }
        }
        return properties;
    }
}
