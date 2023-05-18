package com.chua.poi.support.excel.file.export;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;

/**
 * 导出
 *
 * @author CH
 */
@Spi("xlsx")
public class XlsxExportFile extends XlsExportFile {

    public XlsxExportFile(ExportConfiguration configuration) {
        super(configuration);
    }


    @Override
    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLSX;
    }
}
