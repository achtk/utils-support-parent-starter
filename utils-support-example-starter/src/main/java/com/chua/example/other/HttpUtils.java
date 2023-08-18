package com.chua.example.other;

/**
 *  http请求工具类
 * @author : Created by Unicorn
 * @date : Created in 2019/1/26
 */
public class HttpUtils {

    public static void main(String[] args) {
        HttpResponse response = HttpClient.get()
                .url("https://api.seniverse.com/v3/weather/now.json")
                .body("key", "SDhtdCnlb7SVLl2GS")
                .body("location", "zhoushan")
                .body("language", "zh-Hans")
                .body("unit", "c").newInvoker().execute();
        String s = response.to(String.class);

        System.out.println();
    }
}
