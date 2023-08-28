package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

/**
 * 基础类
 *
 * @author CH
 */
public class FloatInfo extends AbstractConstantInfo {
    private float value;

    public FloatInfo() {
        type = ConstantType.FLOAT;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void resolve(BinaryData binaryData) {
        int intBits = binaryData.readInt();
        value = Float.intBitsToFloat(intBits);
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool) {
    }
}
