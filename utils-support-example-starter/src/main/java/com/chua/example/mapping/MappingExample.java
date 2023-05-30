package com.chua.example.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.mapping.MappingProxy;
import com.chua.example.mapping.guangdian.CulturalAuditorium;
import com.chua.example.mapping.guangdian.PlaceAuditorium;

import java.util.List;

/**
 * @author CH
 */
public class MappingExample {

    public static void main(String[] args) {
        testHttpMapping();
    }


    private static void testHttpMapping() {
//        testGuangdian();
//        testMp();
        testMoe();
//        testIdiom();
//       testHoliday();
    }

    private static void testGuangdian() {
        CulturalAuditorium culturalAuditorium = MappingProxy.create(CulturalAuditorium.class);
        List<PlaceAuditorium> placeAuditoriums = culturalAuditorium.listPlace(0, 100);
        System.out.println(placeAuditoriums);
        placeAuditoriums = culturalAuditorium.listPlace(0, 100);
        System.out.println(placeAuditoriums);
    }

    private static void testMp() {
        MpTarget mpTarget = MappingProxy.create(MpTarget.class);


        System.out.println();
    }

    private static void testIdiom() {
        Idiom idiom = MappingProxy.create(Idiom.class);
        List<IdiomQuery> query = idiom.query(1, 1000, "两");
        System.out.println(query);
    }

    private static void testMoe() {
        MoeIp moeIp = MappingProxy.create(MoeIp.class);

        JSONObject user = moeIp.analysis("221.12.111.18");
        user = moeIp.analysis("221.12.111.18");
        System.out.println(user);
    }

}
