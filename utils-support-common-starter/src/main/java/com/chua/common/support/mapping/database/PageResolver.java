package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.SqlWrapper;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.lang.page.Page;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SELECT;
import static com.chua.common.support.mapping.database.SqlResolver.PATTERN;

/**
 * sql解析器
 * @author CH
 */
@Extension("page")
public class PageResolver implements Resolver {

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        Page page = (Page<?>) Arrays.stream(args).filter(it -> it instanceof Page).findFirst().get();
        StringBuilder stringBuilder = new StringBuilder(SYMBOL_SELECT);

        Dialect dialect = Dialect.create(dataSource);
        List<Object> param = new LinkedList<>();
        String sqlSelect = "*";
        String sqlSegment = "";
        ExpressionParser expressionParser = ServiceProvider.of(ExpressionParser.class).getExtension("spring");
        if(args.length > 1) {
            Object arg = args[1];
            expressionParser.setVariable("ew", arg);
            SqlWrapper<?> wrapper = (SqlWrapper<?>) arg;
             sqlSegment = wrapper.getSqlSegment();
            if(!StringUtils.isEmpty(wrapper.getSqlSelect())) {
                sqlSelect = wrapper.getSqlSelect();
            }

        }
        stringBuilder.append(sqlSelect).append(" FROM `").append(metadata.getTable()).append("` ");
        if(StringUtils.isNotEmpty(sqlSegment)) {
            Matcher matcher = PATTERN.matcher(sqlSegment);
            StringBuilder newSql = new StringBuilder();
            int endOffset = -1;
            while (matcher.find()) {
                String group = matcher.group(2);
                newSql.append(matcher.group(1)).append("?").append(matcher.group(3));
                endOffset = matcher.end();
                param.add(expressionParser.parseExpression(group).getValue());
            }

            if(endOffset < sqlSegment.length()) {
                newSql.append(sqlSegment.substring(endOffset));
            }

            if(newSql.length() != 0) {
                stringBuilder.append(CommonConstant.SYMBOL_WHERE).append(newSql);
            }
        }
        SqlModel sqlModel = dialect.formatPageSql(
                stringBuilder.toString(), (page.getPageNum() - 1) * page.getPageSize(), page.getPageSize());

        param.addAll(sqlModel.getArgs());
        try {
            List<?> query = new JdbcInquirer(dataSource, true).query(sqlModel.getSql(), param.toArray(), metadata.getJavaType());
            page.setData(query);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
