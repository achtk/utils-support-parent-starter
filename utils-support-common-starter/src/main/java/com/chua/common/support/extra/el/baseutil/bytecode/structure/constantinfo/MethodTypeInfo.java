package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class MethodTypeInfo extends AbstractConstantInfo
{
    /**
     * descriptor_index为CONSTANT_Utf8类型常量项的索引，里面存储了方法描述符的字符串
     */
    private int descriptorIndex;

    public MethodTypeInfo()
    {
        type = ConstantType.METHOD_TYPE;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        descriptorIndex = binaryData.readShort();
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
    }
}
