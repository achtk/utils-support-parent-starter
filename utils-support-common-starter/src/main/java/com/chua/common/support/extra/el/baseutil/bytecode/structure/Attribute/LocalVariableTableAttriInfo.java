package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;
import java.util.Comparator;

public class LocalVariableTableAttriInfo extends AttributeInfo
{
    private int                       local_variable_table_length;
    private LocalVariableTableEntry[] entries;

    public LocalVariableTableAttriInfo(String name, int length)
    {
        super(name, length);
    }

    @Override
    public String toString()
    {
        return "LocalVariableTableAttriInfo{" + "entries=" + Arrays.toString(entries) + '}';
    }

    @Override
    protected void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        local_variable_table_length = binaryData.readShort();
        entries = new LocalVariableTableEntry[local_variable_table_length];
        for (int i = 0; i < entries.length; i++)
        {
            entries[i] = new LocalVariableTableEntry();
            entries[i].resolve(binaryData, constantInfos);
        }
        Arrays.sort(entries, new Comparator<LocalVariableTableEntry>()
        {
            @Override
            public int compare(LocalVariableTableEntry o1, LocalVariableTableEntry o2)
            {
                return o1.getIndex() - o2.getIndex();
            }
        });
    }

    public LocalVariableTableEntry[] getEntries()
    {
        return entries;
    }

    public class LocalVariableTableEntry
    {
        private int    start_pc;
        private int    length;
        private int    name_index;
        private int    descriptor_index;
        private int    index;
        private String name;
        private String descriptor;

        void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
        {
            //忽略start_pc
            binaryData.addIndex(2);
            //忽略length
            binaryData.addIndex(2);
            name_index = binaryData.readShort();
            name = ((Utf8Info) constantInfos[name_index - 1]).getValue();
            descriptor_index = binaryData.readShort();
            descriptor = ((Utf8Info) constantInfos[descriptor_index - 1]).getValue();
            index = binaryData.readShort();
        }

        public int getIndex()
        {
            return index;
        }

        public String getName()
        {
            return name;
        }

        public String getDescriptor()
        {
            return descriptor;
        }

        @Override
        public String toString()
        {
            return "LocalVariableTableEntry{" + "index=" + index + ", name='" + name + '\'' + '}';
        }
    }
}
