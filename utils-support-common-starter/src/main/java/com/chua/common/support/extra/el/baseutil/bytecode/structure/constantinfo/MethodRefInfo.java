package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class MethodRefInfo extends RefInfo
{
    public MethodRefInfo()
    {
        type = ConstantType.MethodRef;
    }

    @Override
    public String toString()
    {
        return "MethodRefInfo{" + "classInfoIndex=" + classInfoIndex + ", nameAndTypeInfoIndex=" + nameAndTypeInfoIndex + '}';
    }
}
