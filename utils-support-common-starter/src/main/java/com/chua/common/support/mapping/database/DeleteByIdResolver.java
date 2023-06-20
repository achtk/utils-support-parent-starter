package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.MapUtils;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_DELETE;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_UPDATE;

/**
 * 解释器
 *
 * @author CH
 */
@Extension("DeleteById")
public class DeleteByIdResolver implements Resolver{

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SYMBOL_DELETE).append("`").append(metadata.getTable()).append("`");
        List<Column> column = metadata.getColumn();
        Object arg = args[0];

        List<Object> values = new LinkedList<>();
        List<String> keys = new LinkedList<>();
        for (Column column1 : column) {
            if(column1.isPrimary()) {
                keys.add("`" + column1.getName() + "` = ?");
                values.add(arg);
                break;
            }
        }

        stringBuilder.append(" WHERE 1 = 1 AND ").append(Joiner.on(" AND ").join(keys));
        JdbcInquirer jdbcInquirer = new JdbcInquirer(dataSource, true);

        return jdbcInquirer.update(stringBuilder.toString(), values.toArray());
    }
}
