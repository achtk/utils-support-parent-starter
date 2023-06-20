package com.chua.common.support.mapping.database;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_INSERT;

/**
 * 解释器
 *
 * @author CH
 */
@Extension("saveBatch")
public class SaveBatchResolver implements Resolver{

    @Override
    public Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata) {
        List<Object> arg = (List<Object>) args[0];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SYMBOL_INSERT).append("`").append(metadata.getTable()).append("`");
        stringBuilder.append("(");
        List<Column> column = metadata.getColumn();
        List<String> names = new LinkedList<>();
        Column primaryKey = null;
        for (Column it : column) {
            names.add(it.getName());
            if (it.isPrimary()) {
                primaryKey = it;
            }
            stringBuilder.append("`").append(it.getName()).append("`,");
        }

        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(")");
        stringBuilder.append(" VALUES(").append(StringUtils.repeat("?,", column.size()));
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append(")");

        JdbcInquirer jdbcInquirer = new JdbcInquirer(dataSource, false);

        int[] batch = jdbcInquirer.batch(stringBuilder.toString(), CollectionUtils.toArray(arg, names));
        if(null != primaryKey) {
            for (int j = 0; j < batch.length; j++) {
                int i = batch[j];
                ClassUtils.setFieldValue(primaryKey.getFieldName(), i, arg.get(j));
            }
        }

        return args[0];
    }
}
