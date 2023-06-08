package com.chua.hibernate.support.database.executor;

import com.chua.common.support.constant.Action;
import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.entity.Index;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.expression.Expression;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.lang.formatter.DdlFormatter;
import com.chua.common.support.lang.formatter.HighlightingFormatter;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * 元数据执行器
 *
 * @author CH
 */
@Slf4j
public class HibernateMetadataExecutor implements MetadataExecutor {

    private final Expression expression;

    public HibernateMetadataExecutor(Expression expression) {
        this.expression = expression;
    }


    @Override
    @SuppressWarnings("ALL")
    public void execute(Object datasource, Action action) {
        DataSource ds = (DataSource) datasource;
        Dialect dialect = Dialect.create(ds);

        Metadata metadata1 = expression.getValue(Metadata.class);
        Table table = new Table(metadata1.getTable());
        table.setComment(metadata1.getTableComment());
        table.setSchema(metadata1.getDatabase());
        table.setCatalog(metadata1.getCatalog());
        PrimaryKey primaryKey = new PrimaryKey(table);

        List<Column> column = metadata1.getColumn();
        List<Column> primary = new LinkedList<>();

        for (Column o : column) {
            if(!o.isExist()) {
                continue;
            }
            org.hibernate.mapping.Column itemColumn = new org.hibernate.mapping.Column(o.getName());
            itemColumn.setComment(o.getComment());
            itemColumn.setLength(o.getLength());
            itemColumn.setNullable(o.isNullable());
            if (StringUtils.isNotEmpty(o.getDefaultValue())) {
                itemColumn.setDefaultValue(o.getDefaultValue());
            }

            itemColumn.setScale(o.getScale());
            itemColumn.setLength(o.getLength() == 0 ? (o.getJavaType() == String.class ? 255 : 11) : o.getLength());
            itemColumn.setScale(o.getScale());
            itemColumn.setPrecision(o.getPrecision());
            if (!StringUtils.isNullOrEmpty(o.getDefaultValue())) {
                itemColumn.setDefaultValue(o.getDefaultValue());
            }

            itemColumn.setValue(new HibernateIdValue(o, new HibernateSimpleMetadata(o.getJavaType())));
            if (o.isPrimary()) {
                primaryKey.addColumn(itemColumn);
                primary.add(o);
            }
            itemColumn.setSqlType(null);
            table.addColumn(itemColumn);
        }

        if (!primaryKey.getColumns().isEmpty()) {
            Column first = CollectionUtils.findFirst(primary);
            HibernateIdValue simpleValue = new HibernateIdValue(first, (MetadataImplementor) null);
            simpleValue.setIdentifierGeneratorStrategy(first.getPrimary().getStrategy());
            table.setIdentifierValue(simpleValue);
            table.setPrimaryKey(primaryKey);

        }

        List<Index> indices = metadata1.getIndex();
        for (Index index : indices) {
            org.hibernate.mapping.Index index1 = new org.hibernate.mapping.Index();
            index1.addColumn(new org.hibernate.mapping.Column(index.getValue()), index.getOrder());
            index1.setTable(table);

            table.addIndex(index1);
        }


        org.hibernate.dialect.Dialect d = (org.hibernate.dialect.Dialect) dialect.getHibernateDialect();

        JdbcInquirer jdbcInquirer = new JdbcInquirer(ds, true);
        if (action == Action.CREATE) {
            create(jdbcInquirer, d, table);
            return;
        }

        if (action == Action.DROP) {
            drop(jdbcInquirer, d, table);
            return;
        }


        if (action == Action.DROP_CREATE) {
            drop(jdbcInquirer, d, table);
            create(jdbcInquirer, d, table);
            return;
        }

        if (action == Action.UPDATE) {
            update(jdbcInquirer, d, table, datasource);
        }

    }

    private void update(JdbcInquirer jdbcInquirer, org.hibernate.dialect.Dialect d, Table table, Object datasource) {
        try {
            HibernateDatabaseMetadata databaseMetadata = new HibernateDatabaseMetadata(((DataSource) datasource).getConnection(), d, null, false);
            HibernateTableMetadata newMetadata = databaseMetadata.getTableMetadata(table.getName(), table.getSchema(), table.getCatalog(), false);
            if (null == newMetadata) {
                create(jdbcInquirer, d, table);
                return;
            }
            Iterator<String> iterator = table.sqlAlterStrings(d, null, newMetadata, SqlStringGenerationContextImpl.forBackwardsCompatibility(d, newMetadata.getCatalog(), newMetadata.getSchema()));
            while (iterator.hasNext()) {
                String script = iterator.next();
                try {
                    jdbcInquirer.executeStatement(script);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void drop(JdbcInquirer jdbcInquirer, org.hibernate.dialect.Dialect d, Table table) {
        String dropString = table.sqlDropString(d, null, null);
        log.info("\r\n {}", HighlightingFormatter.INSTANCE.format(new DdlFormatter().format(dropString)));
        try {
            jdbcInquirer.executeStatement(dropString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void create(JdbcInquirer jdbcInquirer, org.hibernate.dialect.Dialect d, Table table) {
        String sql = table.sqlCreateString(d, new HibernateSimpleMetadata(), null, null);
        log.debug("\r\n {}", HighlightingFormatter.INSTANCE.format(new DdlFormatter().format(sql)));
        try {
            jdbcInquirer.executeStatement(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
