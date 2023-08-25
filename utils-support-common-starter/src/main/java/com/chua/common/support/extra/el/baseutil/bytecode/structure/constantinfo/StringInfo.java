package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class StringInfo extends ConstantInfo
{
    private int    stringIndex;
    private String value;

    public StringInfo()
    {
        type = ConstantType.String;
    }

    public int getStringIndex()
    {
        return stringIndex;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        stringIndex = binaryData.readShort();
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
        value = ((Utf8Info) constant_pool[stringIndex - 1]).getValue();
    }
}
