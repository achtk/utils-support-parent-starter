package com.chua.common.support.database.sqldialect;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.database.SqlModel;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_QUESTION;

/**
 * mysql
 *
 * @author CH
 */
@Spi({"mysql"})
public class MysqlDialect implements Dialect {
    @Override
    public SqlModel formatPageSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ").append(SYMBOL_QUESTION);
        if (offset != 0L) {
            sql.append(SYMBOL_COMMA).append(SYMBOL_QUESTION);
            return new SqlModel(sql.toString(), offset, limit);
        }
        return new SqlModel(sql.toString(), limit);
    }

    @Override
    public String driverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String createCreateSql(Metadata<?> metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommonConstant.SYMBOL_CREATE_TABLE)
                .append(" `").append(metadata.getTable()).append("` (");

        for (Column column : metadata.getColumn()) {
            sb.append(createColumn(column));
            if (column.isPrimary()) {
                sb.append(" AUTO_INCREMENT ");
            }
            sb.append(",");
        }

        String keyProperty = metadata.getKeyProperty();
        if (StringUtils.isNotBlank(keyProperty)) {
            sb.append("KEY ").append(keyProperty).append(" (").append(keyProperty).append(")");
        } else {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append(" ) ENGINE=InnoDB DEFAULT CHARSET=").append(metadata.getUncode()).append(" COMMENT='").append(metadata.getTableComment()).append("'");
        return sb.toString();
    }

}
