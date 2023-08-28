package com.chua.common.support.extra.el.expression.token;

import java.util.HashMap;
import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public enum KeyWord implements Token {
    /**
     * true
     */
    TRUE,
    /**
     * false
     */
    FALSE,
    /**
     * null
     */
    NULL;

    private static final Map<String, KeyWord> DEFAULT_KEEY_WORDS = new HashMap<String, KeyWord>(128);

    static {
        for (KeyWord each : KeyWord.values()) {
            DEFAULT_KEEY_WORDS.put(each.name().toLowerCase(), each);
        }
    }

    public static KeyWord getKeyWord(String literals) {
        return DEFAULT_KEEY_WORDS.get(literals.toLowerCase());
    }
}
