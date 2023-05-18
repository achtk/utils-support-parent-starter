package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

public class RuntimeVisibleParameterAnnotationsAttrInfo extends AttributeInfo
{
    private int                   num_parameters;
    private ParameterAnnotation[] parameterAnnotations;

    public RuntimeVisibleParameterAnnotationsAttrInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        num_parameters = binaryData.readByte();
        parameterAnnotations = new ParameterAnnotation[num_parameters];
        for (int i = 0; i < parameterAnnotations.length; i++)
        {
            parameterAnnotations[i] = new ParameterAnnotation();
            parameterAnnotations[i].resolve(binaryData, constantInfos);
        }
    }

    @Override
    public String toString()
    {
        return "RuntimeVisibleParameterAnnotationsAttrInfo{" + "parameterAnnotations=" + Arrays.toString(parameterAnnotations) + '}';
    }

    class ParameterAnnotation
    {
        private int              num_annotations;
        private AnnotationInfo[] annotationInfos;

        void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
        {
            num_annotations = binaryData.readShort();
            annotationInfos = new AnnotationInfo[num_annotations];
            for (int i = 0; i < annotationInfos.length; i++)
            {
                annotationInfos[i] = new AnnotationInfo();
                annotationInfos[i].resolve(binaryData, constantInfos);
            }
        }

        @Override
        public String toString()
        {
            return "ParameterAnnotation{" + "annotationInfos=" + Arrays.toString(annotationInfos) + '}';
        }
    }
}
