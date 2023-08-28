package com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.nio.charset.Charset;
/**
 * 基础类
 * @author CH
 */
public class Utf8Info extends AbstractConstantInfo
{
    private static final Charset CHARSET = Charset.forName("utf8");
    private              int     length;
    private              String  value;

    public Utf8Info()
    {
        type = ConstantType.UTF_8;
    }

    public int getLength()
    {
        return length;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "Utf8Info{" + value + '}';
    }

    @Override
    public void resolve(BinaryData binaryData)
    {
        length = binaryData.readShort();
        byte[] content = new byte[length];
        binaryData.read(content);
        value = new String(content, CHARSET);
    }

    @Override
    public void resolve(AbstractConstantInfo[] constantPool)
    {
    }
}
