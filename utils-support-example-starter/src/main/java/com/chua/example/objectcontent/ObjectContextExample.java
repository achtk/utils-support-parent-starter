package com.chua.example.objectcontent;

import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.provider.ObjectProvider;

import java.io.File;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
        installJar(args);
    }

    public static void sample(String[] args) {
        ConfigureObjectContext objectContext = ConfigureObjectContext.newDefault();
        Test2Demo test2Demo = objectContext.getBean(Test2Demo.class).get();
        test2Demo = objectContext.getBean(Test2Demo.class).get();
        System.out.println();
    }

    public static void installJar(String[] args) {
        String name = "D:\\env\\repository\\com\\chua\\utils-support-common-starter\\3.1.0\\utils-support-common-starter-3.1.0.jar";
        ConfigureObjectContext objectContext = ConfigureObjectContext.newDefault();
        Robin test2Demo = objectContext.getBean(Robin.class).get();
        objectContext.register(new File(name));
        test2Demo = objectContext.getBean(Robin.class).get();
        ObjectProvider<Robin> bean = objectContext.getBean(Robin.class);
        objectContext.unregister(name);
        test2Demo = objectContext.getBean(Robin.class).get();
        System.out.println();
    }
}
