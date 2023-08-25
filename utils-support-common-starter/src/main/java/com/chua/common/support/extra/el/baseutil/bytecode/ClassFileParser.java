package com.chua.common.support.extra.el.baseutil.bytecode;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.FieldInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.*;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

public class ClassFileParser
{
    private int             magic;
    private int             minor_version;
    private int             major_version;
    private int             constant_pool_count;
    private ConstantInfo[]  constant_pool;
    private int             access_flags;
    private String          this_class_name;
    private String          super_class_name;
    private String[]        interfaces;
    private FieldInfo[]     fieldInfos;
    private MethodInfo[]    methodInfos;
    private AttributeInfo[] attributeInfos;
    private BinaryData      binaryData;

    public ClassFileParser(BinaryData binaryData)
    {
        this.binaryData = binaryData;
        readMagic();
        readminorVersion();
        readmajorVersion();
        readConstantPoolCount();
        readConstantInfo();
        readAccessFlags();
        readThisClass();
        readSuperClass();
        readInterfaces();
        readFieldInfos();
        readMethodInfos();
        readAttributeInfos();
    }

    public ClassFileParser(byte[] bytecode)
    {
        this(new BinaryData(bytecode));
    }

    public ClassFile parse()
    {
        ClassFile classFile = new ClassFile();
        classFile.setAccess_flags(access_flags);
        classFile.setMinor_version(minor_version);
        classFile.setMajor_version(major_version);
        classFile.setInterfaces(interfaces);
        classFile.setSuper_class_name(super_class_name);
        classFile.setThis_class_name(this_class_name);
        classFile.setAttributeInfos(attributeInfos);
        classFile.setFieldInfos(fieldInfos);
        classFile.setMethodInfos(methodInfos);
        return classFile;
    }

    private void readAttributeInfos()
    {
        int attribute_count = binaryData.readShort();
        attributeInfos = new AttributeInfo[attribute_count];
        for (int i = 0; i < attributeInfos.length; i++)
        {
            attributeInfos[i] = AttributeInfo.parse(binaryData, constant_pool);
        }
    }

    private void readMethodInfos()
    {
        int method_count = binaryData.readShort();
        methodInfos = new MethodInfo[method_count];
        for (int i = 0; i < method_count; i++)
        {
            methodInfos[i] = new MethodInfo();
            methodInfos[i].resolve(binaryData, constant_pool);
        }
    }

    private void readFieldInfos()
    {
        int fields_cout = binaryData.readShort();
        fieldInfos = new FieldInfo[fields_cout];
        for (int i = 0; i < fields_cout; i++)
        {
            fieldInfos[i] = new FieldInfo();
            fieldInfos[i].resolve(binaryData, constant_pool);
        }
    }

    private void readInterfaces()
    {
        int interfaces_cout = binaryData.readShort();
        interfaces = new String[interfaces_cout];
        for (int i = 0; i < interfaces_cout; i++)
        {
            int interfaceIndex = binaryData.readShort();
            interfaces[i] = ((ClassInfo) constant_pool[interfaceIndex - 1]).getName();
        }
    }

    private void readSuperClass()
    {
        int super_class = binaryData.readShort();
        if (super_class == 0)
        {
            if (!this_class_name.equals("java/lang/Object"))
            {
                throw new RuntimeException("字节码解析错误，只有Object类型的父类才允许为空");
            }
            super_class_name = null;
        }
        else
        {
            super_class_name = ((ClassInfo) constant_pool[super_class - 1]).getName();
        }
    }

    private void readThisClass()
    {
        int this_class = binaryData.readShort();
        this_class_name = ((ClassInfo) constant_pool[this_class - 1]).getName();
    }

    private void readAccessFlags()
    {
        access_flags = binaryData.readShort();
    }

    private void readConstantInfo()
    {
        constant_pool = new ConstantInfo[constant_pool_count - 1];
        for (int i = 0; i < constant_pool.length; i++)
        {
            ConstantType constantType = readTag();
            ConstantInfo constantInfo;
            switch (constantType)
            {
                case UTF_8:
                    constantInfo = new Utf8Info();
                    break;
                case INTEGER:
                    constantInfo = new IntegerInfo();
                    break;
                case FLOAT:
                    constantInfo = new FloatInfo();
                    break;
                case LONG:
                    constantInfo = new LongInfo();
                    break;
                case DOUBLE:
                    constantInfo = new DoubleInfo();
                    break;
                case CLASS:
                    constantInfo = new ClassInfo();
                    break;
                case String:
                    constantInfo = new StringInfo();
                    break;
                case FIELD_REF:
                    constantInfo = new FieldRefInfo();
                    break;
                case METHOD_REF:
                    constantInfo = new MethodRefInfo();
                    break;
                case INTERFACE_METHOD_REF:
                    constantInfo = new InterfaceMethodRefInfo();
                    break;
                case NAME_AND_TYPE:
                    constantInfo = new NameAndTypeInfo();
                    break;
                case METHOD_HANDLE:
                    constantInfo = new MethodHandleInfo();
                    break;
                case METHOD_TYPE:
                    constantInfo = new MethodTypeInfo();
                    break;
                case INVOKE_DYNAMIC:
                    constantInfo = new InvokeDynamicInfo();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            constantInfo.resolve(binaryData);
            constant_pool[i] = constantInfo;
            if (constantInfo instanceof LongInfo || constantInfo instanceof DoubleInfo)
            {
                //JVM规范规定了如果遇到这两个常量类型，则编号多递增1
                i++;
            }
        }
        for (ConstantInfo constantInfo : constant_pool)
        {
            if (constantInfo != null)
            {
                constantInfo.resolve(constant_pool);
            }
        }
    }

    private ConstantType readTag()
    {
        int tag = binaryData.readByte();
        return ConstantType.byteValue(tag);
    }

    private void readConstantPoolCount()
    {
        constant_pool_count = binaryData.readShort();
    }

    private void readmajorVersion()
    {
        major_version = binaryData.readShort();
    }

    private void readminorVersion()
    {
        minor_version = binaryData.readShort();
    }

    private void readMagic()
    {
        if ((binaryData.readByte() & 0xff) == 0xca//
            && (binaryData.readByte() & 0xff) == 0xfe//
            && (binaryData.readByte() & 0xff) == 0xba//
            && (binaryData.readByte() & 0xff) == 0xbe)
        {
            magic = 0xcafebabe;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString()
    {
        return "ClassFileParser{" + "minor_version=" + minor_version + ", major_version=" + major_version + ", constant_pool_count=" + constant_pool_count + ", constant_pool=" + Arrays.toString(constant_pool) + ", access_flags=" + access_flags + ", this_class_name='" + this_class_name + '\'' + ", super_class_name='" + super_class_name + '\'' + ", interfaces=" + Arrays.toString(interfaces) + ", fieldInfos=" + Arrays.toString(fieldInfos) + ", methodInfos=" + Arrays.toString(methodInfos) + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }
}
