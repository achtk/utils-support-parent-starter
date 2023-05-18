package com.alibaba.json.serializer;

import com.alibaba.json.serializer.JSONSerializer;
import com.alibaba.json.serializer.ObjectSerializer;
import com.alibaba.json.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class ToStringSerializer implements ObjectSerializer {

    public static final ToStringSerializer instance = new ToStringSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
                      int features) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        String strVal = object.toString();
        out.writeString(strVal);
    }

}
