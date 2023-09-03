package com.chua.example.objectcontent;

import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.StandardConfigureObjectContext;

import java.io.File;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
        ConfigureObjectContext objectContext = new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().build());
        objectContext.register(new File("Z://works/spring-core-5.3.23.jar"));
        System.out.println();
    }
}
