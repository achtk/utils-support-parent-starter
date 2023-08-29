package com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;
/**
 * 基础类
 * @author CH
 */
public class RuntimeVisibleAnnotationsAttriInfo extends AbstractAttributeInfo {
    private int numAnnotations;
    private AnnotationInfo[] annotations;

    public RuntimeVisibleAnnotationsAttriInfo(String name, int length) {
        super(name, length);
    }

    @Override
    public String toString() {
        return "RuntimeVisibleAnnotationsAttriInfo{" + "num_annotations=" + numAnnotations + ", annotations=" + Arrays.toString(annotations) + '}';
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        numAnnotations = binaryData.readShort();
        annotations = new AnnotationInfo[numAnnotations];
        for (int i = 0; i < numAnnotations; i++) {
            annotations[i] = new AnnotationInfo();
            annotations[i].resolve(binaryData, constantInfos);
        }
    }

    public AnnotationInfo[] getAnnotations() {
        return annotations;
    }
}
