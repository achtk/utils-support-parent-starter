package com.chua.poi.support.database.transfer.file;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;

import java.io.OutputStream;

/**
 * xlsx
 */
@Spi("xlsx")
public class XlsxReaderChannel extends XlsReaderChannel {

    public XlsxReaderChannel(Object obj) {
        super(obj);
    }

    public XlsxReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
    }

    @Override
    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLSX;
    }
}
