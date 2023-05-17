package com.chua.example.pinyin;

import com.chua.common.support.lang.pinyin.PinyinFactory;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 中文转拼音例子
 *
 * @author CH
 * @since 2021-12-30
 */
public class PinyinExample {

    public static void main(String[] args) {
        String word = "单";
        //通过spi获取 PinyinFactory 的实现
        ServiceProvider<PinyinFactory> provider = ServiceProvider.of(PinyinFactory.class);
        //获取实现
        PinyinFactory pinyinFactory = provider.getExtension("tiny");
        //转化为拼音
        System.out.println(word + "全拼：" + pinyinFactory.transfer(word));
    }
}
