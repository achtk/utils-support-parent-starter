package com.chua.example.objectcontent;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.example.mapping.MoeIp;

import java.io.File;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
        sample(args);
    }

    public static void sample(String[] args) {
        ConfigureObjectContext objectContext = new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().packages(new String[] {
                "com.chua.example.mapping", "com.chua.example.objectcontent"
        }).build());
        Test2Demo test2Demo = objectContext.getBean(Test2Demo.class).get();
        test2Demo = objectContext.getBean(Test2Demo.class).get();
        MoeIp moeIp = objectContext.getBean(MoeIp.class.getTypeName(), MoeIp.class);
        JSONObject jsonObject = moeIp.analysis("127.0.0.1");
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
