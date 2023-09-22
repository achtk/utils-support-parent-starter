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
public interface ContextConstant {
    /************************** persistence ******************************************/
    Class<? extends Annotation> TABLE = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Table");
    Class<? extends Annotation> ID = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Id");
    Class<? extends Annotation> COLUMN = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.Column");
    Class<? extends Annotation> GENERATED_VALUE = (Class<? extends Annotation>) ClassUtils.forName("javax.persistence.GeneratedValue");
    Class<? extends Annotation> COMMENT = (Class<Annotation>) ClassUtils.forName("org.hibernate.annotations.Comment");

    /************************** mybatis ******************************************/
    Class<? extends Annotation> TABLE_ID = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableId");
    Class<? extends Annotation> TABLE_FIELD = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableField");
    Class<? extends Annotation> TABLE_LOGIC = (Class<? extends Annotation>) ClassUtils.forName("com.baomidou.mybatisplus.annotation.TableLogic");

    Class<? extends AutoService> CONFIGURATION_CLASS = AutoService.class;
    Class<? extends Annotation> RESOURCE_TYPE = (Class<? extends Annotation>) ClassUtils.forName("javax.annotation.Resource");
    Class<? extends Annotation> POST_CONSTRUCT = (Class<? extends Annotation>) ClassUtils.forName("javax.annotation.PostConstruct");
    /************************** JSR330 ******************************************/
    Class<? extends Annotation> INJECT = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject");
    Class<? extends Annotation> MAPPER = (Class<? extends Annotation>) ClassUtils.forName("org.apache.ibatis.annotations.Mapper");
    Class<? extends Annotation> NAMED = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named");
    Class<? extends Annotation> SINGLETON = (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Singleton");
    Class<? extends Annotation> CONFIGURATION = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.Configuration");
    Class<? extends Annotation> VALUE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Value");
    Class<? extends Annotation> COMPONENT = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Component");
    Class<? extends Annotation> REPOSITORY = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Repository");
    Class<? extends Annotation> QUALIFIER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Qualifier");
    Class<? extends Annotation> CONTROLLER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Controller");
    Class<? extends Annotation> SCOPE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.Scope");
    Class<? extends Annotation> ORDER = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.core.annotation.Order");
    Class<? extends Annotation> AUTOWIRE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.beans.factory.annotation.Autowired");
    Class<? extends Annotation> PROPERTY_SOURCE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.context.annotation.PropertySource");
    Class<?> INITIALIZING_BEAN = ClassUtils.forName("org.springframework.beans.factory.InitializingBean");
    Class<? extends Annotation> SERVICE = (Class<? extends Annotation>) ClassUtils.forName("org.springframework.stereotype.Service");
    Comparator<TypeDefinition> COMPARATOR = (o1, o2) -> BaseOrdering.natural().compare(o2.order(), o1.order());

    List<Class<? extends Annotation>> SCANN = CollectionUtils.<Class<? extends Annotation>>newArrayList(INJECT, COMPONENT, REPOSITORY, SERVICE, CONTROLLER);

}
