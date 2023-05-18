package com.chua.common.support.extra.el.baseutil.smc.model;

import com.chua.common.support.extra.el.baseutil.smc.SmcHelper;

public class FieldModel
{
    private final String   name;
    private final Class<?> type;

    protected String outSource;

    public FieldModel(String name, Class<?> type, ClassModel classModel)
    {
        this.name = name;
        this.type = type;
        StringBuilder builder = new StringBuilder();
        builder.append("public ").append(SmcHelper.getReferenceName(type, classModel)).append(" ").append(name).append(";\r\n");
        outSource = builder.toString();
    }

    public FieldModel(String name, Class<?> type, String initStr, ClassModel classModel)
    {
        this.name = name;
        this.type = type;
        StringBuilder builder = new StringBuilder();
        builder.append("public ").append(SmcHelper.getReferenceName(type, classModel)).append(" ").append(name).append(" = ").append(initStr).append(";\r\n");
        outSource = builder.toString();
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return outSource;
    }
}
