package com.chua.agent.support.json.spi;

import com.chua.agent.support.json.parser.ParserConfig;
import com.chua.agent.support.json.parser.deserializer.ObjectDeserializer;
import com.chua.agent.support.json.serializer.ObjectSerializer;
import com.chua.agent.support.json.serializer.SerializeConfig;

public interface Module {
    ObjectDeserializer createDeserializer(ParserConfig config, Class type);

    ObjectSerializer createSerializer(SerializeConfig config, Class type);
}
