package com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public class ConstantValueAttrInfo extends AbstractAttributeInfo
{
    /**
     * 定长2个字节描述的常量池索引
     */
    private int index;

    public ConstantValueAttrInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos)
    {
        binaryData.addIndex(length);
    }
}
