package com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute;

import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;
import java.util.Comparator;
/**
 * 基础类
 * @author CH
 */
public class LocalVariableTableAttriInfo extends AbstractAttributeInfo {
    private int localVariableTableLength;
    private LocalVariableTableEntry[] entries;

    public LocalVariableTableAttriInfo(String name, int length) {
        super(name, length);
    }

    @Override
    public String toString() {
        return "LocalVariableTableAttriInfo{" + "entries=" + Arrays.toString(entries) + '}';
    }

    @Override
    protected void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        localVariableTableLength = binaryData.readShort();
        entries = new LocalVariableTableEntry[localVariableTableLength];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new LocalVariableTableEntry();
            entries[i].resolve(binaryData, constantInfos);
        }
        Arrays.sort(entries, new Comparator<LocalVariableTableEntry>() {
            @Override
            public int compare(LocalVariableTableEntry o1, LocalVariableTableEntry o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
    }

    public LocalVariableTableEntry[] getEntries() {
        return entries;
    }

    public class LocalVariableTableEntry {
        private int startPc;
        private int length;
        private int nameIndex;
        private int descriptorIndex;
        private int index;
        private String name;
        private String descriptor;

        void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
            //忽略start_pc
            binaryData.addIndex(2);
            //忽略length
            binaryData.addIndex(2);
            nameIndex = binaryData.readShort();
            name = ((Utf8Info) constantInfos[nameIndex - 1]).getValue();
            descriptorIndex = binaryData.readShort();
            descriptor = ((Utf8Info) constantInfos[descriptorIndex - 1]).getValue();
            index = binaryData.readShort();
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        @Override
        public String toString() {
            return "LocalVariableTableEntry{" + "index=" + index + ", name='" + name + '\'' + '}';
        }
    }
}
