package com.chua.example.tokenizer;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.Md5Utils;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author CH
 */
public class TokenizerExample {

    public static void main(String[] args) throws IOException {
        System.out.println(IoUtils.toString(new File("z://base64.txt")));
    }

    public static void main2(String[] args) {
        Tokenizer tokenizer = Tokenizer.newDefault();
        System.out.println(tokenizer.segments("测试单词"));
    }

    public static void main1(String[] args) {
        Map<String, Object> param = ImmutableBuilder.<String, Object>builderOfMap()
                .put("device_info", "013467007045764")
                .put("nonce_str", "")
                .put("body", "image形象店-深圳腾大- QQ公仔")
                .put("out_trade_no", "")
                .put("total_fee", 1)
                .put("spbill_create_ip", "8.8.8.8")
                .put("auth_code", "")
                .build();
        Map<String, Object> newParam = MapUtils.sortKey(param);
        String join = Joiner.on("&").withKeyValueSeparator("=").join(newParam) + "&key=";
        System.out.println(join);
        String sign = Md5Utils.getInstance().getMd5String(join).toUpperCase();
        System.out.println(sign);
        param.put("sign", sign);

        HttpClientInvoker invoker = HttpClient.post()
                .url("https://api.mch.weixin.qq.com/pay/micropay")
                .body(ImmutableBuilder.<String, Object>builderOfMap()
                        .put("xml", param).build())
                .header(HttpHeaders.CONTENT_TYPE, "text/xml")
                .newInvoker();
        HttpResponse httpResponse = invoker.execute();
        System.out.println(httpResponse.content(String.class));


//        Tokenizer tokenizer = Tokenizer.newDefault();
//        System.out.println(tokenizer.segments("测试单词"));
    }
}
