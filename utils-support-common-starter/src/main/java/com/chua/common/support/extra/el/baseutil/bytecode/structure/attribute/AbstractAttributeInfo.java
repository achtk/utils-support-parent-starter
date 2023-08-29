package com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

/**
 * 基础类
 * @author CH
 */
public abstract class AbstractAttributeInfo {
    private static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
    private static final String RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations";
    private static final String ANNOTATION_DEFAULT = "AnnotationDefault";
    private static final String CODE = "Code";
    private static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";
    /**
     * 2个字节的索引
     */
    protected String name;
    /**
     * 4个字节的bytes长度
     */
    protected int length;

    public AbstractAttributeInfo(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public static AbstractAttributeInfo parse(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        int nameIndex = binaryData.readShort();
        int length = binaryData.readInt();
        String name = ((Utf8Info) constantInfos[nameIndex - 1]).getValue();
        AbstractAttributeInfo info;
        if (RUNTIME_VISIBLE_ANNOTATIONS.equals(name)) {
            info = new RuntimeVisibleAnnotationsAttriInfo(name, length);
        } else if (RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS.equals(name)) {
            info = new RuntimeVisibleParameterAnnotationsAttrInfo(name, length);
        } else if (ANNOTATION_DEFAULT.equals(name)) {
            info = new AnnotationDefaultAttriInfo(name, length);
        } else if (CODE.equals(name)) {
            info = new CodeAttriInfo(name, length);
        } else if (LOCAL_VARIABLE_TABLE.equals(name)) {
            info = new LocalVariableTableAttriInfo(name, length);
        } else {
            info = new UnknowAttrInfo(name, length);
        }
        info.resolve(binaryData, constantInfos);
        return info;
    }

    public int getLength() {
        return length;
    }

    /**
     * 处理数据
     * @param binaryData 数据
     * @param constantInfos 常量
     */
    protected abstract void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos);
    /**
     * 处理数据
     * @param binaryData 数据
     */
    protected void ignoreParse(BinaryData binaryData) {
        binaryData.addIndex(length);
    }

    @Override
    public String toString() {
        return "AttributeInfo{" + "name='" + name + '\'' + ", length=" + length + '}';
    }
}
