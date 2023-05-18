package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class FieldRefInfo extends RefInfo
{
    public FieldRefInfo()
    {
        type = ConstantType.FieldRef;
    }
}
