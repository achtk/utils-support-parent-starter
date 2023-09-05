package com.chua.common.support.database.executor;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.expression.Expression;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.lang.formatter.DdlFormatter;
import com.chua.common.support.lang.formatter.HighlightingFormatter;
import com.chua.common.support.log.Log;
import com.chua.common.support.task.cache.Cacheable;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CH
 */
public class DelegateMetadataExecutor implements MetadataExecutor {

    private final Expression expression;

    private static final Log log = Log.getLogger(MetadataExecutor.class);
    private static final Cacheable<DataSource, Dialect> CACHEABLE = Cacheable.auto();

    public DelegateMetadataExecutor(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Object datasource, Action action) {
        DataSource ds = (DataSource) datasource;

        Metadata<?> metadata1 = expression.getValue(Metadata.class);
        Dialect guessDialect = CACHEABLE.getOrPut(ds, Dialect.guessDialect(ds));
        JdbcInquirer jdbcInquirer = new JdbcInquirer(ds, true);
        if (action == Action.CREATE) {
            create(jdbcInquirer, guessDialect, metadata1);
            return;
        }

        if (action == Action.DROP) {
            drop(jdbcInquirer, guessDialect, metadata1);
            return;
        }

        if (action == Action.DROP_CREATE) {
            drop(jdbcInquirer, guessDialect, metadata1);
            create(jdbcInquirer, guessDialect, metadata1);
            return;
        }


        if (action == Action.UPDATE) {
            update(jdbcInquirer, guessDialect, metadata1);
            return;
        }
    }

    private void update(JdbcInquirer jdbcInquirer, Dialect guessDialect, Metadata<?> metadata1) {
        //TODO:
        List<Column> columns = jdbcInquirer.getColumn(metadata1.getTable());
        if (columns.isEmpty()) {
            this.create(jdbcInquirer, guessDialect, metadata1);
            return;
        }
        List<Column> tpl = equals(columns, metadata1.getColumn());
        if (tpl.isEmpty()) {
            return;
        }
        for (Column column : tpl) {
            String alterColumn = guessDialect.createAlterColumn(metadata1.getTable(), column);
            try {
                jdbcInquirer.executeStatement(alterColumn);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (log.isDebugEnabled()) {
                log.debug("\r\n {}", HighlightingFormatter.INSTANCE.format(new DdlFormatter().format(alterColumn)));
            }
        }
    }

    private List<Column> equals(List<Column> databaseColumn, List<Column> javaColumn) {
        List<Column> tpl = new LinkedList<>();
        for (Column columnModel : javaColumn) {
            if (databaseColumn.stream().noneMatch(it -> it.getName().equals(columnModel.getName()))) {
                tpl.add(columnModel);
            }
        }
        return tpl;
    }

    private void drop(JdbcInquirer jdbcInquirer, Dialect guessDialect, Metadata<?> metadata) {
        String string = guessDialect.createDropSql(metadata);
        try {
            jdbcInquirer.executeStatement(string);
        } catch (Exception ignored) {
        }
    }

    private void create(JdbcInquirer jdbcInquirer, Dialect dialect, Metadata<?> metadata) {
        String string = dialect.createCreateSql(metadata);
        try {
            jdbcInquirer.executeStatement(string);
            if (log.isDebugEnabled()) {
                log.debug("\r\n {}", HighlightingFormatter.INSTANCE.format(new DdlFormatter().format(string)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
