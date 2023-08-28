package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import javassist.bytecode.AttributeInfo;

public class ExceptionsAttriInfo extends AbstractAttributeInfo
{
    private int numberOfExceptions;
    /**
     * exception_index_table列表每项为CONSTANT_Class常量项的索引，表示具体的异常类
     */
    private int[] exceptionIndexTable;

    public ExceptionsAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos)
    {
        ignoreParse(binaryData);
    }
}
