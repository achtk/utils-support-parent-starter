package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

public class InvokeDynamicInfo extends AbstractConstantInfo
{
    /**
     * class文件中attributes属性的索引
     */
    private int bootstrapMethodAttrIndex;
    private int nameAndTypeIndex;

    public InvokeDynamicInfo()
    {
        type = ConstantType.INVOKE_DYNAMIC;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        bootstrapMethodAttrIndex = binaryData.readShort();
        nameAndTypeIndex = binaryData.readShort();
    }

    @Override
    public String toString()
    {
        return "InvokeDynamicInfo{" + "bootstrap_method_attr_index=" + bootstrapMethodAttrIndex + ", name_and_type_index=" + nameAndTypeIndex + '}';
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
    }
}
