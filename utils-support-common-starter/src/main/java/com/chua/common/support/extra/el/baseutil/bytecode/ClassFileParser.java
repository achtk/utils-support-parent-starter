package com.chua.common.support.extra.el.baseutil.bytecode;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.FieldInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.*;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;

import java.util.Arrays;

import static com.chua.common.support.constant.NameConstant.OBJECT_NAME;
/**
 * 基础类
 * @author CH
 */
public class ClassFileParser {
    private int magic;
    private int minorVersion;
    private int majorVersion;
    private int constantPoolCount;
    private AbstractConstantInfo[] constantPool;
    private int accessFlags;
    private String thisClassName;
    private String superClassName;
    private String[] interfaces;
    private FieldInfo[] fieldInfos;
    private MethodInfo[] methodInfos;
    private AbstractAttributeInfo[] attributeInfos;
    private BinaryData binaryData;

    public ClassFileParser(BinaryData binaryData) {
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

    public ClassFileParser(byte[] bytecode) {
        this(new BinaryData(bytecode));
    }

    public ClassFile parse() {
        ClassFile classFile = new ClassFile();
        classFile.setAccessFlags(accessFlags);
        classFile.setMinorVersion(minorVersion);
        classFile.setMajorVersion(majorVersion);
        classFile.setInterfaces(interfaces);
        classFile.setSuperClassName(superClassName);
        classFile.setThisClassName(thisClassName);
        classFile.setAttributeInfos(attributeInfos);
        classFile.setFieldInfos(fieldInfos);
        classFile.setMethodInfos(methodInfos);
        return classFile;
    }

    private void readAttributeInfos() {
        int attributeCount = binaryData.readShort();
        attributeInfos = new AbstractAttributeInfo[attributeCount];
        for (int i = 0; i < attributeInfos.length; i++) {
            attributeInfos[i] = AbstractAttributeInfo.parse(binaryData, constantPool);
        }
    }

    private void readMethodInfos() {
        int methodCount = binaryData.readShort();
        methodInfos = new MethodInfo[methodCount];
        for (int i = 0; i < methodCount; i++) {
            methodInfos[i] = new MethodInfo();
            methodInfos[i].resolve(binaryData, constantPool);
        }
    }

    private void readFieldInfos() {
        int fieldsCout = binaryData.readShort();
        fieldInfos = new FieldInfo[fieldsCout];
        for (int i = 0; i < fieldsCout; i++) {
            fieldInfos[i] = new FieldInfo();
            fieldInfos[i].resolve(binaryData, constantPool);
        }
    }

    private void readInterfaces() {
        int interfacesCout = binaryData.readShort();
        interfaces = new String[interfacesCout];
        for (int i = 0; i < interfacesCout; i++) {
            int interfaceIndex = binaryData.readShort();
            interfaces[i] = ((ClassInfo) constantPool[interfaceIndex - 1]).getName();
        }
    }

    private void readSuperClass() {
        int superClass = binaryData.readShort();
        if (superClass == 0) {
            if (!OBJECT_NAME.equals(thisClassName)) {
                throw new RuntimeException("字节码解析错误，只有Object类型的父类才允许为空");
            }
            superClassName = null;
        } else {
            superClassName = ((ClassInfo) constantPool[superClass - 1]).getName();
        }
    }

    private void readThisClass() {
        int thisClass = binaryData.readShort();
        thisClassName = ((ClassInfo) constantPool[thisClass - 1]).getName();
    }

    private void readAccessFlags() {
        accessFlags = binaryData.readShort();
    }

    private void readConstantInfo() {
        constantPool = new AbstractConstantInfo[constantPoolCount - 1];
        for (int i = 0; i < constantPool.length; i++) {
            ConstantType constantType = readTag();
            AbstractConstantInfo constantInfo;
            switch (constantType) {
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
                case STRING:
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
            constantPool[i] = constantInfo;
            if (constantInfo instanceof LongInfo || constantInfo instanceof DoubleInfo) {
                //JVM规范规定了如果遇到这两个常量类型，则编号多递增1
                i++;
            }
        }
        for (AbstractConstantInfo constantInfo : constantPool) {
            if (constantInfo != null) {
                constantInfo.resolve(constantPool);
            }
        }
    }

    private ConstantType readTag() {
        int tag = binaryData.readByte();
        return ConstantType.byteValue(tag);
    }

    private void readConstantPoolCount() {
        constantPoolCount = binaryData.readShort();
    }

    private void readmajorVersion() {
        majorVersion = binaryData.readShort();
    }

    private void readminorVersion() {
        minorVersion = binaryData.readShort();
    }

    private void readMagic() {
        int xff = 0xff;
        int xca = 0xca;
        if ((binaryData.readByte() & xff) == xca
                && (binaryData.readByte() & 0xff) == 0xfe
                && (binaryData.readByte() & 0xff) == 0xba
                && (binaryData.readByte() & 0xff) == 0xbe) {
            magic = 0xcafebabe;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "ClassFileParser{" + "minor_version=" + minorVersion + ", major_version=" + majorVersion + ", constant_pool_count=" + constantPoolCount + ", constant_pool=" + Arrays.toString(constantPool) + ", access_flags=" + accessFlags + ", this_class_name='" + thisClassName + '\'' + ", super_class_name='" + superClassName + '\'' + ", interfaces=" + Arrays.toString(interfaces) + ", fieldInfos=" + Arrays.toString(fieldInfos) + ", methodInfos=" + Arrays.toString(methodInfos) + ", attributeInfos=" + Arrays.toString(attributeInfos) + '}';
    }
}
