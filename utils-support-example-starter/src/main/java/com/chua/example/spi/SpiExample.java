package com.chua.example.spi;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.spi.autowire.ServiceAutowire;

import java.util.Map;

/**
 * @author CH
 */
public class SpiExample {

    public static void main(String[] args) {
        //查找 com.chua.common.support.protocol.spi.autowire.ServiceAutowire  类的实现类
        //查找位置包括
        //1.META-INF/extensions
        //2.com.chua.common.support.protocol.spi.autowire.ServiceAutowire的同级或者子级包
        //3.原生的ServiceLoader
        //4.spring-factories
        ServiceProvider<ServiceAutowire> serviceProvider = ServiceProvider.of(ServiceAutowire.class);
        Map<String, Class<ServiceAutowire>> stringClassMap = serviceProvider.listType();
        System.out.println();
    }
}
