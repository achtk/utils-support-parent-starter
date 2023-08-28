package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.ElementValueInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class AnnotationDefaultAttriInfo extends AbstractAttributeInfo {
    private ElementValueInfo elementValueInfo;

    public AnnotationDefaultAttriInfo(String name, int length) {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        elementValueInfo = new ElementValueInfo();
        elementValueInfo.resolve(binaryData, constantInfos);
    }

    @Override
    public String toString() {
        return "AnnotationDefaultAttriInfo{" + "elementValueInfo=" + elementValueInfo + '}';
    }

    public ElementValueInfo getElementValueInfo() {
        return elementValueInfo;
    }
}
