package com.chua.agent.support.json.parser.deserializer;

/**
 * @author wenshao[szujobs@hotmail.com]
 * @since 1.2.9
 */
public interface ExtraProcessable {
    void processExtra(String key, Object value);
}
