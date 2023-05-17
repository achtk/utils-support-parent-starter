package com.chua.common.support.lang.pinyin.jpinyin;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.pinyin.Pinyin;
import com.chua.common.support.lang.pinyin.PinyinFactory;

import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.lang.pinyin.jpinyin.PinyinFormat.WITHOUT_TONE;

/**
 * jpinyin
 *
 * @author CH
 * @since 2022-05-13
 */
@Spi({"tiny", "pinyin"})
public class JpPinyinFactory implements PinyinFactory {
    @Override
    public List<Pinyin> transfer(String word) {
        List<Pinyin> rs = new LinkedList<>();
        String[] split = word.split(",");
        for (String s : split) {
            String[] fullMulti = PinyinHelper.getFullMulti(s, WITHOUT_TONE);
            rs.add(new Pinyin(fullMulti, s));
        }
        return rs;
    }
}
