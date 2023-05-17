package com.chua.pinyin.support.pinyin;

import com.chua.common.support.lang.pinyin.Pinyin;
import com.chua.common.support.lang.pinyin.PinyinConverter;
import com.chua.common.support.utils.StringUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.LinkedList;
import java.util.List;

/**
 * pinuyin4j
 *
 * @author CH
 * @since 2021-12-30
 */
public class Pinyin4JPinyinConverter implements PinyinConverter {

    private final HanyuPinyinOutputFormat formatMarker = new HanyuPinyinOutputFormat();

    {
        /**
         * 输出大小写设置
         *
         * LOWERCASE:输出小写
         * UPPERCASE:输出大写
         */
        formatMarker.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        /**
         * 输出音标设置
         *
         * WITH_TONE_MARK:直接用音标符（必须设置WITH_U_UNICODE，否则会抛出异常）
         * WITH_TONE_NUMBER：1-4数字表示音标
         * WITHOUT_TONE：没有音标
         */
        formatMarker.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        /**
         * 特殊音标ü设置
         *
         * WITH_V：用v表示ü
         * WITH_U_AND_COLON：用"u:"表示ü
         * WITH_U_UNICODE：直接用ü
         */
        formatMarker.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
    }

    @Override
    public List<Pinyin> transfer(String word) {
        if (StringUtils.isNullOrEmpty(word)) {
            return null;
        }

        char[] hanYuArr = word.trim().toCharArray();
        List<Pinyin> pinYin = new LinkedList<>();

        try {
            for (char c : hanYuArr) {
                //匹配是否是汉字
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    //如果是多音字，返回多个拼音，这里只取第一个
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, formatMarker);
                    pinYin.add(new Pinyin(pys, String.valueOf(c)));
                } else {
                    pinYin.add(new Pinyin(String.valueOf(c)));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return pinYin;
    }
}
