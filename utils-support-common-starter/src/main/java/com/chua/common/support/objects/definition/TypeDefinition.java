package com.chua.common.support.objects.definition;

import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.objects.definition.element.FieldDefinition;
import com.chua.common.support.objects.definition.element.MethodDefinition;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定义
 *
 * @author CH
 */
public interface TypeDefinition {
    /**
     * 获取类型
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getType();

    /**
     * 獲取對象
     *
     * @return 对象
     */
    Object getObject();


    /**
     * 是否单例
     * @return boolean
     */
    boolean isSingle();

    /**
     * 是否代理
     * @return boolean
     */
    boolean isProxy();

    /**
     * 优先级
     *
     * @return boolean
     */
    int order();

    /**
     * 顺序
     * 优先级
     *
     * @param order 顺序
     * @return boolean
     */
    int order(int order);

    /**
     * 目标类是否是当前类的子类
     *
     * @param target 目标类
     * @return 目标类是否是当前类的子类
     */
    boolean isAssignableFrom(Class<?> target);

    /**
     * 目标类是否是当前类的子类
     *
     * @param target 目标类
     * @return 目标类是否是当前类的子类
     */
    boolean fromAssignableFrom(Class<?> target);

    /**
     * 类加载器
     *
     * @return 类加载器
     */
    ClassLoader getClassLoader();


    /**
     * 超类型和接口
     *
     * @return {@link Set}<{@link String}>
     */
    Set<String> superTypeAndInterface();

    /**
     * 名称
     *
     * @return {@link String}
     */
    String[] getName();

    /**
     * 实例化
     *
     * @param typeDefinitionSourceFactory 定义
     * @param <T>                         类型
     * @return 结果
     */
    <T> T newInstance(TypeDefinitionSourceFactory typeDefinitionSourceFactory);

    /**
     * 依赖
     *
     * @return {@link List}<{@link URL}>
     */
    List<URL> getDepends();

    /**
     * 添加bean名称
     *
     * @param value 值
     */
    void addBeanName(String[] value);

    /**
     * 添加bean名称
     *
     * @param value 值
     */
    default void addBeanName(String value) {
        addBeanName(new String[]{value});
    }


    /**
     * 获取方法释义
     *
     * @return {@link Map}<{@link String}, {@link List}<{@link MethodDefinition}>>
     */
    Map<String, List<MethodDefinition>> getMethodDefinition();


    /**
     * 获取领域释义
     *
     * @return {@link List}<{@link FieldDefinition}>
     */
    List<FieldDefinition> getFieldDefinition();

    /**
     * 存在注解
     *
     * @param annotationType 注解类型
     * @return boolean
     */
    boolean hasAnnotation(Class<? extends Annotation> annotationType);

    /**
     * 获取注解释义
     *
     * @return {@link List}<{@link AnnotationDefinition}>
     */
    List<AnnotationDefinition> getAnnotationDefinition();
}
