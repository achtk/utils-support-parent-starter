package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public abstract class RefInfo extends ConstantInfo
{
    protected int       classInfoIndex;
    protected int       nameAndTypeInfoIndex;
    protected ClassInfo classInfo;

    @Override
    public void resolve(BinaryData binaryData)
    {
        classInfoIndex = binaryData.readShort();
        nameAndTypeInfoIndex = binaryData.readShort();
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
        classInfo = (ClassInfo) constant_pool[classInfoIndex - 1];
    }
}
