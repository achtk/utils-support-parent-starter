package com.chua.common.support.database.executor;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.JdbcType;
import com.chua.common.support.database.entity.Primary;
import com.chua.common.support.database.expression.Expression;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.jdbc.Type;
import com.chua.common.support.database.jdbc.id.GenerationType;
import com.chua.common.support.database.jdbc.model.ColumnModel;
import com.chua.common.support.database.jdbc.model.TableModel;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author CH
 */
public class DelegateMetadataExecutor implements MetadataExecutor {

    private final Expression expression;

    public DelegateMetadataExecutor(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Object datasource, Action action) {
        DataSource ds = (DataSource) datasource;
        Dialect dialect = Dialect.create(ds);

        Metadata metadata1 = expression.getValue(Metadata.class);
        TableModel tableModel = new TableModel(metadata1.getTable());
        List<Column> column = metadata1.getColumn();
        for (Column column1 : column) {
            ColumnModel columnModel = new ColumnModel(column1.getName());
            columnModel.setLength(column1.getLength());
            columnModel.setComment(column1.getComment());
            columnModel.setColumnType(null == column1.getJdbcType() || column1.getJdbcType() == JdbcType.NONE ? Type.valueTypeOf(column1.getJavaType()) : Type.valueOf(column1.getJdbcType().name()));
            columnModel.setColumnDefinition(column1.getColumnDefinition());

            if (!StringUtils.isNullOrEmpty(column1.getDefaultValue())) {
                columnModel.setDefaultValue(column1.getDefaultValue());
            }

            if (column1.isPrimary()) {
                Primary primary = column1.getPrimary();
                columnModel.setIdGenerationType("auto".equalsIgnoreCase(primary.getStrategy()) ? GenerationType.AUTO : GenerationType.IDENTITY);
            }
            columnModel.setInsertable(column1.isInsertable());
            columnModel.setUpdatable(column1.isUpdatable());
            columnModel.setNullable(column1.isNullable());
            columnModel.setScale(column1.getScale());
            columnModel.setPrecision(column1.getPrecision());

            tableModel.addColumn(columnModel);
        }

        com.chua.common.support.database.jdbc.Dialect guessDialect = com.chua.common.support.database.jdbc.Dialect.guessDialect(ds);
        JdbcInquirer jdbcInquirer = new JdbcInquirer(ds, true);
        if (action == Action.CREATE) {
            create(jdbcInquirer, guessDialect, tableModel);
            return;
        }

        if (action == Action.DROP) {
            drop(jdbcInquirer, guessDialect, tableModel);
            return;
        }

        if (action == Action.DROP_CREATE) {
            drop(jdbcInquirer, guessDialect, tableModel);
            create(jdbcInquirer, guessDialect, tableModel);
            return;
        }


        if (action == Action.UPDATE) {
            update(jdbcInquirer, guessDialect, tableModel);
            return;
        }
    }

    private void update(JdbcInquirer jdbcInquirer, com.chua.common.support.database.jdbc.Dialect guessDialect, TableModel tableModel) {

        //TODO:
    }

    private void drop(JdbcInquirer jdbcInquirer, com.chua.common.support.database.jdbc.Dialect guessDialect, TableModel tableModel) {
        String[] strings = guessDialect.toDropDDL(tableModel);
        for (String string : strings) {
            try {
                jdbcInquirer.executeStatement(string);
            } catch (Exception ignored) {
            }
        }

    }

    private void create(JdbcInquirer jdbcInquirer, com.chua.common.support.database.jdbc.Dialect guessDialect, TableModel tableModel) {
        String[] strings = guessDialect.toCreateDDL(tableModel);
        for (String string : strings) {
            try {
                jdbcInquirer.executeStatement(string);
            } catch (Exception ignored) {
            }
        }
    }

}
