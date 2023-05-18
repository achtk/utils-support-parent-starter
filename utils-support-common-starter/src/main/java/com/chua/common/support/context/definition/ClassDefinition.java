package com.chua.common.support.context.definition;

import com.chua.common.support.context.enums.Scope;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.context.resolver.*;
import com.chua.common.support.context.resolver.factory.AutoInjectHandler;
import com.chua.common.support.context.value.AutoValueHandler;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.reflection.craft.MethodCraftTable;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.marker.Marker;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.AnnotationUtils;
import com.chua.common.support.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING_ARRAY;

/**
 * 类型定义
 * @author CH
 */
public class ClassDefinition<T> extends AbstractDelegateDefinition<T>{



    public ClassDefinition(Class<T> type, String... name) {
        super(type, name);

    }


}
