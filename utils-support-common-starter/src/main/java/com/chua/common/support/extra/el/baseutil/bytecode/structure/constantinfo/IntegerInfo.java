package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class IntegerInfo extends ConstantInfo
{
    private int value;

    public IntegerInfo()
    {
        type = ConstantType.Integer;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        value = binaryData.readInt();
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
    }
}
