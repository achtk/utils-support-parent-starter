package com.chua.common.support.objects.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.objects.definition.element.FieldDefinition;
import com.chua.common.support.objects.definition.element.MethodDefinition;
import com.chua.common.support.objects.definition.element.SuperTypeDefinition;
import com.chua.common.support.objects.definition.resolver.*;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 定义
 * @author CH
 */
public class ClassTypeDefinition implements TypeDefinition, InitializingAware {


    private final Class<?> type;
    protected ObjectContext context;
    private boolean isSingle;
    private boolean isProxy;
    private int order;
    private String name;

    private Map<String, FieldDefinition> fieldDefinitions;

    private Map<String, List<MethodDefinition>> methodDefinitions;

    private Map<String, AnnotationDefinition> annotationDefinitions;

    private Map<String, SuperTypeDefinition> superTypeDefinitions;

    private Set<String> interfaces = new LinkedHashSet<>();
    private Set<String> superType = new LinkedHashSet<>();


    public ClassTypeDefinition(Class<?> type) {
        this.type = type;
        this.afterPropertiesSet();
    }

    public ClassTypeDefinition(Class<?> type, ObjectContext context) {
        this.type = type;
        this.context = context;
        this.afterPropertiesSet();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public boolean isProxy() {
        return isProxy;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public boolean isAssignableFrom(Class<?> target) {
        return type.isAssignableFrom(target);
    }

    @Override
    public ClassLoader getClassLoader() {
        return type.getClassLoader();
    }

    @Override
    public Set<String> superTypeAndInterface() {
        Set<String> rs = new LinkedHashSet<>();
        rs.addAll(interfaces);
        rs.addAll(superType);
        return rs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void afterPropertiesSet() {
        this.isSingle = ServiceProvider.of(SingleResolver.class).getSpiService().isSingle();
        this.isProxy = ServiceProvider.of(ProxyResolver.class).getSpiService().isProxy();
        this.order = ServiceProvider.of(OrderResolver.class).getSpiService().order();
        this.name = ServiceProvider.of(NameResolver.class).getSpiService().name();
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(type);
        this.fieldDefinitions = ServiceProvider.of(FieldResolver.class).getSpiService().get(type);
        this.methodDefinitions = ServiceProvider.of(MethodResolver.class).getSpiService().get(type);
        this.superTypeDefinitions = ServiceProvider.of(SuperTypeResolver.class).getSpiService().get(type);
        this.interfaces.addAll(ClassUtils.getAllInterfaces(type).stream().map(Class::getTypeName).collect(Collectors.toSet()));
        this.superType.addAll(ClassUtils.getSuperType(type).stream().map(Class::getTypeName).collect(Collectors.toSet()));
    }
}
