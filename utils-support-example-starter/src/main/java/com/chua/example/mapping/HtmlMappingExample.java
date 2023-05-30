package com.chua.example.mapping;

import com.chua.common.support.mapping.MappingProxy;

/**
 * @author CH
 */
public class HtmlMappingExample {

    public static void main(String[] args) {
        Gitee gitee = MappingProxy.create(Gitee.class);
        System.out.println(gitee.test());
        System.out.println();
    }
}
