package com.chua.poi.support.excel.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 冻结行
 *
 * @author CH
 */
public class FreezeHandler implements SheetWriteHandler {

    public int colSplit = 0, rowSplit = 1, leftmostColumn = 0, topRow = 1;

    /**
     * CreateFreezePane(0,1,0,1):冻结第一行,冻结行下侧第一行的左边框显示“2”
     * CreateFreezePane(1,0,1,0):冻结第一列，冻结列右侧的第一列为B列
     * CreateFreezePane(2,0,5,0):冻结左侧两列，冻结列右侧的第一列为F列
     *
     * @param colSplit       表示要冻结的列数；
     * @param rowSplit       表示要冻结的行数；
     * @param leftmostColumn 表示被固定列右边第一列的列号；
     * @param topRow         表示被固定行下边第一列的行号;
     */
    public FreezeHandler(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
        this.leftmostColumn = leftmostColumn;
        this.topRow = topRow;
    }

    public FreezeHandler() {
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);

    }
}
