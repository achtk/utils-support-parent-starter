package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

/**
 * 基础类
 *
 * @author CH
 */
public class MethodInfo {
    private int accessFlags;
    private String name;
    private String descriptor;
    private AbstractAttributeInfo[] attributeInfos;

    public void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        accessFlags = binaryData.readShort();
        int nameIndex = binaryData.readShort();
        name = ((Utf8Info) constantInfos[nameIndex - 1]).getValue();
        int descriptorIndex = binaryData.readShort();
        descriptor = ((Utf8Info) constantInfos[descriptorIndex - 1]).getValue();
        int attributesCount = binaryData.readShort();
        attributeInfos = new AbstractAttributeInfo[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            attributeInfos[i] = AbstractAttributeInfo.parse(binaryData, constantInfos);
        }
    }

    @Override
    public String toString() {
        return "MethodInfo{" + "access_flags=" + accessFlags + ", name='" + name + '\'' + ", descriptor='" + descriptor + '\'' + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }

    public AbstractAttributeInfo[] getAttributeInfos() {
        return attributeInfos;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
