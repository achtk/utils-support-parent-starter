package com.chua.common.support.extra.el.baseutil.bytecode.structure;

/**
 * 基础类
 *
 * @author CH
 */
public class EnumConstant {
    String typeName;
    String enumName;

    public EnumConstant(String typeName, String enumName) {
        if (typeName.startsWith("L")) {
            typeName = typeName.substring(1, typeName.length() - 1);
        }
        this.typeName = typeName;
        this.enumName = enumName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getEnumName() {
        return enumName;
    }
}
