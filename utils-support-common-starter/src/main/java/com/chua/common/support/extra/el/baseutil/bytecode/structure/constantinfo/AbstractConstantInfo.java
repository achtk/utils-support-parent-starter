package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
/**
 * 基础类
 * @author CH
 */
public abstract class AbstractConstantInfo {
    protected ConstantType type;

    /**
     * 该常量类型解析除了tag字节以外的内容，并且返回解析完成后计数器的值
     * @param binaryData 二级制数据
     */
    public abstract void resolve(BinaryData binaryData);

    /**
     * 使用常量池中的数据解析一些描述字符串链接等
     *
     * @param constantPool 二级制数据
     */
    public abstract void resolve(AbstractConstantInfo[] constantPool);
}
