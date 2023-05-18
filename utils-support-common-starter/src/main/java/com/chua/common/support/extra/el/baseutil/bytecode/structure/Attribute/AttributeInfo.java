package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public abstract class AttributeInfo
{
    //2个字节的索引
    protected String name;
    //4个字节的bytes长度
    protected int    length;

    public AttributeInfo(String name, int length)
    {
        this.name = name;
        this.length = length;
    }

    public static AttributeInfo parse(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        int           nameIndex = binaryData.readShort();
        int           length    = binaryData.readInt();
        String        name      = ((Utf8Info) constantInfos[nameIndex - 1]).getValue();
        AttributeInfo info;
        if ("RuntimeVisibleAnnotations".equals(name))
        {
            info = new RuntimeVisibleAnnotationsAttriInfo(name, length);
        }
        else if ("RuntimeVisibleParameterAnnotations".equals(name))
        {
            info = new RuntimeVisibleParameterAnnotationsAttrInfo(name, length);
        }
        else if ("AnnotationDefault".equals(name))
        {
            info = new AnnotationDefaultAttriInfo(name, length);
        }
        else if ("Code".equals(name))
        {
            info = new CodeAttriInfo(name, length);
        }
        else if ("LocalVariableTable".equals(name))
        {
            info = new LocalVariableTableAttriInfo(name, length);
        }
        else
        {
            info = new UnknowAttrInfo(name, length);
        }
        info.resolve(binaryData, constantInfos);
        return info;
    }

    public int getLength()
    {
        return length;
    }

    protected abstract void resolve(BinaryData binaryData, ConstantInfo[] constantInfos);

    protected void ignoreParse(BinaryData binaryData)
    {
        binaryData.addIndex(length);
    }

    @Override
    public String toString()
    {
        return "AttributeInfo{" + "name='" + name + '\'' + ", length=" + length + '}';
    }
}
