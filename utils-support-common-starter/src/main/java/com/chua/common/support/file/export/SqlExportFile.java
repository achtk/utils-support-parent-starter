package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.unit.name.NamingCase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * sql
 *
 * @author CH
 */
@Spi("sql")
public class SqlExportFile extends AbstractExportFile {
    private OutputStreamWriter writer;

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

        try {
            this.writer = new OutputStreamWriter(outputStream, configuration.charset());
            writer.write(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void append(List<T> records) {
        StringBuffer buffer = new StringBuffer();
        for (T datum : records) {
            buffer.append(createSql(datum));
            buffer.append(";\r\n");
        }
        try {
            writer.write(buffer.toString());
        } catch (IOException ignored) {
        }
    }

    /**
     * 创建sql
     *
     * @param datum 数据
     * @param <T>   类型
     * @return 结果
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
