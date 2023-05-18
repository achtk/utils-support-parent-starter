package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.ExceptionHandler;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

public class CodeAttriInfo extends AttributeInfo
{
    //2字节长度
    private int                max_stack;
    //2字节长度
    private int                max_locals;
    private int                code_length;
    private ExceptionHandler[] exceptionHandlers;
    private AttributeInfo[]    attributeInfos;

    public CodeAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        max_stack = binaryData.readShort();
        max_locals = binaryData.readShort();
        code_length = binaryData.readInt();
        //由于目前不做解析，因此忽略掉这部分code数据
        binaryData.addIndex(code_length);
        int exception_table_length = binaryData.readShort();
        //目前不解析异常表信息，忽略这部分数据
        binaryData.addIndex(exception_table_length * 8);
        int attributes_count = binaryData.readShort();
        attributeInfos = new AttributeInfo[attributes_count];
        for (int i = 0; i < attributeInfos.length; i++)
        {
            attributeInfos[i] = parse(binaryData, constantInfos);
        }
    }

    public AttributeInfo[] getAttributeInfos()
    {
        return attributeInfos;
    }

    @Override
    public String toString()
    {
        return "CodeAttriInfo{" + "max_stack=" + max_stack + ", max_locals=" + max_locals + ", code_length=" + code_length + ", exceptionHandlers=" + Arrays.toString(exceptionHandlers) + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }
}
