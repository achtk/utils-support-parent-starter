package com.alibaba.json.parser.deserializer;

import com.alibaba.json.parser.deserializer.ParseProcess;

import java.lang.reflect.Type;

public interface FieldTypeResolver extends ParseProcess {
    Type resolve(Object object, String fieldName);
}
