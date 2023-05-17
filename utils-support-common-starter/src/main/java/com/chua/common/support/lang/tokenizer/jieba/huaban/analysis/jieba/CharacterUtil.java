package com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba;

import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.LETTER_UPPERCASE_A;
import static com.chua.common.support.constant.CommonConstant.LETTER_UPPERCASE_Z;


/**
 * @author Administrator
 */
public class CharacterUtil {
    private static final char[] CONNECTORS = new char[] { '+', '#', '&', '.', '_', '-' };
    public static final Pattern RE_SKIP = Pattern.compile("(\\d+\\.\\d+|[a-zA-Z0-9]+)");
    private static final char M_12288 = 12288;
    private static final char M_65280 = 65280;
    private static final char M_65375 = 65375;

    public static boolean isChineseLetter(char ch) {
        return ch >= 0x4E00 && ch <= 0x9FA5;
    }


    public static boolean isEnglishLetter(char ch) {
        return (ch >= 0x0041 && ch <= 0x005A) || (ch >= 0x0061 && ch <= 0x007A);
    }


    public static boolean isDigit(char ch) {
        return ch >= 0x0030 && ch <= 0x0039;
    }


    public static boolean isConnector(char ch) {
        for (char connector : CONNECTORS) {
            if (ch == connector) {
                return true;
            }
        }
        return false;
    }


    public static boolean ccFind(char ch) {
        if (isChineseLetter(ch)) {
            return true;
        }
        if (isEnglishLetter(ch)) {
            return true;
        }
        if (isDigit(ch)) {
            return true;
        }
        if (isConnector(ch)) {
            return true;
        }
        return false;
    }


    /**
     * 全角 to 半角,大写 to 小写
     * 
     * @param input
     *            输入字符
     * @return 转换后的字符
     */
    public static char regularize(char input) {
        if (input == M_12288) {
            return 32;
        }
        else if (input > M_65280 && input < M_65375) {
            return (char) (input - 65248);
        }
        else if (input >= LETTER_UPPERCASE_A && input <= LETTER_UPPERCASE_Z) {
            return (input += 32);
        }
        return input;
    }

}
