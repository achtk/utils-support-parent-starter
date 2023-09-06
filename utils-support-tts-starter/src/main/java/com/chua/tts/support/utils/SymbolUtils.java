package com.chua.tts.support.utils;

import com.chua.common.support.utils.ArrayUtils;

/**
 * #### symbol
 * 音素标记。
 * 中文音素，简单英文音素，简单中文音素。
 *
 * @author Administrator
 */
public class SymbolUtils {
    /**
     * # 填充符
     */
    static final String PAD = "_";
    /**
     * # 结束符
     */
    static final String EOS = "~";
    /**
     * # 连接符，连接读音单位
     */
    static final String CHAIN = "-";
    static final String OOV = "*";

    /**
     * 中文音素表
     * 声母：27
     */
    static final String[] SHENGMU = {
            "aa", "b", "c", "ch", "d", "ee", "f", "g", "h", "ii", "j", "k", "l", "m", "n", "oo", "p", "q", "r", "s", "sh",
            "t", "uu", "vv", "x", "z", "zh"
    };
    /**
     * 韵母：41
     */
    static final String[] YUNMU = {
            "a", "ai", "an", "ang", "ao", "e", "ei", "en", "eng", "er", "i", "ia", "ian", "iang", "iao", "ie", "in", "ing",
            "iong", "iu", "ix", "iy", "iz", "o", "ong", "ou", "u", "ua", "uai", "uan", "uang", "ueng", "ui", "un", "uo", "v",
            "van", "ve", "vn", "ng", "uong"
    };
    /**
     * 声调：5
     */
    static final String[] SHENGDIAO = {"1", "2", "3", "4", "5"};
    /**
     * 字母：26
     */
    static final String[] ALPHABET = "Aa Bb Cc Dd Ee Ff Gg Hh Ii Jj Kk Ll Mm Nn Oo Pp Qq Rr Ss Tt Uu Vv Ww Xx Yy Zz".split(" ");
    /**
     * 英文：26
     */
    static final String[] ENGLISH = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    /**
     * 标点：10
     */
    static final String[] BIAODIAN = "! ? . , ; : \" # ( )".split(" ");
    /**
     * 注：!=!！|?=?？|.=.。|,=,，、|;=;；|:=:：|"="“|#= \t|(=(（[［{｛【<《|)=)）]］}｝】>》
     * 其他：7
     */
    static final String[] OTHER = "w y 0 6 7 8 9".split(" ");
    /**
     * 大写字母：26
     */
    static final String[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    /**
     * 小写字母：26
     */
    static final String[] LOWER = "abcdefghijklmnopqrstuvwxyz".split("");
    /**
     * 标点符号：12
     */
    static final String[] PUNCTUATION = "! ' \" ( ) , - . : ; ?".split(" ");

    /**
     * 数字：10
     */
    static final String[] DIGIT = "0123456789".split("");
    /**
     * 字母和符号：64
     * 用于英文:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s
     */
    static final String[] CHARACTER_EN = ArrayUtils.addAll(UPPER, LOWER, PUNCTUATION);

    /**
     * 字母、数字和符号：74
     * 用于英文或中文:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s0123456789
     */
    static final String[] CHARACTER_CN = ArrayUtils.addAll(UPPER, LOWER, PUNCTUATION, DIGIT);

    /**
     * 中文音素：145
     * 支持中文环境、英文环境、中英混合环境，中文把文字转为清华大学标准的音素表示
     */
    static final String[] SYMBOL_CHINESE = ArrayUtils.addAll(new String[]{PAD, EOS, CHAIN}, SHENGMU, YUNMU, SHENGDIAO, ALPHABET, ENGLISH, BIAODIAN, OTHER);

    /**
     * 简单英文音素：66
     * 支持英文环境
     * ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s
     */
    static final String[] SYMBOL_ENGLISH_SIMPLE = ArrayUtils.addAll(new String[]{PAD, EOS}, UPPER, LOWER, PUNCTUATION);

    /**
     * 简单中文音素：76
     * 支持英文、中文环境，中文把文字转为拼音字符串
     * ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\'"(),-.:;?\s0123456789
     */
    static final String[] SYMBOL_CHINESE_SIMPLE = ArrayUtils.addAll(new String[]{PAD, EOS}, UPPER, LOWER, PUNCTUATION, DIGIT);

	
	/*static{
		//英文
		System.arraycopy(_upper, 0, _character_en, 0, _upper.length);
		System.arraycopy(_lower, 0, _character_en, _upper.length, _lower.length); 
	}*/

}
