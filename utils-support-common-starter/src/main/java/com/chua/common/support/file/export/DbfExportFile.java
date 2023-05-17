package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.javadbf.DbfField;
import com.chua.common.support.file.javadbf.DbfWriter;
import com.chua.common.support.value.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * dbf
 *
 * @author CH
 */
@Spi("dbf")
public class DbfExportFile extends AbstractExportFile {
    public DbfExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        DbfField[] headerFields = new DbfField[headers.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair pair = pairs[i];
            DbfField item = new DbfField();
            String name = pair.getName();
            byte[] bytes1 = name.getBytes();
            if (bytes1.length > 10) {
                item.setName(name.substring(0, 10));
            } else {
                item.setName(name);
            }
//            if (Date.class.isAssignableFrom(pair.getJavaType())) {
//                item.setDataType((byte) 'D');
//            } else {
            item.setDataType((byte) 'C');
            item.setFieldLength(254);
//            }
            headerFields[i] = item;
        }

        try (OutputStream writer = outputStream) {
            DbfWriter dbfWriter = new DbfWriter();
            dbfWriter.setCharacterSetName(configuration.charset());
            dbfWriter.setFields(headerFields);
            for (T datum : data) {
                Object[] array = createArray(datum, false);
                if (null != array) {
                    try {
                        dbfWriter.addRecord(array);
                    } catch (Exception ignored) {
                    }
                }
            }
            dbfWriter.write(writer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
