package com.chua.common.support.lang.pinyin;

import com.chua.common.support.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 拼音
 *
 * @author CH
 * @since 2021-12-30
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Pinyin {
    /**
     * 原始单词
     */
    @NonNull
    private String word;
    /**
     * 拼音元
     */
    private List<PinyinItem> items;

    public Pinyin(String items, @NonNull String word) {
        this.word = word;
        this.items = new LinkedList<>();
        this.items.add(new PinyinItem(items, word, items.substring(0, 1), null));
    }

    public Pinyin(String[] items, @NonNull String word) {
        this.word = word;
        this.items = new LinkedList<>();
        String marks = null, name, fNames;
        for (String item : items) {
            name = item.substring(0, item.length() - 1);
            String substring = item.substring(item.length() - 1);
            if (NumberUtils.isNumber(substring)) {
                marks = item.substring(item.length() - 1);
            } else {
                name = item;
            }
            fNames = name.substring(0, 1);
            this.items.add(new PinyinItem(name, word, fNames, marks));
        }
    }

    /**
     * 获取首字母
     *
     * @return 首字母
     */
    public String getFirst() {
        if (null == items || items.isEmpty()) {
            return null;
        }

        return items.get(0).getFirst();
    }

    /**
     * 获取拼音
     *
     * @return 拼音
     */
    public String getPinyin() {
        if (null == items || items.isEmpty()) {
            return null;
        }

        return items.get(0).getName();
    }
}
