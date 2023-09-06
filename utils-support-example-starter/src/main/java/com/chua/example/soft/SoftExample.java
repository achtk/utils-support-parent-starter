package com.chua.example.soft;


import com.chua.common.support.soft.Soft;
import com.chua.common.support.soft.SoftInfo;
import com.chua.common.support.soft.SoftService;

import java.util.List;

/**
 * @author CH
 */
public class SoftExample {

    public static void main(String[] args) {
        Soft soft = Soft.builder().softPath("Z:/soft").build();
        List<SoftInfo> list = soft.list();
        soft.install("redis");
        SoftService softService = soft.createService("redis");
        softService.start();

        System.out.println();
    }
}
