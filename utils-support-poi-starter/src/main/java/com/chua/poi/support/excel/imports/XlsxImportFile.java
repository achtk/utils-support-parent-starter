package com.chua.poi.support.excel.imports;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.imports.AbstractImportFile;

/**
 * 导入
 *
 * @author CH
 */
@Spi("xlsx")
public class XlsxImportFile extends XlsImportFile {

    public XlsxImportFile(ExportConfiguration configuration) {
        super(configuration);
    }


    @Override
    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLSX;
    }
}
