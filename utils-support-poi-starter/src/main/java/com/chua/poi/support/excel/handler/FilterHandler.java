package com.chua.poi.support.excel.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 过滤
 *
 * @author CH
 */
public class FilterHandler implements SheetWriteHandler {
    public String autoFilterRange = "1:1";

    public FilterHandler(String autoFilterRange) {
        this.autoFilterRange = autoFilterRange;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
    }
}
