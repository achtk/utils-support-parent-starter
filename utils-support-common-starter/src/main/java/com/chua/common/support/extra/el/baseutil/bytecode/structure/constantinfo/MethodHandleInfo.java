package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class MethodHandleInfo extends ConstantInfo
{
    private int referenceKind;
    /**
     * reference_index为常量池项的索引，根据reference_kind值不同，指向不同类型的常量池项。
     * 当reference_kind为1、2、3、4时，为CONSTANT_Fieldref的索引值；
     * 当reference_kind为5、6、7、8时，为CONSTANT_Methodref的索引值；
     * 当reference_kind为9时，为CONSTANT_InterfaceMethodref的索引值。
     */
    private int referenceIndex;

    public MethodHandleInfo()
    {
        type = ConstantType.METHOD_HANDLE;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        referenceKind = binaryData.readByte();
        referenceIndex = binaryData.readShort();
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
    }
}
