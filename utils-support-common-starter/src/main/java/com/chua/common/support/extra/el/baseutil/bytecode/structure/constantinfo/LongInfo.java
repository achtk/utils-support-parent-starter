package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class LongInfo extends AbstractConstantInfo
{

    private long value;

    public LongInfo()
    {
        type = ConstantType.LONG;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        value = binaryData.readLong();
    }

    public long getValue()
    {
        return value;
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
    }
}
