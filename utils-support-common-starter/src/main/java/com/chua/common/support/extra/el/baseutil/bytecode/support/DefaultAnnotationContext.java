package com.chua.common.support.extra.el.baseutil.bytecode.support;

import com.chua.common.support.extra.el.baseutil.bytecode.annotation.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class DefaultAnnotationContext implements AnnotationContext
{
    private final List<AnnotationMetadata>                metadataList;
    private       Map<Class<?>, List<AnnotationMetadata>> metadataStore;
    private       Map<Class<?>, List<Annotation>>         annotationStore;

    public DefaultAnnotationContext(List<AnnotationMetadata> metadataList)
    {
        this.metadataList = metadataList;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> ckass)
    {
        String resourceName = ckass.getName().replace('.', '/');
        for (AnnotationMetadata annotationMetadata : metadataList)
        {
            if (find(annotationMetadata, resourceName) != null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public <E extends Annotation> E getAnnotation(Class<E> ckass)
    {
        return (E) getAnnotationMetadata(ckass).annotation();
    }

    private AnnotationMetadata find(AnnotationMetadata metadata, String resourceName)
    {
        if (metadata.isAnnotation(resourceName))
        {
            return metadata;
        }
        for (AnnotationMetadata presentAnnotation : metadata.getPresentAnnotations())
        {
            AnnotationMetadata result = find(presentAnnotation, resourceName);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    private void findAll(AnnotationMetadata metadata, String resourceName, List<AnnotationMetadata> list)
    {
        if (metadata.isAnnotation(resourceName))
        {
            list.add(metadata);
        }
        for (AnnotationMetadata presentAnnotation : metadata.getPresentAnnotations())
        {
            findAll(presentAnnotation, resourceName, list);
        }
    }

    @Override
    public <E extends Annotation> List<E> getAnnotations(Class<E> ckass)
    {
        if (getAnnotationStore().containsKey(ckass))
        {
            return (List<E>) getAnnotationStore().get(ckass);
        }
        List<E> list = new LinkedList<E>();
        for (AnnotationMetadata each : getAnnotationMetadatas(ckass))
        {
            list.add((E) each.annotation());
        }
        getAnnotationStore().put(ckass, (List<Annotation>) list);
        return list;
    }

    private Map<Class<?>, List<Annotation>> getAnnotationStore()
    {
        if (annotationStore != null)
        {
            return annotationStore;
        }
        annotationStore = new IdentityHashMap<Class<?>, List<Annotation>>();
        return annotationStore;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata(Class<? extends Annotation> ckass)
    {
        String resourceName = ckass.getName().replace('.', '/');
        for (AnnotationMetadata each : metadataList)
        {
            AnnotationMetadata metadata = find(each, resourceName);
            if (metadata != null)
            {
                return metadata;
            }
        }
        return null;
    }

    @Override
    public List<AnnotationMetadata> getAnnotationMetadatas(Class<? extends Annotation> ckass)
    {
        if (getMetadataStore().containsKey(ckass))
        {
            return getMetadataStore().get(ckass);
        }
        String                   resourceName = ckass.getName().replace('.', '/');
        List<AnnotationMetadata> result       = new LinkedList<AnnotationMetadata>();
        for (AnnotationMetadata each : metadataList)
        {
            findAll(each, resourceName, result);
        }
        getMetadataStore().put(ckass, result);
        return result;
    }

    private Map<Class<?>, List<AnnotationMetadata>> getMetadataStore()
    {
        if (metadataStore != null)
        {
            return metadataStore;
        }
        metadataStore = new IdentityHashMap<Class<?>, List<AnnotationMetadata>>();
        return metadataStore;
    }
}
