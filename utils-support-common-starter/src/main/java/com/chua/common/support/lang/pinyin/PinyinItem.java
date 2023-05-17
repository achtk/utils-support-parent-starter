package com.chua.common.support.lang.pinyin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 拼音
 *
 * @author CH
 * @since 2021-12-30
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PinyinItem {
    /**
     * 拼音
     */
    private String name;
    /**
     * 原始单词
     */
    @NonNull
    private String word;
    /**
     * 首字母
     */
    private String first;
    /**
     * 音标
     */
    private String mark;

}
