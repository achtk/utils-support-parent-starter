package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.Wrapper;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.spi.ServiceProvider;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DELETE;
import static com.chua.common.support.mapping.database.SqlResolver.PATTERN;

/**
 * 解释器
 *
 * @author CH
 */
@Extension("delete")
public class DeleteResolver implements Resolver {

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        ExpressionParser expressionParser = ServiceProvider.of(ExpressionParser.class).getExtension("spring");
        Wrapper<?> wrapper = (Wrapper<?>) args[0];
        List<Object> values = new LinkedList<>();

        expressionParser.setVariable("ew", wrapper);
        String sqlSegment = wrapper.getSqlSegment();
        Matcher matcher = PATTERN.matcher(sqlSegment);

        StringBuilder newSql = new StringBuilder();
        int endOffset = -1;
        while (matcher.find()) {
            String group = matcher.group(2);
            newSql.append(matcher.group(1)).append("?").append(matcher.group(3));
            endOffset = matcher.end();
            values.add(expressionParser.parseExpression(group).getValue());
        }

        if (endOffset < sqlSegment.length()) {
            newSql.append(sqlSegment.substring(endOffset));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SYMBOL_DELETE).append("`").append(metadata.getTable()).append("`");

        if (newSql.length() != 0) {
            stringBuilder.append(CommonConstant.SYMBOL_WHERE).append(newSql);
        }
        JdbcInquirer jdbcInquirer = new JdbcInquirer(dataSource, true);

        return jdbcInquirer.update(stringBuilder.toString(), values.toArray());
    }
}
