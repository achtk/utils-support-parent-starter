package com.chua.common.support.file.imports;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.export.resolver.NamedResolver;
import com.chua.common.support.file.javadbf.DbfHeader;
import com.chua.common.support.file.javadbf.DbfReader;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Pair;

import java.io.InputStream;

/**
 * dbf
 *
 * @author CH
 */
@Spi("dbf")
public class DbfImportFile extends AbstractImportFile {


    public DbfImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        try (InputStream read = inputStream) {
            DbfReader dbfReader = new DbfReader(read);
            dbfReader.setCharacterSetName(configuration.charset());

            int i = 0;
            Object[] objects;
            while ((objects = dbfReader.nextRecord()) != null) {
                try {
                    if (skip > 0 && i < skip) {
                        continue;
                    }
                    listener.accept(doAnalysis(dbfReader.getHeader(), type, objects));
                    if (listener.isEnd(i)) {
                        break;
                    }
                } finally {
                    i++;
                }
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 遍历数据
     *
     * @param header  header
     * @param type    类型
     * @param objects 数据
     * @param <T>     类型
     * @return 结果
     */
    private <T> T doAnalysis(DbfHeader header, Class<T> type, Object[] objects) {
        T forObject = ClassUtils.forObject(type);
        if (null == forObject) {
            return null;
        }

        NamedResolver namedResolver = configuration.namedResolver();
        ClassUtils.doWithFields(type, field -> {

            Pair pair = namedResolver.name(field);
            int index = -1;
            try {
                index = header.indexOf(pair.getLabel());
            } catch (Exception ignored) {
                index = header.indexOf(pair.getName());
            }

            if (-1 == index) {
                index = header.indexOf(pair.getName());
            }

            if (-1 == index) {
                return;
            }

            Object value = objects[index];

            if (null == value) {
                return;
            }

            if (value instanceof String) {
                value = value.toString().trim();
            }

            ClassUtils.setAllFieldValue(field, value, type, forObject);
        });
        return forObject;
    }

    @Override
    public void afterPropertiesSet() {
    }

}
