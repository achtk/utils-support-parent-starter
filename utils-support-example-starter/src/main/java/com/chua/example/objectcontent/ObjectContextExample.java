package com.chua.example.objectcontent;

import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import com.chua.common.support.objects.provider.ObjectProvider;

import java.io.File;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
        ConfigureObjectContext objectContext =
                new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().outSideInAnnotation(true).build());
        objectContext.register(new File("D:\\env\\repository\\com\\chua\\utils-support-common-starter\\3.1.0\\utils-support-common-starter-3.1.0.jar"));
        ObjectProvider<Robin> bean = objectContext.getBean(Robin.class);
        Robin robin = objectContext.getBean("polling", Robin.class);
//        objectContext.unregister("Z://zookeeper-3.7.1.jar");
        System.out.println();
    }
}
