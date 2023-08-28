package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;

public class MethodRefInfo extends AbstractRefInfo
{
    public MethodRefInfo()
    {
        type = ConstantType.METHOD_REF;
    }

    @Override
    public String toString()
    {
        return "MethodRefInfo{" + "classInfoIndex=" + classInfoIndex + ", nameAndTypeInfoIndex=" + nameAndTypeInfoIndex + '}';
    }
}
