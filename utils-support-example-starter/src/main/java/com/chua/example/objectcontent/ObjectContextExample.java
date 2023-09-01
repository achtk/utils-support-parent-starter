package com.chua.example.objectcontent;

import com.chua.common.support.json.JsonObject;
import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;

/**
 * @author CH
 */
public class ObjectContextExample {

    public static void main(String[] args) {
//        ConfigureObjectContext objectContext = new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().build());
//        StandardConfigureEnvironment environment = objectContext.getEnvironment();

        TypeDefinition typeDefinition = new ClassTypeDefinition(JsonObject.class);
        System.out.println();

    }
}
