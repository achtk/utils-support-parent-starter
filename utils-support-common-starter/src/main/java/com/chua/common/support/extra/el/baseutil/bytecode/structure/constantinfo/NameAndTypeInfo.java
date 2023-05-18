package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class NameAndTypeInfo extends ConstantInfo
{
    private int    nameIndex;
    private int    descriptorIndex;
    private String name;
    private String descriptor;

    public NameAndTypeInfo()
    {
        type = ConstantType.NameAndType;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        nameIndex = binaryData.readShort();
        descriptorIndex = binaryData.readShort();
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
        name = ((Utf8Info) constant_pool[nameIndex - 1]).getValue();
        descriptor = ((Utf8Info) constant_pool[descriptorIndex - 1]).getValue();
    }
}
