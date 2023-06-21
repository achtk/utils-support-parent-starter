package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;

import javax.sql.DataSource;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SELECT;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_WHERE;

/**
 * sql解析器
 * @author CH
 */
@Extension("GetById")
public class GetByIdResolver implements Resolver {

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        StringBuilder stringBuilder = new StringBuilder(SYMBOL_SELECT);
        stringBuilder.append(" * FROM `").append(metadata.getTable()).append("`");
        stringBuilder.append(SYMBOL_WHERE).append("`").append(metadata.getKeyProperty()).append("` = ?");
        try {
            return new JdbcInquirer(dataSource, true).queryOne(stringBuilder.toString(), args, metadata.getJavaType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
