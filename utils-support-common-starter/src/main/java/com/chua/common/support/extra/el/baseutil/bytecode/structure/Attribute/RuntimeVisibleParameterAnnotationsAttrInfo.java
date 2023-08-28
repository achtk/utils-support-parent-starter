package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;
/**
 * 基础类
 * @author CH
 */
public class RuntimeVisibleParameterAnnotationsAttrInfo extends AbstractAttributeInfo {
    private int numParameters;
    private ParameterAnnotation[] parameterAnnotations;

    public RuntimeVisibleParameterAnnotationsAttrInfo(String name, int length) {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        numParameters = binaryData.readByte();
        parameterAnnotations = new ParameterAnnotation[numParameters];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            parameterAnnotations[i] = new ParameterAnnotation();
            parameterAnnotations[i].resolve(binaryData, constantInfos);
        }
    }

    @Override
    public String toString() {
        return "RuntimeVisibleParameterAnnotationsAttrInfo{" + "parameterAnnotations=" + Arrays.toString(parameterAnnotations) + '}';
    }

    class ParameterAnnotation {
        private int numAnnotations;
        private AnnotationInfo[] annotationInfos;

        void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
            numAnnotations = binaryData.readShort();
            annotationInfos = new AnnotationInfo[numAnnotations];
            for (int i = 0; i < annotationInfos.length; i++) {
                annotationInfos[i] = new AnnotationInfo();
                annotationInfos[i].resolve(binaryData, constantInfos);
            }
        }

        @Override
        public String toString() {
            return "ParameterAnnotation{" + "annotationInfos=" + Arrays.toString(annotationInfos) + '}';
        }
    }
}
