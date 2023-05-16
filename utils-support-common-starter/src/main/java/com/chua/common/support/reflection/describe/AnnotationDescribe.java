package com.chua.common.support.reflection.describe;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.CollectionUtils;
import lombok.Data;

import java.lang.annotation.*;
import java.util.*;

/**
 * 注解
 *
 * @author CH
 */
@Data
public class AnnotationDescribe {
    /**
     * 注解名称
     */
    private String name;
    /**
     * 注解
     */
    private Annotation annotation;
    /**
     * 注解参数
     */
    private List<AnnotationParameterDescribe> annotationParameterDescribes;
    /**
     * 注解
     */
    private AnnotationDescribe[] annotationDescribes;

    /**
     * 添加注解
     *
     * @param item 注解参数
     */
    public void addAnnotationParameter(AnnotationParameterDescribe item) {
        if (null == annotationParameterDescribes) {
            synchronized (this) {
                if (null == annotationParameterDescribes) {
                    annotationParameterDescribes = new LinkedList<>();
                }
            }
        }

        annotationParameterDescribes.add(item);
    }

    /**
     * 初始化
     *
     * @param annotation 注解
     * @return 描述
     */
    public static AnnotationDescribe of(Annotation annotation) {
        AnnotationDescribe item = new AnnotationDescribe();
        item.setAnnotation(annotation);
        if (null != annotation) {
            item.setName(annotation.annotationType().getName());
            Annotation[] declaredAnnotations = annotation.annotationType().getDeclaredAnnotations();
            List<AnnotationDescribe> annotationDescribes = new LinkedList<>();
            for (int i = 0; i < declaredAnnotations.length; i++) {
                Annotation declaredAnnotation = declaredAnnotations[i];
                if (
                        declaredAnnotation instanceof Documented ||
                                declaredAnnotation instanceof Target ||
                                declaredAnnotation instanceof Retention ||
                                declaredAnnotation instanceof Native ||
                                declaredAnnotation instanceof Inherited ||
                                declaredAnnotation instanceof Repeatable
                ) {
                    continue;
                }

                annotationDescribes.add(AnnotationDescribe.of(declaredAnnotation));
            }
            item.setAnnotationDescribes(annotationDescribes.toArray(new AnnotationDescribe[0]));
        }


        return item;
    }

    public List<AnnotationParameterDescribe> getAnnotationParameterDescribes() {
        if (null == annotationParameterDescribes) {
            synchronized (this) {
                if (null == annotationParameterDescribes) {
                    Map<String, Object> stringObjectMap = AnnotationUtils.asMap(annotation);
                    stringObjectMap.forEach((k, v) -> {
                        addAnnotationParameter(AnnotationParameterDescribe.of(k, v));
                    });
                }
            }
        }
        return annotationParameterDescribes;
    }

    /**
     * 获取值
     *
     * @param value 值
     * @return 结果
     */
    public String getValue(String value) {
        return getValue(value, String.class);
    }

    /**
     * 获取值
     *
     * @param value        值
     * @param defaultValue 默认值
     * @return 结果
     */
    public String getValue(String value, String defaultValue) {
        return Optional.ofNullable(getValue(value, String.class)).orElse(defaultValue);
    }

    /**
     * 获取所有数据
     *
     * @return 数据
     */
    public Map<String, Object> asMap() {
        if (ArrayUtils.isEmpty(annotationDescribes)) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> rs = new HashMap<>(annotationDescribes.length);
        for (AnnotationParameterDescribe describe : annotationParameterDescribes) {
            rs.put(describe.getName(), describe.getValue());
        }

        return rs;
    }

    /**
     * 获取值
     *
     * @param value        值
     * @param defaultValue 默认值
     * @param target       类型
     * @return 结果
     */
    public <T> T getValue(String value, T defaultValue, Class<T> target) {
        return Optional.ofNullable(getValue(value, target)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param value  值
     * @param target 类型
     * @return 结果
     */
    public <T> T getValue(String value, Class<T> target) {
        if (CollectionUtils.isEmpty(annotationParameterDescribes)) {
            return null;
        }

        for (AnnotationParameterDescribe annotationParameterDescribe : annotationParameterDescribes) {
            if (annotationParameterDescribe.isEquals(value)) {
                return Converter.convertIfNecessary(annotationParameterDescribe.getValue(), target);
            }
        }

        return null;
    }

    /**
     * 注入数据
     *
     * @param bean 对象
     */
    public void transfer(Object bean) {
        BeanUtils.copyProperties(bean, asMap());
    }

    /**
     * 是否存在注解
     *
     * @return 是否存在注解
     */
    public boolean hasAnnotation() {
        return null != annotation;
    }
}
