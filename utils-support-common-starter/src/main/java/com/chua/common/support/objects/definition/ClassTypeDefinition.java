package com.chua.common.support.objects.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.proxy.VoidMethodIntercept;
import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.definition.element.*;
import com.chua.common.support.objects.definition.resolver.*;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 定义
 *
 * @author CH
 */
public class ClassTypeDefinition implements TypeDefinition, InitializingAware {


    private final Class<?> type;
    protected ObjectContext context;
    private boolean isSingle;
    private boolean isProxy;
    private int order;
    private List<String> name;

    private Object bean;

    private boolean isLoaded;

    private Map<String, FieldDescribe> fieldDefinitions;

    private Map<String, List<MethodDescribe>> methodDefinitions;

    private Map<String, AnnotationDescribe> annotationDefinitions;

    private Map<String, SuperTypeDescribe> superTypeDefinitions;

    private final Set<String> interfaces = new LinkedHashSet<>();
    private final Set<String> superType = new LinkedHashSet<>();


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
    public int order(int order) {
        this.order = order;
        return order;
    }

    @Override
    public boolean isAssignableFrom(Class<?> target) {
        return type.isAssignableFrom(target);
    }

    @Override
    public boolean fromAssignableFrom(Class<?> target) {
        String typeName = target.getTypeName();
        return interfaces.contains(typeName) || superType.contains(typeName) || typeName.equals(type.getTypeName());
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
    public String[] getName() {
        return null != name ? name.toArray(EMPTY_ARRAY) : EMPTY_ARRAY;
    }

    @Override
    @SuppressWarnings("ALL")
    public <T> T newInstance(TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        if (isSingle) {
            return newBean(typeDefinitionSourceFactory);
        }
        Object newBeanObject = newBeanObject(typeDefinitionSourceFactory);
        reset();
        return (T) newBeanObject;
    }

    @Override
    public List<URL> getDepends() {
        return Collections.emptyList();
    }

    @Override
    public void addBeanName(String[] name) {
        this.name.add(Arrays.toString(name));
    }

    @Override
    public Map<String, List<MethodDescribe>> getMethodDefinition() {
        if (null == methodDefinitions) {
            this.methodDefinitions = ServiceProvider.of(MethodResolver.class).getSpiService().get(type);
        }
        return methodDefinitions;
    }

    @Override
    public List<FieldDescribe> getFieldDefinition() {
        if (null == fieldDefinitions) {
            this.fieldDefinitions = ServiceProvider.of(FieldResolver.class).getSpiService().get(type);
        }
        return new ArrayList<>(fieldDefinitions.values());
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return annotationDefinitions.containsKey(annotationType.getTypeName());
    }

    @Override
    public List<AnnotationDescribe> getAnnotationDefinition() {
        return new ArrayList<>(annotationDefinitions.values());
    }

    private void reset() {
        this.isLoaded = false;
    }

    @SuppressWarnings("ALL")
    private <T> T newBean(TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        if (!isLoaded) {
            return (T) (bean = newBeanObject(typeDefinitionSourceFactory));
        }

        return (T) bean;
    }

    @SuppressWarnings("ALL")
    private <T> T newBeanObject(TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        isLoaded = true;
        if (type.isInterface()) {
            return isProxy ? (T) ProxyUtils.newProxy(type, getClassLoader(), new VoidMethodIntercept<>()) : null;
        }

        return newBeanObject(type, typeDefinitionSourceFactory);

    }

    private <T> T newBeanObject(Class<?> type, TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        return new ConstructorDescribe(this, type, typeDefinitionSourceFactory).newInstance();
    }


    @Override
    public void afterPropertiesSet() {
        if (ClassUtils.isVoid(type)) {
            return;
        }
        this.isSingle = ServiceProvider.of(SingleResolver.class).getSpiService().isSingle();
        this.isProxy = ServiceProvider.of(ProxyResolver.class).getSpiService().isProxy();
        this.order = ServiceProvider.of(OrderResolver.class).getSpiService().order();
        this.name = new ArrayList<>();
        this.name.add(ServiceProvider.of(NameResolver.class).getSpiService().name(type));
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(type);
        this.superTypeDefinitions = ServiceProvider.of(SuperTypeResolver.class).getSpiService().get(type);
        this.interfaces.addAll(ClassUtils.getAllInterfaces(type).stream().map(Class::getTypeName).collect(Collectors.toSet()));
        this.superType.addAll(ClassUtils.getSuperType(type).stream().map(Class::getTypeName).collect(Collectors.toSet()));
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getClassLoader().hashCode();
    }
}
