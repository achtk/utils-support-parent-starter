package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class StringInfo extends AbstractConstantInfo {
    private int stringIndex;
    private String value;

    public StringInfo() {
        type = ConstantType.STRING;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    @Override
    public void resolve(BinaryData binaryData) {
        stringIndex = binaryData.readShort();
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool) {
        value = ((Utf8Info) constantPool[stringIndex - 1]).getValue();
    }
}
