package com.alibaba.json.parser.deserializer;

import com.alibaba.json.parser.DefaultJSONParser;

import java.lang.reflect.Type;

public abstract class ContextObjectDeserializer implements ObjectDeserializer {
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, null, 0);
    }
    
    public abstract <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, String format, int features); 
}
