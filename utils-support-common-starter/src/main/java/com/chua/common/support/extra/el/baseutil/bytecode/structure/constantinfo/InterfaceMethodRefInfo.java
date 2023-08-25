package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;

public class InterfaceMethodRefInfo extends RefInfo
{
    public InterfaceMethodRefInfo()
    {
        type = ConstantType.INTERFACE_METHOD_REF;
    }
}
