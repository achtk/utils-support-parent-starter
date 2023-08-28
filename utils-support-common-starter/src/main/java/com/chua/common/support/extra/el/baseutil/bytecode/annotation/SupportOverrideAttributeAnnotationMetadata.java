package com.chua.common.support.extra.el.baseutil.bytecode.annotation;

import com.chua.common.support.extra.el.baseutil.bytecode.ClassFile;
import com.chua.common.support.extra.el.baseutil.bytecode.ClassFileParser;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.AnnotationInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.AbstractAttributeInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.Attribute.RuntimeVisibleAnnotationsAttriInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.structure.MethodInfo;
import com.chua.common.support.extra.el.baseutil.bytecode.support.OverridesAttribute;
import com.chua.common.support.extra.el.baseutil.bytecode.util.BytecodeUtil;

import java.util.*;

public class SupportOverrideAttributeAnnotationMetadata extends AbstractAnnotationMetadata
{
    static  String                          name = OverridesAttribute.class.getName()
                                                                           .replace('.', '/');
    private Map<String, List<OverrideItem>> map  = new HashMap<String, List<OverrideItem>>();

    private SupportOverrideAttributeAnnotationMetadata(String resourceName, Map<String, ValuePair> attributes, ClassLoader loader)
    {
        super(resourceName, attributes, loader);
        byte[] bytecode = BytecodeUtil.loadBytecode(loader, resourceName);
        ClassFile classFile = new ClassFileParser(bytecode).parse();
        class StreamData
        {
            MethodInfo     methodInfo;
            AbstractAttributeInfo attributeInfo;
            AnnotationInfo annotationInfo;

            public StreamData(MethodInfo methodInfo, AbstractAttributeInfo attributeInfo)
            {
                this.methodInfo = methodInfo;
                this.attributeInfo = attributeInfo;
            }

            public StreamData(MethodInfo methodInfo, AnnotationInfo annotationInfo)
            {
                this.methodInfo = methodInfo;
                this.annotationInfo = annotationInfo;
            }
        }
        Arrays.stream(classFile.getMethodInfos())
              .flatMap(methodInfo -> Arrays.stream(methodInfo.getAttributeInfos())
                                           .map(attributeInfo -> new StreamData(methodInfo, attributeInfo)))
              .filter(data -> data.attributeInfo instanceof RuntimeVisibleAnnotationsAttriInfo)
              .flatMap(data -> Arrays.stream(((RuntimeVisibleAnnotationsAttriInfo) data.attributeInfo).getAnnotations())
                                     .map(annotation -> new StreamData(data.methodInfo, annotation)))
              .filter(data -> data.annotationInfo.getType()
                                                 .equals(name))
              .forEach(data -> {
                  AnnotationMetadata overrideAttribute = data.annotationInfo.getAnnotation(loader);
                  ValuePair valuePair = overrideAttribute.getAttribyte("annotation");
                  String annotationResourceName = valuePair.getClassName()
                                                           .replace('.', '/');
                  String name = overrideAttribute.getAttribyte("name")
                                                 .getStringValue();
                  OverrideItem overrideItem = new OverrideItem();
                  overrideItem.overrideAnnotationName = annotationResourceName;
                  overrideItem.overrideAttribute = name;
                  overrideItem.attribute = data.methodInfo.getName();
                  overrideItem.valuePair = attributes.get(data.methodInfo.getName());
                  List<OverrideItem> overrideItems = map.get(annotationResourceName);
                  if (overrideItems == null)
                  {
                      overrideItems = new LinkedList<OverrideItem>();
                      map.put(annotationResourceName, overrideItems);
                  }
                  overrideItems.add(overrideItem);
              });
//        for (MethodInfo methodInfo : classFile.getMethodInfos())
//        {
//            for (AttributeInfo attributeInfo : methodInfo.getAttributeInfos())
//            {
//                if (attributeInfo instanceof RuntimeVisibleAnnotationsAttriInfo)
//                {
//                    for (AnnotationInfo annotation : ((RuntimeVisibleAnnotationsAttriInfo) attributeInfo).getAnnotations())
//                    {
//                        if (annotation.getType()
//                                .equals(name))
//                        {
//                            AnnotationMetadata overrideAttribute = annotation.getAnnotation(loader);
//                            ValuePair valuePair = overrideAttribute.getAttribyte("annotation");
//                            String annotationResourceName = valuePair.getClassName()
//                                    .replace('.', '/');
//                            String name = overrideAttribute.getAttribyte("name")
//                                    .getStringValue();
//                            OverrideItem overrideItem = new OverrideItem();
//                            overrideItem.overrideAnnotationName = annotationResourceName;
//                            overrideItem.overrideAttribute = name;
//                            overrideItem.attribute = methodInfo.getName();
//                            overrideItem.valuePair = attributes.get(methodInfo.getName());
//                            List<OverrideItem> overrideItems = map.get(annotationResourceName);
//                            if (overrideItems == null)
//                            {
//                                overrideItems = new LinkedList<OverrideItem>();
//                                map.put(annotationResourceName, overrideItems);
//                            }
//                            overrideItems.add(overrideItem);
//                            break;
//                        }
//                    }
//                    break;
//                }
//            }
//        }
    }

    public static SupportOverrideAttributeAnnotationMetadata castFrom(DefaultAnnotationMetadata defaultAnnotationMetadata)
    {
        return new SupportOverrideAttributeAnnotationMetadata(defaultAnnotationMetadata.getResourceName(), defaultAnnotationMetadata.getAttributes(), defaultAnnotationMetadata.getLoader());
    }

    @Override
    public List<AnnotationMetadata> getPresentAnnotations()
    {
        if (presentAnnotations == null)
        {
            List<AnnotationMetadata> tmp = BytecodeUtil.findAnnotationsOnClass(type(), this.getClass()
                                                                                           .getClassLoader());
            presentAnnotations = new LinkedList<AnnotationMetadata>();
            for (AnnotationMetadata annotationMetadata : tmp)
            {
                if (annotationMetadata instanceof DefaultAnnotationMetadata == false)
                {
                    throw new UnsupportedOperationException();
                }
                SupportOverrideAttributeAnnotationMetadata castFrom = castFrom((DefaultAnnotationMetadata) annotationMetadata);
                if (map.containsKey(castFrom.getResourceName()))
                {
                    List<OverrideItem> overrideItems = map.get(castFrom.getResourceName());
                    for (OverrideItem each : overrideItems)
                    {
                        castFrom.attributes.put(each.overrideAttribute, each.valuePair);
                    }
                }
                presentAnnotations.add(castFrom);
            }
        }
        return presentAnnotations;
    }

    class OverrideItem
    {
        String    overrideAnnotationName;
        String    overrideAttribute;
        String    attribute;
        ValuePair valuePair;
    }
}
