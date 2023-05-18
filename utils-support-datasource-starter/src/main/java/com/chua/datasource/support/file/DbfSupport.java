package com.chua.datasource.support.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.javadbf.DbfField;
import com.chua.common.support.file.javadbf.DbfReader;
import com.chua.common.support.lang.profile.Profile;
import com.chua.datasource.support.rule.TableEnumerator;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * csv
 *
 * @author CH
 */
@Spi("dbf")
public class DbfSupport extends AbstractFileSupport {
    protected AbstractEnumerable<Object> dataList;

    public DbfSupport(Profile configureAttributes, String file, String suffix) {
        super(configureAttributes, file, suffix);
        if (mem) {
            this.dataList = new AbstractEnumerable<Object>() {
                @Override
                public Enumerator<Object> enumerator() {
                    return new TableEnumerator(getDataList());
                }
            };
        }
    }

    /**
     * Returns the data list of the table.
     */
    public List<Object[]> getDataList() {
        List<Object[]> rs = new LinkedList<>();
        try (InputStream read = getStream()) {
            DbfReader dbfReader = new DbfReader(read);
            dbfReader.setCharacterSetName("UTF-8");

            int i = 0;
            Object[] objects;
            while ((objects = dbfReader.nextRecord()) != null) {
                rs.add(objects);
            }
            return rs;
        } catch (Exception ignore) {
        }
        return Collections.emptyList();
    }

    @Override
    protected InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        final List<RelDataType> types = new ArrayList<>();
        List<String> names = new LinkedList<>();


        List<DbfField> fields = new LinkedList<>();
        try (InputStream read = getStream()) {
            DbfReader dbfReader = new DbfReader(read);
            dbfReader.setCharacterSetName("UTF-8");
            int fieldCount = dbfReader.getFieldCount();
            for (int i = 0; i < fieldCount; i++) {
                fields.add(dbfReader.getField(i));
            }
        } catch (Exception ignore) {
        }

        for (DbfField field : fields) {
            names.add(mapping(field.getName()));
            types.add(relDataTypeFactory.createSqlType(SqlTypeName.VARCHAR));
        }

        return relDataTypeFactory.createStructType(Pair.zip(names, types));
    }

    @Override
    public Enumerable<Object> find(Profile attributes, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields) {
        return aggregate(profile, null, null);
    }

    @Override
    public Enumerable<Object> aggregate(Profile configureAttributes, List<Map.Entry<String, Class>> fields, List<String> operations) {
        if (mem) {
            return dataList;
        }
        return new AbstractEnumerable<Object>() {
            @Override
            public Enumerator<Object> enumerator() {
                return new TableEnumerator(getDataList());
            }
        };
    }
}
