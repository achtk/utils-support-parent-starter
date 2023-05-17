package com.chua.common.support.lang.pinyin;

import java.util.List;

/**
 * 拼音
 *
 * @author CH
 */
public interface PinyinConverter {
    /**
     * 转拼音
     *
     * @param word 单词
     * @return 拼音
     */
    List<Pinyin> transfer(String word);

}
