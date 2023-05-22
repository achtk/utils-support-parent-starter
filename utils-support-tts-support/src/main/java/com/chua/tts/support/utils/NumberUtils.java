package com.chua.tts.support.utils;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    static final String[] NUMBER_CN = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    static final String[] NUMBER_LEVEL = {"千", "百", "十", "万", "千", "百", "十", "亿", "千", "百", "十", "万", "千", "百", "十", "个"};
    static final String ZERO = NUMBER_CN[0];
    static final Pattern TEN_RE = Pattern.compile("^一十");
    static final List<String> GRADE_LEVEL = ImmutableBuilder.<String>builder().add("万", "亿", "个").newUnmodifiableList();
    static final Pattern NUMBER_GROUP_RE = Pattern.compile("([0-9]+)");

    public static void main(String[] args) {
        System.out.println(sayDigit("51234565"));
        System.out.println(sayNumber("12345678901234561"));
        System.out.println(sayDecimal("3.14"));
        System.out.println(convertNumber("hello314.1592and2718281828"));

        // 五一二三四五六五
        // 12345678901234561 (小于等于16位时: 十二亿三千四百五十六万七千八百九十)
        // 三点一四
        // hello三百一十四.一千五百九十二and二七一八二八一八二八
    }

    public static String sayDigit(String num) {
        StringBuilder outs = new StringBuilder();
        String[] ss = num.split("");
        for (String s : ss) {
            outs.append(NUMBER_CN[Integer.valueOf(s)]);
        }
        return outs.toString();
    }

    public static String sayNumber(String nums) {
        String x = nums;
        if (x == "0") {
            return NUMBER_CN[0];
        } else if (x.length() > 16) {
            return nums;
        }
        int length = x.length();
        LinkedList<String> outs = new LinkedList();
        String[] ss = x.split("");
        for (int i = 0; i < ss.length; i++) {
            String a = NUMBER_CN[Integer.valueOf(ss[i])];
            String b = NUMBER_LEVEL[NUMBER_LEVEL.length - length + i];
            if (!a.equals(ZERO)) {
                outs.add(a);
                outs.add(b);
            } else {
                if (GRADE_LEVEL.contains(b)) {
                    if (!ZERO.equals(outs.getLast())) {
                        outs.add(b);
                    } else {
                        outs.removeLast();
                        outs.add(b);
                    }
                } else {
                    if (!ZERO.equals(outs.getLast())) {
                        outs.add(a);
                    }
                }
            }
        }
        outs.removeLast();
        String out = Joiner.on("").join(outs);
        // 进行匹配
        Matcher matcher = TEN_RE.matcher(out);
        out = matcher.replaceAll("十");
        return out;
    }

    public static String sayDecimal(String num) {
        String[] nums = num.split("\\.");
        String zCn = sayNumber(nums[0]);
        String x_cn = sayDigit(nums[1]);
        return zCn + '点' + x_cn;
    }

    public static String convertNumber(String text) {

        Matcher matcher = NUMBER_GROUP_RE.matcher(text);
        LinkedList<Integer> postion = new LinkedList();
        while (matcher.find()) {
            postion.add(matcher.start());
            postion.add(matcher.end());
        }
        if (postion.size() == 0) {
            return text;
        }
        List<String> parts = ImmutableBuilder.newArrayList();
        parts.add(text.substring(0, postion.getFirst()));
        int size = postion.size() - 1;
        for (int i = 0; i < size; i++) {
            parts.add(text.substring(postion.get(i), postion.get(i + 1)));
        }
        parts.add(text.substring(postion.getLast()));
        LinkedList<String> outs = new LinkedList();
        for (String elem : parts) {
            if (com.chua.common.support.utils.NumberUtils.isNumber(elem)) {
                if (elem.length() <= 9) {
                    outs.add(sayNumber(elem));
                } else {
                    outs.add(sayDigit(elem));
                }
            } else {
                outs.add(elem);
            }
        }
        return Joiner.on("").join(outs);
    }


}
