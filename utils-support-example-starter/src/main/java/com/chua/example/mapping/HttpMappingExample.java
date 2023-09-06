package com.chua.example.mapping;

import com.chua.common.support.mapping.Mapping;

import java.util.List;

/**
 * @author CH
 */
public class HttpMappingExample {
    public static void main(String[] args) {
        Idiom idiom = Mapping.of(Idiom.class).get();
        List<IdiomQuery> query = idiom.query(1, 10, "测");
    }
}
