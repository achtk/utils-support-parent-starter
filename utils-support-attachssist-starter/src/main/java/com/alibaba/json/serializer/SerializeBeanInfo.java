package com.alibaba.json.serializer;

import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.util.FieldInfo;

public class SerializeBeanInfo {

    protected final Class<?> beanType;
    protected final String typeName;
    protected final String typeKey;
    protected final JSONType jsonType;

    protected final com.alibaba.json.util.FieldInfo[] fields;
    protected final com.alibaba.json.util.FieldInfo[] sortedFields;

    protected int features;

    public SerializeBeanInfo(Class<?> beanType, //
                             JSONType jsonType, //
                             String typeName, //
                             String typeKey,
                             int features,
                             com.alibaba.json.util.FieldInfo[] fields, //
                             FieldInfo[] sortedFields
    ){
        this.beanType = beanType;
        this.jsonType = jsonType;
        this.typeName = typeName;
        this.typeKey = typeKey;
        this.features = features;
        this.fields = fields;
        this.sortedFields = sortedFields;
    }

}
