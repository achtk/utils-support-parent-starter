package com.chua.common.support.value;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数字
 *
 * @author CH
 */
public class NumberValue implements Value<Number> {

    public static final List<String> HIGH_LEVEL;
    public static final List<String> NUMBER;
    public static final List<String> BIG_NUMBER;
    public static final List<String> LEVEL;

    static {
        HIGH_LEVEL = new ArrayList<>();
        HIGH_LEVEL.add("");
        HIGH_LEVEL.add("万");
        HIGH_LEVEL.add("亿");
        NUMBER = new ArrayList<>();
        NUMBER.add("零");
        NUMBER.add("一");
        NUMBER.add("二");
        NUMBER.add("三");
        NUMBER.add("四");
        NUMBER.add("五");
        NUMBER.add("六");
        NUMBER.add("七");
        NUMBER.add("八");
        NUMBER.add("九");
        BIG_NUMBER = new ArrayList<>();
        BIG_NUMBER.add("零");
        BIG_NUMBER.add("壹");
        BIG_NUMBER.add("贰");
        BIG_NUMBER.add("叁");
        BIG_NUMBER.add("肆");
        BIG_NUMBER.add("伍");
        BIG_NUMBER.add("陆");
        BIG_NUMBER.add("柒");
        BIG_NUMBER.add("捌");
        BIG_NUMBER.add("玖");
        LEVEL = new ArrayList<>();
        LEVEL.add("");
        LEVEL.add("十");
        LEVEL.add("百");
        LEVEL.add("千");
    }

    private final String source;

    public NumberValue(String source) {
        this.source = source;
    }

    @Override
    public boolean isNull() {
        return StringUtils.isEmpty(source) && null == Converter.convertIfNecessary(source, Number.class);
    }

    @Override
    public Number getValue() {
        Number number = Converter.convertIfNecessary(source, Number.class);
        if (null != number) {
            return number;
        }
        return null;
    }

}
