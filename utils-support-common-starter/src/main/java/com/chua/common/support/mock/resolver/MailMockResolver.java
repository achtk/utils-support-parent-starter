package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;

import static com.chua.common.support.utils.NumberUtils.getNum;

/**
 * 邮箱
 *
 * @author CH
 */
@Spi("mail")
public class MailMockResolver implements MockResolver {
    public static final String BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    protected static final String[] EMAIL_SUFFIX = new String[]{"@gmail.com", "@yahoo.com", "@msn.com", "@hotmail.com", "@aol.com", "@ask.com", "@live.com", "@qq.com", "@0355.net", "@163.com", "@163.net", "@263.net", "@3721.net", "@yeah.net", "@googlemail.com", "@126.com", "@sina.com", "@sohu.com", "@yahoo.com.cn"};

    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return getRandom(5, 20);
    }

    /**
     * 获取随机邮箱
     *
     * @param min 最小位数
     * @param max 最大位数
     * @return 邮箱
     */
    public static String getRandom(int min, int max) {
        int length = getNum(min, max);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * BASE.length());
            sb.append(BASE.charAt(number));
        }
        sb.append(EMAIL_SUFFIX[(int) (Math.random() * EMAIL_SUFFIX.length)]);
        return sb.toString();
    }
}
