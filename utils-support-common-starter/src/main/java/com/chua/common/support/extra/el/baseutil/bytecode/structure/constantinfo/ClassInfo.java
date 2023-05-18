package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class ClassInfo extends ConstantInfo
{
    private int    nameIndex;
    private String name;

    public ClassInfo()
    {
        type = ConstantType.Class;
    }

    public String getName()
    {
        return name;
    }

    public void setNameIndex(int nameIndex)
    {
        this.nameIndex = nameIndex;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        nameIndex = binaryData.readShort();
    }

    @Override
    public String toString()
    {
        return "ClassInfo{" + "nameIndex=" + nameIndex + ", name='" + name + '\'' + '}';
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
        name = ((Utf8Info) constant_pool[nameIndex - 1]).getValue();
    }
}
