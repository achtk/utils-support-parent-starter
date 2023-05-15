package com.chua.spi;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.spi.autowire.ServiceAutowire;

import java.util.Map;

/**
 * @author CH
 */
public class Example {

    public static void main(String[] args) {
        //查找 com.chua.common.support.spi.autowire.ServiceAutowire  类的实现类
        //查找位置包括
        //1.META-INF/extensions
        //2.com.chua.common.support.spi.autowire.ServiceAutowire的同级或者子级包
        //3.原生的ServiceLoader
        ServiceProvider<ServiceAutowire> serviceProvider = ServiceProvider.of(ServiceAutowire.class);
        Map<String, Class<? extends ServiceAutowire>> stringClassMap = serviceProvider.listType();
        System.out.println();
    }
}
