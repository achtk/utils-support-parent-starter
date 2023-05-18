package com.chua.hibernate.support.database.executor;

import com.chua.common.support.converter.Converter;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * @author Administrator
 */
public class HibernateColumnMetadata implements ColumnInformation {
    private final String name;
    private final String typeName;
    private TableInformation tableInformation;
    private final int columnSize;
    private final int decimalDigits;
    private final String isNullable;
    private final int typeCode;

    HibernateColumnMetadata(ResultSet rs, TableInformation tableInformation) throws SQLException {
        name = rs.getString("COLUMN_NAME");
        columnSize = rs.getInt("COLUMN_SIZE");
        decimalDigits = rs.getInt("DECIMAL_DIGITS");
        isNullable = rs.getString("IS_NULLABLE");
        typeCode = rs.getInt("DATA_TYPE");
        typeName = new StringTokenizer(rs.getString("TYPE_NAME"), "() ").nextToken();
        this.tableInformation = tableInformation;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    @Override
    public TableInformation getContainingTableInformation() {
        return tableInformation;
    }

    @Override
    public Identifier getColumnIdentifier() {
        return new Identifier(name, true);
    }

    @Override
    public TruthValue getNullable() {
        return TruthValue.valueOf(Converter.convertIfNecessary(getNullable(), Boolean.class).toString().toUpperCase());
    }

    public String toString() {
        return "ColumnMetadata(" + name + ')';
    }

    public int getTypeCode() {
        return typeCode;
    }
}
