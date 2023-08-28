package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class IntegerInfo extends AbstractConstantInfo
{
    private int value;

    public IntegerInfo()
    {
        type = ConstantType.INTEGER;
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
    public void resolve(AbstractConstantInfo[] constantPool)
    {
    }
}
