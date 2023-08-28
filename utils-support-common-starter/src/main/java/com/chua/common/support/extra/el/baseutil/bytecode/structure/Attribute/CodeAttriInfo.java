package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.ExceptionHandler;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import javassist.bytecode.AttributeInfo;

import java.util.Arrays;
/**
 * 基础类
 * @author CH
 */
public class CodeAttriInfo extends AbstractAttributeInfo {
    /**
     * 2字节长度
     */
    private int maxStack;
    /**
     * 2字节长度
     */
    private int maxLocals;
    private int codeLength;
    private ExceptionHandler[] exceptionHandlers;
    private AbstractAttributeInfo[] attributeInfos;

    public CodeAttriInfo(String name, int length) {
        super(name, length);
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        maxStack = binaryData.readShort();
        maxLocals = binaryData.readShort();
        codeLength = binaryData.readInt();
        //由于目前不做解析，因此忽略掉这部分code数据
        binaryData.addIndex(codeLength);
        int exceptionTableLength = binaryData.readShort();
        //目前不解析异常表信息，忽略这部分数据
        binaryData.addIndex(exceptionTableLength * 8);
        int attributesCount = binaryData.readShort();
        attributeInfos = new AbstractAttributeInfo[attributesCount];
        for (int i = 0; i < attributeInfos.length; i++) {
            attributeInfos[i] = parse(binaryData, constantInfos);
        }
    }

    public AbstractAttributeInfo[] getAttributeInfos() {
        return attributeInfos;
    }

    @Override
    public String toString() {
        return "CodeAttriInfo{" + "max_stack=" + maxStack + ", max_locals=" + maxLocals + ", code_length=" + codeLength + ", exceptionHandlers=" + Arrays.toString(exceptionHandlers) + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }
}
