package com.chua.poi.support.table;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.datasource.support.file.AbstractFileSupport;
import com.chua.datasource.support.rule.TableEnumerator;
import lombok.SneakyThrows;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.type.SqlTypeName;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * excel
 *
 * @author CH
 */
@Spi({"xls", "xlsx", "csv"})
public class ExcelSupport extends AbstractFileSupport {


    private AbstractEnumerable<Object> memDataAble;


    public ExcelSupport(Profile configureAttributes, String file, String suffix) {
        super(configureAttributes, file, suffix);
        if (mem) {
            DataReadListener readListener = new DataReadListener();
            EasyExcel.read(getStream(), readListener)
                    .excelType(ExcelTypeEnum.valueOf(FileUtils.getBaseName(suffix.toUpperCase())))
                    .charset(StandardCharsets.UTF_8)
                    .autoCloseStream(true)
                    .sheet().doReadSync();
            this.memDataAble = new AbstractEnumerable<Object>() {
                @Override
                public Enumerator<Object> enumerator() {
                    return new TableEnumerator(readListener.getData());
                }
            };
        }
    }

    @SneakyThrows
    @Override
    protected InputStream getStream() {
        return new FileInputStream(file);
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        typeFactory = new JavaTypeFactoryImpl();
        HeaderReadListener readListener = new HeaderReadListener(profile.getType("mapping", Collections.emptyMap(), Map.class));
        EasyExcel.read(getStream(), readListener)
                .excelType(ExcelTypeEnum.valueOf(FileUtils.getBaseName(suffix.toUpperCase())))
                .charset(StandardCharsets.UTF_8)
                .autoCloseStream(true)
                .sheet().doReadSync();
        List<String> columns = readListener.getColumns();
        List<RelDataType> relDataTypes = new LinkedList<>();
        for (int i = 0; i < columns.size(); i++) {
            RelDataType sqlType = typeFactory.createSqlType(SqlTypeName.VARCHAR);
            sqlType = typeFactory.createTypeWithNullability(sqlType, true);
            relDataTypes.add(sqlType);
        }
        return typeFactory.createStructType(relDataTypes, new LinkedList<>(columns));
    }

    @Override
    public Enumerable<Object> find(Profile attributes, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields) {
        return aggregate(attributes, null, null);
    }

    @Override
    public Enumerable<Object> aggregate(Profile configureAttributes, List<Map.Entry<String, Class>> fields, List<String> operations) {
        return createData();
    }

    private Enumerable<Object> createData() {
        if (mem) {
            return memDataAble;
        }

        DataReadListener readListener = new DataReadListener();
        EasyExcel.read(getStream(), readListener)
                .excelType(ExcelTypeEnum.valueOf(FileUtils.getBaseName(suffix.toUpperCase())))
                .charset(StandardCharsets.UTF_8)
                .autoCloseStream(true)
                .sheet().doReadSync();
        return new AbstractEnumerable<Object>() {
            @Override
            public Enumerator<Object> enumerator() {
                return new TableEnumerator(readListener.getData());
            }
        };
    }
}
