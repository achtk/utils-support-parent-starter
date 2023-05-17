package com.chua.example.describe;

import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.describe.TypeAttribute;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;

/**
 * @author CH
 */
public class DescribeExample {

    public static void main(String[] args) {
        TypeAttribute typeAttribute = TypeAttribute.create(MethodDescribe.class);
        Object name = typeAttribute.getFieldValue("name");
        MethodDescribeProvider provider = typeAttribute.getMethodDescribe("name");
        provider.execute();
        System.out.println();
    }
}
