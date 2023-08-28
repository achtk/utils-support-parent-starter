package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class ClassInfo extends AbstractConstantInfo
{
    private int    nameIndex;
    private String name;

    public ClassInfo()
    {
        type = ConstantType.CLASS;
    }

    public String getName()
    {
        return name;
    }

    public void setNameIndex(int nameIndex)
    {
        this.nameIndex = nameIndex;
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        nameIndex = binaryData.readShort();
    }

    @Override
    public String toString()
    {
        return "ClassInfo{" + "nameIndex=" + nameIndex + ", name='" + name + '\'' + '}';
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
        name = ((Utf8Info) constantPool[nameIndex - 1]).getValue();
    }
}
