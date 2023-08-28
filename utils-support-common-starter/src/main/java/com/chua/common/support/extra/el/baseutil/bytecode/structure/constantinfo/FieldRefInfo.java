package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;

/**
 * 基础类
 *
 * @author CH
 */
public class FieldRefInfo extends AbstractRefInfo {
    public FieldRefInfo() {
        type = ConstantType.FIELD_REF;
    }
}
