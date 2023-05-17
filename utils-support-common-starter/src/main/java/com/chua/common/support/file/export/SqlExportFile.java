package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.function.NameAware;
import com.chua.common.support.unit.name.NamingCase;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * sql
 * @author CH
 */
@Spi("sql")
public class SqlExportFile extends AbstractExportFile{
    public SqlExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        StringBuffer buffer = new StringBuffer();
        for (T datum : data) {
            buffer.append(createSql(datum));
            buffer.append(";\r\n");
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, configuration.charset())) {
            writer.write(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建sql
     * @param datum 数据
     * @return 结果
     * @param <T> 类型
     */
    private <T> String createSql(T datum) {
        StringBuffer stringBuffer = new StringBuffer();
        BeanMap mapping = BeanMap.create(datum);
        String simpleName = datum.getClass().getSimpleName();

        stringBuffer.append("INSERT INTO ").append(NamingCase.toCamelUnderscore(simpleName).toUpperCase())
                .append("(`").append(Joiner.on("`,`").join(mapping.keySet())).append("`)")
                .append(" VALUE ('").append(Joiner.on("','").join(mapping.values())).append("')");

        return stringBuffer.toString();
    }
}
