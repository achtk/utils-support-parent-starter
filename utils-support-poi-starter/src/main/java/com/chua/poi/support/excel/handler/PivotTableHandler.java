package com.chua.poi.support.excel.handler;

import com.alibaba.excel.event.Handler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.WorkbookWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.chua.common.support.reflect.Reflect;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.value.BeanValue;
import com.chua.poi.support.utils.PoiUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

/**
 * 透析视图
 *
 * @author CH
 */
public class PivotTableHandler implements WorkbookWriteHandler {
    /**
     * 数据区域
     */
    private final String area;

    public PivotTableHandler(String area) {
        this.area = area;
    }

    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        WorkbookWriteHandler.super.afterWorkbookDispose(writeWorkbookHolder);

        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);

        int first = 0, end = 0;
        XSSFSheet xssfSheet = null;
        AreaReference areaReference = null;
        List<List<String>> head = writeWorkbookHolder.getHead();
        if (sheet instanceof XSSFSheet) {
            xssfSheet = (XSSFSheet) sheet;
            areaReference = new AreaReference(area, null);
        } else if (sheet instanceof SXSSFSheet) {
            SXSSFSheet sxssfSheet = (SXSSFSheet) sheet;
            xssfSheet = Reflect.create(sxssfSheet).getFieldValue("_sh", XSSFSheet.class);
            PoiUtils.copyRow(sxssfSheet, xssfSheet);
            areaReference = new AreaReference(area, null);
        }

        if (null == xssfSheet) {
            return;
        }

        //数据透视表生成为位置
        CellReference cellReference = new CellReference(1, head.size() + 2);
        xssfSheet.createPivotTable(areaReference, cellReference, xssfSheet);
    }

}
