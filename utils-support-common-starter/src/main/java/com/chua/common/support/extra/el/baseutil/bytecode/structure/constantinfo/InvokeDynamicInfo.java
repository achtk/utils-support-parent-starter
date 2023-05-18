package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.ConstantType;

public class InvokeDynamicInfo extends ConstantInfo
{
    /**
     * class文件中attributes属性的索引
     */
    private int bootstrap_method_attr_index;
    private int name_and_type_index;

    public InvokeDynamicInfo()
    {
        type = ConstantType.InvokeDynamic;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        bootstrap_method_attr_index = binaryData.readShort();
        name_and_type_index = binaryData.readShort();
    }

    @Override
    public String toString()
    {
        return "InvokeDynamicInfo{" + "bootstrap_method_attr_index=" + bootstrap_method_attr_index + ", name_and_type_index=" + name_and_type_index + '}';
    }

    @Override
    public void resolve(ConstantInfo[] constant_pool)
    {
    }
}
