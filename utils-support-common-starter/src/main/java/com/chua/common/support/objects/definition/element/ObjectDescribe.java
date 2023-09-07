package com.chua.common.support.objects.definition.element;

import com.chua.common.support.objects.definition.resolver.MethodResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import java.util.List;

/**
 * 描述
 * @author CH
 */
public class ObjectDescribe extends TypeDescribe{

    private final Object data;

    /**
     * 对象描述
     *
     * @param data 数据
     */
    public ObjectDescribe(Object data) {
        super(ClassUtils.toType(data));
        this.data = data;
    }
    private void loadMethodDefinitions() {
        methodDefinitions = ServiceProvider.of(MethodResolver.class).getSpiService().get(type);
        for (List<MethodDescribe> describeList : methodDefinitions.values()) {
            describeList.register(data);
        }
    }


}
