package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class DoubleInfo extends AbstractConstantInfo {
    private double value;

    public DoubleInfo() {
        type = ConstantType.DOUBLE;
    }

    @Override
    public void resolve(BinaryData binaryData) {
        long longBits = binaryData.readLong();
        this.value = Double.longBitsToDouble(longBits);
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool) {
    }

    public double getValue() {
        return value;
    }
}
