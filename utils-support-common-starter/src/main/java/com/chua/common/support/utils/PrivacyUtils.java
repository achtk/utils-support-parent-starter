package com.chua.common.support.utils;

/**
 * @author CH
 */
public class PrivacyUtils {

    /**
     * 隐藏手机号中间四位
     */
    public static String hidePhone(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 隐藏邮箱
     */
    public static String hideEmail(String email) {
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    /**
     * 隐藏身份证
     */
    public static String hideIDCard(String idCard) {
        return idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    /**
     * 【中文姓名】只显示第一个汉字，其他隐藏为星号，比如：任**
     */
    public static String hideChineseName(String chineseName) {
        if (chineseName == null) {
            return null;
        }
        return desValue(chineseName, 1, 0, "*");
    }

    /**
     * 【身份证号】显示前4位, 后2位
     */
    public static String hideIdCard(String idCard) {
        return desValue(idCard, 4, 2, "*");
    }


    /**
     * 对字符串进行脱敏操作
     *
     * @param origin          原始字符串
     * @param prefixNoMaskLen 左侧需要保留几位明文字段
     * @param suffixNoMaskLen 右侧需要保留几位明文字段
     * @param maskStr         用于遮罩的字符串, 如'*'
     * @return 脱敏后结果
     */
    public static String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (origin == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = origin.length(); i < n; i++) {
            if (i < prefixNoMaskLen) {
                sb.append(origin.charAt(i));
                continue;
            }
            if (i > (n - suffixNoMaskLen - 1)) {
                sb.append(origin.charAt(i));
                continue;
            }
            sb.append(maskStr);
        }
        return sb.toString();
    }

    /**
     * 【车牌号】前两位后一位，比如：苏M****5
     *
     * @param carNumber 车牌号
     * @return 比如：苏M****5
     */
    public static String hideCarNumber(String carNumber) {
        if (StringUtils.isBlank(carNumber)) {
            return "";
        }
        return StringUtils.left(carNumber, 2).
                concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(carNumber, 1), StringUtils.length(carNumber), "*"), "**"));

    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     *
     * @param password password
     * @return 比如：******
     */
    public static String hidePassword(String password) {
        if (StringUtils.isBlank(password)) {
            return "";
        }
        String pwd = StringUtils.left(password, 0);
        return StringUtils.padAfter(pwd, StringUtils.length(password), "*");
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：6222600**********1234
     *
     * @param cardNum cardNum
     * @return 6222600**********1234
     */
    public static String hideBankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address       address
     * @param sensitiveSize 敏感信息长度
     * @return 北京市海淀区****
     */
    public static String hideAddress(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.padAfter(StringUtils.left(address, length - sensitiveSize), length, "*");
    }
}
