package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class InnerClassesAttriInfo extends AttributeInfo
{
    private int          number_of_classes;
    private InnerClass[] innerClasses;

    public InnerClassesAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        ignoreParse(binaryData);
    }

    class InnerClass
    {
        //inner_class_info_index为CONSTANT_Class常量项的索引，指明内部类的类型
        private int inner_class_info_index;
        //outer_class_info_index为CONSTANT_Class常量项的索引，指明内部类对应的外部类的类型。
        private int outer_class_info_index;
        //inner_name_index为CONSTANT_Utf8常量项的索引，表示内部类的名称。
        private int inner_name_index;
        //inner_class_access_flags表示内部类的访问属性。
        private int inner_class_access_flags;
    }
}
