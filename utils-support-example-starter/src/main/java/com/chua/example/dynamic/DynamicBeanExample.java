package com.chua.example.dynamic;

import com.chua.common.support.reflection.dynamic.DynamicBean;
import com.chua.common.support.reflection.dynamic.DynamicScriptBean;
import com.chua.common.support.reflection.dynamic.DynamicStringBean;
import com.chua.common.support.reflection.dynamic.attribute.MethodAttribute;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * 动态对象例子
 *
 * @author CH
 * @since 2021-07-15
 */
@Slf4j
public class DynamicBeanExample {

    public static void main(String[] args) throws Exception {
        DynamicBean demoInfoDynamicBean = DynamicStringBean
                .newBuilder()
                .packages(UUID.class)
                .method(MethodAttribute.builder().name("getUuid").body("return UUID.randomUUID().toString();").build())
                .build();

        Object tDemoInfo1 = DynamicStringBean.newBuilder()
                .name("test")
                .field("name233", String.class).build().createBean(Object.class);

        TDemoInfo tDemoInfo = demoInfoDynamicBean.createBean(TDemoInfo.class);
        System.out.println(tDemoInfo.getUuid());

        DynamicBean dynamicBean = DynamicScriptBean.newBuilder()
                .name("classpath:TDemoInfoImpl.java")
                .build();

        TDemoInfo dynamicBeanBean = dynamicBean.createBean(TDemoInfo.class);
        System.out.println(dynamicBeanBean.getUuid());
        System.out.println(dynamicBeanBean.getUuid());
    }
}
