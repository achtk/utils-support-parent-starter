package com.alibaba.json.parser.deserializer;

import com.alibaba.json.parser.deserializer.ParseProcess;

import java.lang.reflect.Type;

/**
 * @author wenshao[szujobs@hotmail.com]
 * @since 1.1.34
 */
public interface ExtraTypeProvider extends ParseProcess {

    Type getExtraType(Object object, String key);
}
