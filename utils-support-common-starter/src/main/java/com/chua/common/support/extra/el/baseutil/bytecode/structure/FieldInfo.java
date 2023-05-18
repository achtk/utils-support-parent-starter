package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

public class FieldInfo
{
    private int             access_flags;
    private int             name_index;
    private String          name;
    private String          descriptor;
    private AttributeInfo[] attributeInfos;

    public void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        access_flags = binaryData.readShort();
        name_index = binaryData.readShort();
        name = ((Utf8Info) constantInfos[name_index - 1]).getValue();
        int descriptor_index = binaryData.readShort();
        descriptor = ((Utf8Info) constantInfos[descriptor_index - 1]).getValue();
        int attributes_count = binaryData.readShort();
        attributeInfos = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributes_count; i++)
        {
            attributeInfos[i] = AttributeInfo.parse(binaryData, constantInfos);
        }
    }

    public String getName()
    {
        return name;
    }

    public AttributeInfo[] getAttributeInfos()
    {
        return attributeInfos;
    }

    @Override
    public String toString()
    {
        return "FieldInfo{" + "name='" + name + '\'' + ", descriptor='" + descriptor + '\'' + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }
}
