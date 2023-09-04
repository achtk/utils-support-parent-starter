package com.chua.common.support.constant;


import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.scanner.annotations.AutoService;
import com.chua.common.support.range.order.BaseOrdering;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

/**
 * 常量
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ContextConstant {
    /************************** persistence ******************************************/
    public static final Class<? extends Annotation> TABLE = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Table");
    public static final Class<? extends Annotation> ID = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Id");
    public static final Class<? extends Annotation> COLUMN = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Column");
    public static final Class<? extends Annotation> GENERATED_VALUE = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.GeneratedValue");
    public static final Class<? extends Annotation> COMMENT = (Class<Annotation>) ClassUtils.forName("org.hibernate.annotations.Comment");

    /************************** mybatis ******************************************/
    public static final Class<? extends Annotation> TABLE_ID = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableId");
    public static final Class<? extends Annotation> TABLE_FIELD = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableField");
    public static final Class<? extends Annotation> TABLE_LOGIC = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableLogic");

    public static final Class<? extends AutoService> CONFIGURATION_CLASS = AutoService.class;
    public static final Class<? extends Annotation> RESOURCE_TYPE = (Class<? extends Annotation>) ClassUtils.forName("javax.annotation.Resource");
    public static final Class<? extends Annotation> POST_CONSTRUCT = (Class<? extends Annotation>) ClassUtils.forName("javax.annotation.PostConstruct");
    /************************** JSR330 ******************************************/
    public static final Class<? extends Annotation> INJECT = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject");
    public static final Class<? extends Annotation> MAPPER = (Class<? extends Annotation>) ClassUtils.forName("org.apache.ibatis.annotations.Mapper");
    public static final Class<? extends Annotation> NAMED = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named");
    public static final Class<? extends Annotation> SINGLETON = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Singleton");
    public static final Class<? extends Annotation> CONFIGURATION = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.Configuration");
    public static final Class<? extends Annotation> VALUE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Value");
    public static final Class<? extends Annotation> COMPONENT = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Component");
    public static final Class<? extends Annotation> REPOSITORY = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Repository");
    public static final Class<? extends Annotation> QUALIFIER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Qualifier");
    public static final Class<? extends Annotation> CONTROLLER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Controller");
    public static final Class<? extends Annotation> SCOPE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.Scope");
    public static final Class<? extends Annotation> ORDER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.core.annotation.Order");
    public static final Class<? extends Annotation> AUTOWIRE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Autowired");
    public static final Class<? extends Annotation> PROPERTY_SOURCE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.PropertySource");
    public static final Class<?> INITIALIZING_BEAN = ClassUtils.forName("org.springframework.beans.factory.InitializingBean");
    public static final Class<? extends Annotation> SERVICE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Service");
    public static final Comparator<TypeDefinition> COMPARATOR = (o1, o2) -> BaseOrdering.natural().compare(o2.order(), o1.order());

    public static final List<Class<? extends Annotation>> SCANN = CollectionUtils.<Class<? extends Annotation>>newArrayList(INJECT, COMPONENT, REPOSITORY, SERVICE, CONTROLLER);

}
