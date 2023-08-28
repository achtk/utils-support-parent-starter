package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class NameAndTypeInfo extends AbstractConstantInfo
{
    private int    nameIndex;
    private int    descriptorIndex;
    private String name;
    private String descriptor;

    public NameAndTypeInfo()
    {
        type = ConstantType.NAME_AND_TYPE;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        nameIndex = binaryData.readShort();
        descriptorIndex = binaryData.readShort();
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
        name = ((Utf8Info) constantPool[nameIndex - 1]).getValue();
        descriptor = ((Utf8Info) constantPool[descriptorIndex - 1]).getValue();
    }
}
