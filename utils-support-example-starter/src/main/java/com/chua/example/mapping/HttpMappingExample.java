package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.Mapping;
import com.chua.common.support.mapping.MappingConfig;

import java.util.List;

/**
 * @author CH
 */
public class HttpMappingExample {

    public static void main(String[] args) {
        HikClient hikClient = Mapping.of(HikClient.class, MappingConfig.builder()
                .host("")
                .path("/artemis")
                .appKey("")
                .secretAccessKey("")
                .build()
        ).get();
        OrgListResult orgListResult = hikClient.orgList(1, 100);
        System.out.println();

    }


    public static void moeIp(String[] args) {
        MoeIp moeIp = Mapping.of(MoeIp.class).get();
        JSONObject analysis = moeIp.analysis("127.0.0.1");
        System.out.println();
    }

    public static void idiom(String[] args) {
        Idiom idiom = Mapping.of(Idiom.class).get();
        List<IdiomQuery> query = idiom.query(1, 10, "测");
    }
}
