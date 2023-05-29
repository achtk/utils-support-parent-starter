package com.chua.example.tokenizer;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.Md5Utils;
import com.chua.common.support.utils.UrlUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author CH
 */
public class TokenizerExample {

    public static void main(String[] args) {
        Map<String, Object> param = ImmutableBuilder.<String, Object>builderOfMap()
                .put("appid", "wx980065354062cb26")
                .put("mch_id", "1625485411")
                .put("device_info", "013467007045764")
                .put("nonce_str", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
                .put("body", "image形象店-深圳腾大- QQ公仔")
                .put("out_trade_no", "130390382306112559")
                .put("total_fee", 1)
                .put("spbill_create_ip", "8.8.8.8")
                .put("auth_code", "130390382306112559").build();

        Map<String, Object> newParam = MapUtils.sortKey(param);
        String join = Joiner.on("&").withKeyValueSeparator("=").join(newParam) + "&key=C633B948583A4D76A5745F466E3950CD";
        System.out.println(join);
        String sign = Md5Utils.getInstance().getMd5String(join).toUpperCase();
        System.out.println(sign);

//        Tokenizer tokenizer = Tokenizer.newDefault();
//        System.out.println(tokenizer.segments("测试单词"));
    }
}