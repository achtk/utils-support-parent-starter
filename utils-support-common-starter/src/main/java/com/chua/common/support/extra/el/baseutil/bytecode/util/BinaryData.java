package com.chua.common.support.extra.el.baseutil.bytecode.util;

public class BinaryData
{
    private byte[] value;
    private int    index;

    public BinaryData(byte[] value)
    {
        this.value = value;
        index = 0;
    }

    public byte readByte()
    {
        byte b = value[index];
        index += 1;
        return b;
    }

    public void addIndex(int add)
    {
        index += add;
    }

    public int readShort()
    {
        int result = ((0xff & value[index]) << 8) | (0xff & value[index + 1]);
        index += 2;
        return result;
    }

    public int readInt()
    {
        int result = ((value[index] & 0xff) << 24)//
                | ((value[index + 1] & 0xff) << 16)//
                | ((value[index + 2] & 0xff) << 8)//
                | ((value[index + 3] & 0xff) << 0);
        index += 4;
        return result;
    }

    public long readLong()
    {
        long result = (((long) value[index] & 0xffL) << 56)//
                | (((long) value[index + 1] & 0xffL) << 48) //
                | (((long) value[index + 2] & 0xffL) << 40) //
                | (((long) value[index + 3] & 0xffL) << 32) //
                | (((long) value[index + 4] & 0xffL) << 24) //
                | (((long) value[index + 5] & 0xffL) << 16) //
                | (((long) value[index + 6] & 0xffL) << 8) //
                | (((long) value[index + 7] & 0xffL));//
        index += 8;
        return result;
    }

    public void read(byte[] dest)
    {
        System.arraycopy(value, index, dest, 0, dest.length);
        index += dest.length;
    }
}
