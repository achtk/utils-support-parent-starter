package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.javadbf.DbfField;
import com.chua.common.support.file.javadbf.AbstractDbfWriter;
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
    private AbstractDbfWriter dbfWriter;
    private OutputStream outputStream;

    public DbfExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        this.outputStream = outputStream;
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
            item.setDataType((byte) 'C');
            item.setFieldLength(254);
            headerFields[i] = item;
        }

        try {
            this.dbfWriter = new AbstractDbfWriter();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void append(List<T> records) {
        for (T datum : records) {
            Object[] array = createArray(datum, false);
            if (null != array) {
                try {
                    dbfWriter.addRecord(array);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        dbfWriter.write(outputStream);
        dbfWriter.close();
    }
}
