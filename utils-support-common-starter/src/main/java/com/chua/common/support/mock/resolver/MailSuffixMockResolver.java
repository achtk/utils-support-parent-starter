package com.chua.common.support.mock.resolver;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.mock.MockValue;
import com.chua.common.support.annotations.Spi;

/**
 * 邮箱后缀
 *
 * @author CH
 */
@Spi("mail_suffix")
public class MailSuffixMockResolver extends MailMockResolver {
    @Override
    public String resolve(MockValue mock, ExpressionParser expressionParser) {
        return EMAIL_SUFFIX[(int) (Math.random() * EMAIL_SUFFIX.length)];
    }
}
