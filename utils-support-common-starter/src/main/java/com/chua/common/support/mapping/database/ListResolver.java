package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;

import javax.sql.DataSource;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SELECT;

/**
 * sql解析器
 * @author CH
 */
@Extension("list")
public class ListResolver implements Resolver {

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        StringBuilder stringBuilder = new StringBuilder(SYMBOL_SELECT);
        stringBuilder.append(" * FROM `").append(metadata.getTable()).append("`");
        try {
            return new JdbcInquirer(dataSource, true).query(stringBuilder.toString(), metadata.getJavaType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
