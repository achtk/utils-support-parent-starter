package com.chua.poi.support.excel.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.chua.poi.support.excel.listener.ExcelHandlerListener;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

/**
 * 下拉
 *
 * @author CH
 */
public class SelectCustomSheetWriteHandler implements CellWriteHandler {


    private final ExcelHandlerListener listener;

    public SelectCustomSheetWriteHandler(ExcelHandlerListener listener) {
        this.listener = listener;
    }

    /**
     * called after the cell is disposed
     *
     * @param writeSheetHolder 写入的sheet
     * @param writeTableHolder 写入的table
     * @param cellDataList     单元格数据
     * @param cell             单元格
     * @param head             标题
     * @param relativeRowIndex 索引
     * @param isHead           是否是标题列
     */
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        if (!isHead) {
            if (listener.isSelect(head.getHeadNameList().get(0))) {
                //设置value下拉框
                setSelectDataList(writeSheetHolder, head, cell.getRowIndex(), cell.getColumnIndex());
            }
        }
    }


    /**
     * 设置下拉框数据
     *
     * @param writeSheetHolder sheet
     * @param head             头
     * @param rowIndex         行号
     * @param columnIndex      列号
     */
    private void setSelectDataList(WriteSheetHolder writeSheetHolder, Head head, int rowIndex, int columnIndex) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 设置下拉列表的行： 首行，末行，首列，末列
        CellRangeAddressList rangeList = new CellRangeAddressList(rowIndex, rowIndex, columnIndex, columnIndex);
        // 设置下拉列表的值
        DataValidationConstraint constraint = helper.createExplicitListConstraint(listener.getSelectValue(head.getHeadNameList().get(0), rowIndex, columnIndex));
        // 设置约束
        DataValidation validation = helper.createValidation(constraint, rangeList);
        // 阻止输入非下拉选项的值
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(false);
        validation.createErrorBox("提示", "请选择下拉选项中的内容");
        sheet.addValidationData(validation);
    }

}
