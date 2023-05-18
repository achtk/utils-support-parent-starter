package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

public class RuntimeVisibleAnnotationsAttriInfo extends AttributeInfo
{
    private int              num_annotations;
    private AnnotationInfo[] annotations;

    public RuntimeVisibleAnnotationsAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    public String toString()
    {
        return "RuntimeVisibleAnnotationsAttriInfo{" + "num_annotations=" + num_annotations + ", annotations=" + Arrays.toString(annotations) + '}';
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        num_annotations = binaryData.readShort();
        annotations = new AnnotationInfo[num_annotations];
        for (int i = 0; i < num_annotations; i++)
        {
            annotations[i] = new AnnotationInfo();
            annotations[i].resolve(binaryData, constantInfos);
        }
    }

    public AnnotationInfo[] getAnnotations()
    {
        return annotations;
    }
}
