package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;

public class FieldRefInfo extends RefInfo
{
    public FieldRefInfo()
    {
        type = ConstantType.FIELD_REF;
    }
}
