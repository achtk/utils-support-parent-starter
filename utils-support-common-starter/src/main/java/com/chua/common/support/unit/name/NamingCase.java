package com.chua.common.support.unit.name;

import static com.chua.common.support.constant.CharConstant.DASHED;
import static com.chua.common.support.constant.CharConstant.UNDERLINE;
import static com.chua.common.support.constant.NumberConstant.SIX;

/**
 * 命名规则封装，主要是针对驼峰风格命名、连接符命名等的封装
 *
 * @author CH
 */
public class NamingCase {

    /**
     * 将驼峰式命名的字符串转换为下划线方式，又称SnakeCase、underScoreCase。<br>
     * 如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间以下划线隔开</li>
     *     <li>每个单字的首字母亦用小写字母</li>
     * </ul>
     * 例如：
     *
     * <pre>
     * HelloWorld=》hello_world
     * Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toCamelUnderscore(CharSequence str) {
        return toSymbolCase(str, UNDERLINE);
    }
    /**
     * 将驼峰式命名的字符串转换为下划线方式，又称SnakeCase、underScoreCase。<br>
     * 如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间以下划线隔开</li>
     *     <li>每个单字的首字母亦用小写字母</li>
     * </ul>
     * 例如：
     *
     * <pre>
     * HelloWorld=》hello_world
     * Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, UNDERLINE);
    }

    /**
     * 将驼峰式命名的字符串转换为短横连接方式。<br>
     * 如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间横线线隔开</li>
     *     <li>每个单字的首字母亦用小写字母</li>
     * </ul>
     * 例如：
     *
     * <pre>
     * HelloWorld=》hello-world
     * Hello_World=》hello-world
     * HelloWorld_test=》hello-world-test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toKebabCase(CharSequence str) {
        return toSymbolCase(str, DASHED);
    }
    /**
     * 驼峰转减号
     * <pre>
     *     toCamelHyphen("userName") = user-name
     * </pre>
     *
     * @param str 原始数据
     * @return 下划线数据
     */
    public static String toCamelHyphen(CharSequence str) {
        return toSymbolCase(str, DASHED);
    }

    /**
     * 将驼峰式命名的字符串转换为使用符号连接方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。
     *
     * @param str    转换前的驼峰式命名的字符串，也可以为符号连接形式
     * @param symbol 连接符
     * @return 转换后符号连接方式命名的字符串
     * @since 4.0.10
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;

                if (null != preChar) {
                    if (symbol == preChar) {
                        // 前一个为分隔符
                        if (null == nextChar || Character.isLowerCase(nextChar)) {
                            //普通首字母大写，如_Abb -> _abb
                            c = Character.toLowerCase(c);
                        }
                        //后一个为大写，按照专有名词对待，如_AB -> _AB
                    } else if (Character.isLowerCase(preChar)) {
                        // 前一个为小写
                        sb.append(symbol);
                        if (null == nextChar || Character.isLowerCase(nextChar) || isNumber(nextChar)) {
                            //普通首字母大写，如aBcc -> a_bcc
                            c = Character.toLowerCase(c);
                        }
                        // 后一个为大写，按照专有名词对待，如aBC -> a_BC
                    } else {
                        //前一个为大写
                        if (null != nextChar && Character.isLowerCase(nextChar)) {
                            // 普通首字母大写，如ABcc -> A_bcc
                            sb.append(symbol);
                            c = Character.toLowerCase(c);
                        }
                        // 后一个为大写，按照专有名词对待，如ABC -> ABC
                    }
                } else {
                    // 首字母，需要根据后一个判断是否转为小写
                    if (null == nextChar || Character.isLowerCase(nextChar)) {
                        // 普通首字母大写，如Abc -> abc
                        c = Character.toLowerCase(c);
                    }
                    // 后一个为大写，按照专有名词对待，如ABC -> ABC
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 将下划线方式命名的字符串转换为帕斯卡式。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间不以空格或任何连接符断开</li>
     *     <li>第一个单字首字母采用大写字母</li>
     *     <li>后续单字的首字母亦用大写字母</li>
     * </ul>
     * 如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 例如：hello_world=》HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toPascalCase(CharSequence name) {
        return toFirstUpperCase(toCamelCase(name));
    }

    /**
     * 将下划线方式命名的字符串转换为帕斯卡式。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间不以空格或任何连接符断开</li>
     *     <li>第一个单字首字母采用大写字母</li>
     *     <li>后续单字的首字母亦用大写字母</li>
     * </ul>
     * 如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 例如：hello_world=》HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toHyphenUpperCamel(CharSequence name) {
        return toFirstUpperCase(toCamelCase(name));
    }


    /**
     * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间不以空格或任何连接符断开</li>
     *     <li>第一个单字首字母采用小写字母</li>
     *     <li>后续单字的首字母亦用大写字母</li>
     * </ul>
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(CharSequence name) {
        return toCamelCase(name, UNDERLINE);
    }


    /**
     * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 规则为：
     * <ul>
     *     <li>单字之间不以空格或任何连接符断开</li>
     *     <li>第一个单字首字母采用小写字母</li>
     *     <li>后续单字的首字母亦用大写字母</li>
     * </ul>
     * 例如：hello_world=》helloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toLowerCamelHyphen(CharSequence name) {
        return toCamelCase(name, UNDERLINE);
    }



    /**
     * 减号转下划线
     * <pre>
     *     toHyphenUpperUnderscore("user-name") = user_name
     * </pre>
     *
     * @param source 原始数据
     * @return 下划线数据
     */
    public static String toHyphenUpperUnderscore(String source) {
        return toSymbolCase(toCamelCase(source, DASHED), UNDERLINE);
    }

    /**
     * 将连接符方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。
     *
     * @param name   转换前的自定义方式命名的字符串
     * @param symbol 原字符串中的连接符连接符
     * @return 转换后的驼峰式命名的字符串
     * @since 5.7.17
     */
    public static String toCamelCase(CharSequence name, char symbol) {
        if (null == name) {
            return null;
        }

        final String name2 = name.toString();
        if (name2.indexOf(symbol) > -1) {
            final int length = name2.length();
            final StringBuilder sb = new StringBuilder(length);
            boolean upperCase = false;
            for (int i = 0; i < length; i++) {
                char c = name2.charAt(i);

                if (c == symbol) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    /**
     * 首字母小写
     * <pre>
     *     toFirstLowerCase("User-name") = user-name
     * </pre>
     *
     * @param source 原始数据
     * @return 下划线数据
     */
    public static String toFirstLowerCase(String source) {
        int strLen;
        if (source == null || (strLen = source.length()) == 0) {
            return source;
        }

        char firstChar = source.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return source;
        }

        return new StringBuilder(strLen)
                .append(Character.toLowerCase(firstChar))
                .append(source.substring(1))
                .toString();
    }

    /**
     * 首字母大写
     * <pre>
     *     toFirstUpperCase("user-name") = User-name
     * </pre>
     *
     * @param source 原始数据
     * @return 下划线数据
     */
    public static String toFirstUpperCase(String source) {
        int strLen;
        if (source == null || (strLen = source.length()) == 0) {
            return source;
        }

        char firstChar = source.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            return source;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(source.substring(1))
                .toString();
    }


    /**
     * Unicode 转  字符串
     *
     * @param source Unicode
     * @return 字符串
     */
    public static String toRevertUnicode(String source) {
        source = (source == null ? "" : source);
        //如果不是unicode码则原样返回
        String u = "\\u";
        if (!source.contains(u)) {
            return source;
        }

        StringBuilder sb = new StringBuilder(1000);

        for (int i = 0; i < source.length() - SIX; ) {
            String strTemp = source.substring(i, i + SIX);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a':
                        t = 10;
                        break;
                    case 'b':
                        t = 11;
                        break;
                    case 'c':
                        t = 12;
                        break;
                    case 'd':
                        t = 13;
                        break;
                    case 'e':
                        t = 14;
                        break;
                    case 'f':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }
                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }

    /**
     * 字符串转 Unicode
     *
     * @param value 值
     * @return Unicode
     */
    public static String toUnicode(String value) {
        value = (value == null ? "" : value);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < value.length(); i++) {
            c = value.charAt(i);
            sb.append("\\u");
            //取出高8位
            j = (c >>> 8);
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            //取出低8位
            j = (c & 0xFF);
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);

        }
        return (new String(sb));
    }

    /**
     * ascii转字符串
     *
     * @param source ascii
     * @return 字符串
     */
    public static String toRevertAscii(String source) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = source.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    /**
     * 字符串转ascii
     *
     * @param source 字符串
     * @return ascii
     */
    public static String toAscii(String source) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = source.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * <p>
     * 检查是否为数字字符，数字字符指0~9
     * </p>
     *
     * <pre>
     *   CharUtil.isNumber('a')  = false
     *   CharUtil.isNumber('A')  = false
     *   CharUtil.isNumber('3')  = true
     *   CharUtil.isNumber('-')  = false
     *   CharUtil.isNumber('\n') = false
     *   CharUtil.isNumber('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return true表示为数字字符，数字字符指0~9
     */
    private static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 减号转驼峰(首字母小写)
     * <pre>
     *     toHyphenLowerCamel("user-name") = userName
     * </pre>
     *
     * @param source 原始数据
     * @return 下划线数据
     */
    public static String toHyphenLowerCamel(String source) {
        int len = source.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = source.charAt(i);
            if (c == UNDERLINE) {
                ++i;
                sb.append(Character.toUpperCase(source.charAt(i)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
