package com.chua.poi.support.database.transfer.file;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;

import java.io.InputStream;

/**
 * xlsx
 */
@Spi("xlsx")
public class XlsxWriterChannel extends XlsWriterChannel {

    public XlsxWriterChannel(Object obj) {
        super(obj);
    }

    public XlsxWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        super(configuration, inputStream);
    }

    @Override
    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLSX;
    }
}
