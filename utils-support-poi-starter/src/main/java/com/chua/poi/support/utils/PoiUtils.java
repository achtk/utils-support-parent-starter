package com.chua.poi.support.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Iterator;

/**
 * sheet
 *
 * @author CH
 */
public final class PoiUtils {
    /***
     * 拷贝行
     * @param sxssfSheet 源
     * @param xssfSheet 目标
     */
    public static void copyRow(SXSSFSheet sxssfSheet, XSSFSheet xssfSheet) {
        int lastRowNum = sxssfSheet.getLastRowNum();
        int index = 1;
        for (int i = sxssfSheet.getLastFlushedRowNum() + 1; i < lastRowNum; i++) {
            SXSSFRow row = sxssfSheet.getRow(i);
            XSSFRow row1 = xssfSheet.createRow(index++);
            copyRow(row, row1);
        }
    }

    /***
     * 拷贝行
     * @param row 源
     * @param row1 目标
     */
    private static void copyRow(SXSSFRow row, XSSFRow row1) {
        int index = 0;
        for (Cell cell : row) {
            XSSFCell cell1 = row1.createCell(index++);
            if (cell.getCellType() == CellType.STRING) {
                cell1.setCellValue(cell.getStringCellValue());
                continue;
            }

            if (cell.getCellType() == CellType.NUMERIC) {
                cell1.setCellValue(cell.getNumericCellValue());
                continue;
            }

            if (cell.getCellType() == CellType.BOOLEAN) {
                cell1.setCellValue(cell.getBooleanCellValue());
                continue;
            }

            if (cell.getCellType() == CellType.BLANK) {
                cell1.setCellValue("");
                continue;
            }

        }
    }
}
