package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ClassNotExistAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.DefaultAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ValuePair;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AnnotationDefaultAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.AbstractConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础类
 *
 * @author CH
 */
public class AnnotationInfo {
    private String type;
    private ElementValuePair[] pairs;

    public void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
        int typeIndex = binaryData.readShort();
        type = ((Utf8Info) constantInfos[typeIndex - 1]).getValue();
        if (type.startsWith("L")) {
            type = type.substring(1, type.length() - 1);
        }
        int numElementValuePairs = binaryData.readShort();
        pairs = new ElementValuePair[numElementValuePairs];
        for (int i = 0; i < numElementValuePairs; i++) {
            pairs[i] = new ElementValuePair();
            pairs[i].resolve(binaryData, constantInfos);
        }
    }

    @Override
    public String toString() {
        return "AnnotationInfo{" + "type='" + type + '\'' + ", pairs=" + Arrays.toString(pairs) + '}';
    }

    public String getType() {
        return type;
    }

    public AnnotationMetadata getAnnotation(ClassLoader classLoader) {
        Map<String, ValuePair> elementValues = new HashMap<>(1 << 4);
        byte[] bytes = BytecodeUtil.loadBytecode(classLoader, type);
        if (bytes == null) {
            return new ClassNotExistAnnotationMetadata(type);
        }
        ClassFile annotationClassFile = new ClassFileParser(new BinaryData(bytes)).parse();
        Map<String, MethodInfo> methodInfoMap = new HashMap<>(1 << 4);
        for (MethodInfo methodInfo : annotationClassFile.getMethodInfos()) {
            methodInfoMap.put(methodInfo.getName(), methodInfo);
        }
        for (MethodInfo methodInfo : annotationClassFile.getMethodInfos()) {
            for (AbstractAttributeInfo attributeInfo : methodInfo.getAttributeInfos()) {
                if (attributeInfo instanceof AnnotationDefaultAttriInfo) {
                    elementValues.put(methodInfo.getName(), ((AnnotationDefaultAttriInfo) attributeInfo).getElementValueInfo().getValue(classLoader, methodInfo));
                    break;
                }
            }
        }
        for (ElementValuePair pair : pairs) {
            String name = pair.getElementName();
            ElementValueInfo value = pair.getValue();
            elementValues.put(name, value.getValue(classLoader, methodInfoMap.get(name)));
        }
        return new DefaultAnnotationMetadata(type, elementValues, classLoader);
    }

    class ElementValuePair {
        private String elementName;
        private ElementValueInfo value;

        void resolve(BinaryData binaryData, AbstractConstantInfo[] constantInfos) {
            int elementNameIndex = binaryData.readShort();
            elementName = ((Utf8Info) constantInfos[elementNameIndex - 1]).getValue();
            value = new ElementValueInfo();
            value.resolve(binaryData, constantInfos);
        }

        String getElementName() {
            return elementName;
        }

        ElementValueInfo getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "ElementValuePair{" + "elementName='" + elementName + '\'' + ", value=" + value + '}';
        }
    }
}
