package com.chua.common.support.extra.el.baseutil.bytecode.structure;

import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ClassNotExistAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.DefaultAnnotationMetadata;
import com.chua.common.support.extra.el.baseutil.bytecode.annotation.ValuePair;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AnnotationDefaultAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.ConstantInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.constantinfo.Utf8Info;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BinaryData;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnnotationInfo
{
    private String               type;
    private element_value_pair[] pairs;

    public void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
    {
        int type_index = binaryData.readShort();
        type = ((Utf8Info) constantInfos[type_index - 1]).getValue();
        if (type.startsWith("L"))
        {
            type = type.substring(1, type.length() - 1);
        }
        int num_element_value_pairs = binaryData.readShort();
        pairs = new element_value_pair[num_element_value_pairs];
        for (int i = 0; i < num_element_value_pairs; i++)
        {
            pairs[i] = new element_value_pair();
            pairs[i].resolve(binaryData, constantInfos);
        }
    }

    @Override
    public String toString()
    {
        return "AnnotationInfo{" + "type='" + type + '\'' + ", pairs=" + Arrays.toString(pairs) + '}';
    }

    public String getType()
    {
        return type;
    }

    public AnnotationMetadata getAnnotation(ClassLoader classLoader)
    {
        Map<String, ValuePair> elementValues = new HashMap<String, ValuePair>();
        byte[]                 bytes         = BytecodeUtil.loadBytecode(classLoader, type);
        if (bytes == null)
        {
            return new ClassNotExistAnnotationMetadata(type);
        }
        ClassFile               annotationClassFile = new ClassFileParser(new BinaryData(bytes)).parse();
        Map<String, MethodInfo> methodInfoMap       = new HashMap<String, MethodInfo>();
        for (MethodInfo methodInfo : annotationClassFile.getMethodInfos())
        {
            methodInfoMap.put(methodInfo.getName(), methodInfo);
        }
        for (MethodInfo methodInfo : annotationClassFile.getMethodInfos())
        {
            for (AttributeInfo attributeInfo : methodInfo.getAttributeInfos())
            {
                if (attributeInfo instanceof AnnotationDefaultAttriInfo)
                {
                    elementValues.put(methodInfo.getName(), ((AnnotationDefaultAttriInfo) attributeInfo).getElementValueInfo().getValue(classLoader, methodInfo));
                    break;
                }
            }
        }
        for (element_value_pair pair : pairs)
        {
            String           name  = pair.getElementName();
            ElementValueInfo value = pair.getValue();
            elementValues.put(name, value.getValue(classLoader, methodInfoMap.get(name)));
        }
        return new DefaultAnnotationMetadata(type, elementValues, classLoader);
    }

    class element_value_pair
    {
        private String           elementName;
        private ElementValueInfo value;

        void resolve(BinaryData binaryData, ConstantInfo[] constantInfos)
        {
            int element_name_index = binaryData.readShort();
            elementName = ((Utf8Info) constantInfos[element_name_index - 1]).getValue();
            value = new ElementValueInfo();
            value.resolve(binaryData, constantInfos);
        }

        String getElementName()
        {
            return elementName;
        }

        ElementValueInfo getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "element_value_pair{" + "elementName='" + elementName + '\'' + ", value=" + value + '}';
        }
    }
}
