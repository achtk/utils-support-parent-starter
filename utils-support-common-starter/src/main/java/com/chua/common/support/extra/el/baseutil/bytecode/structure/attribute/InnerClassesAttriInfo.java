package com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class InnerClassesAttriInfo extends AbstractAttributeInfo {
    private int numberOfClasses;
    private InnerClass[] innerClasses;

    public InnerClassesAttriInfo(String name, int length) {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        ignoreParse(binaryData);
    }

    class InnerClass {
        //inner_class_info_index为CONSTANT_Class常量项的索引，指明内部类的类型
        private int innerClassInfoIndex;
        //outer_class_info_index为CONSTANT_Class常量项的索引，指明内部类对应的外部类的类型。
        private int outerClassInfoIndex;
        //inner_name_index为CONSTANT_Utf8常量项的索引，表示内部类的名称。
        private int innerNameIndex;
        //inner_class_access_flags表示内部类的访问属性。
        private int innerClassAccessFlags;
    }
}
