package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.ElementValueInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class AnnotationDefaultAttriInfo extends AttributeInfo
{
    private ElementValueInfo elementValueInfo;

    public AnnotationDefaultAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        elementValueInfo = new ElementValueInfo();
        elementValueInfo.resolve(binaryData, constantInfos);
    }

    @Override
    public String toString()
    {
        return "AnnotationDefaultAttriInfo{" + "elementValueInfo=" + elementValueInfo + '}';
    }

    public ElementValueInfo getElementValueInfo()
    {
        return elementValueInfo;
    }
}
