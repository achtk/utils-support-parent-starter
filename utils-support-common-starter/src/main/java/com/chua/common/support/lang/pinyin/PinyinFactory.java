package com.chua.common.support.lang.pinyin;


import com.chua.common.support.utils.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 拼音
 *
 * @author CH
 */
public interface PinyinFactory {
    /**
     * 转拼音
     *
     * @param word 单词
     * @return 拼音
     */
    List<Pinyin> transfer(String word);

    /**
     * 拼音
     * @param word 单词
     * @return 拼音
     */
    default String transferSplit(String word) {
        return transfer(word).stream().map(it -> {
            List<PinyinItem> items = it.getItems();
            return CollectionUtils.isEmpty(items) ? "" : items.get(0).getName();
        }).collect(Collectors.joining(" "));
    }
    /**
     * 第一个拼音
     *
     * @param word 单词
     * @return 拼音
     */
    default Pinyin first(String word) {
        List<Pinyin> transfer = transfer(word);
        return transfer.isEmpty() ? null : transfer.get(0);
    }
}
