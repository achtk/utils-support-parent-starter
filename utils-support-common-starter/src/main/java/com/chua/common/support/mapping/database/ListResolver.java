package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.Wrapper;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SELECT;
import static com.chua.common.support.mapping.database.SqlResolver.PATTERN;

/**
 * sql解析器
 * @author CH
 */
@Extension("list")
public class ListResolver implements Resolver {

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        StringBuilder stringBuilder = new StringBuilder(SYMBOL_SELECT);
        List<Object> value = new LinkedList<>();

        if(args.length == 1) {
            ExpressionParser expressionParser = ServiceProvider.of(ExpressionParser.class).getExtension("spring");
            Wrapper<?> wrapper = (Wrapper<?>) args[0];
            String sqlSegment = wrapper.getSqlSegment();
            String sqlSelect = wrapper.getSqlSelect();
            if(StringUtils.isEmpty(sqlSelect)) {
                sqlSelect = "*";
            }
            stringBuilder.append(sqlSelect).append(" FROM `").append(metadata.getTable()).append("` ");
            expressionParser.setVariable("ew", wrapper);
            Matcher matcher = PATTERN.matcher(sqlSegment);
            StringBuilder newSql = new StringBuilder();
            int endOffset = -1;
            while (matcher.find()) {
                String group = matcher.group(2);
                newSql.append(matcher.group(1)).append("?").append(matcher.group(3));
                endOffset = matcher.end();
                value.add(expressionParser.parseExpression(group).getValue());
            }

            if(endOffset < sqlSegment.length()) {
                newSql.append(sqlSegment.substring(endOffset));
            }

            if(newSql.length() != 0) {
                stringBuilder.append(CommonConstant.SYMBOL_WHERE).append(newSql);
            }
        } else {
            stringBuilder.append(" * FROM `").append(metadata.getTable()).append("`");
        }
        try {
            return new JdbcInquirer(dataSource, true).query(stringBuilder.toString(), value.toArray(), metadata.getJavaType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
