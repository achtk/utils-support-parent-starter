package com.alibaba.json.spi;

import com.alibaba.json.parser.ParserConfig;
import com.alibaba.json.parser.deserializer.ObjectDeserializer;
import com.alibaba.json.serializer.ObjectSerializer;
import com.alibaba.json.serializer.SerializeConfig;

public interface Module {
    ObjectDeserializer createDeserializer(ParserConfig config, Class type);
    ObjectSerializer createSerializer(SerializeConfig config, Class type);
}
