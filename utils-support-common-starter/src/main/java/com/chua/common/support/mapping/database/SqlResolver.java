package com.chua.common.support.mapping.database;

import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.spi.ServiceProvider;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql解析器
 *
 * @author CH
 */
public class SqlResolver implements Resolver {
    private final Method method;
    private final String sql;
    public static final Pattern PATTERN = Pattern.compile("(.*?)#\\{(.*?)\\}(.*?)");

    public SqlResolver(Method method, String sql) {
        this.method = method;
        this.sql = sql;
    }

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        Parameter[] parameters = method.getParameters();
        ExpressionParser expressionParser = ServiceProvider.of(ExpressionParser.class).getExtension("spring");
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            expressionParser.setVariable(parameter.getName(), args[i]);
        }

        Matcher matcher = PATTERN.matcher(sql);
        StringBuilder newSql = new StringBuilder();
        List<Object> value = new LinkedList<>();
        while (matcher.find()) {
            String group = matcher.group(2);
            newSql.append(matcher.group(1)).append("?").append(matcher.group(3));
            value.add(expressionParser.parseExpression(group).getValue());
        }
        return new JdbcInquirer(dataSource, true).query(newSql.toString(), value.toArray(), metadata.getJavaType());
    }
}
