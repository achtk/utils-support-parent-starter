package com.chua.example.objectcontent;

import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.StandardConfigureObjectContext;
import com.chua.common.support.objects.environment.StandardConfigureEnvironment;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
        ConfigureObjectContext objectContext = new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().build());
        StandardConfigureEnvironment environment = objectContext.getEnvironment();

//        TypeDefinition typeDefinition = new ClassTypeDefinition(JsonObject.class);
        System.out.println();

    }
}
