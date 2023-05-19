package com.chua.poi.support.excel.file.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.AbstractExportFile;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.reflection.describe.TypeAttribute;
import com.chua.poi.support.excel.handler.ColFilterHandler;
import com.chua.poi.support.excel.handler.EasyExcelCustomCellWriteHandler;
import com.chua.poi.support.excel.handler.FilterHandler;
import com.chua.poi.support.excel.handler.FreezeHandler;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 导出
 *
 * @author CH
 */
@Spi("xls")
public class XlsExportFile extends AbstractExportFile {

    public static final String[] DEFAULT_ANNOTATION = new String[]{
            ExcelProperty.class.getTypeName(),
            ExcelIgnoreUnannotated.class.getTypeName(),
            ExcelIgnore.class.getTypeName(),
            DateTimeFormat.class.getTypeName(),
            NumberFormat.class.getTypeName(),
            ColumnWidth.class.getTypeName(),
            ContentFontStyle.class.getTypeName(),
            ContentLoopMerge.class.getTypeName(),
            ContentRowHeight.class.getTypeName(),
            ContentStyle.class.getTypeName(),
            HeadFontStyle.class.getTypeName(),
            HeadRowHeight.class.getTypeName(),
            HeadStyle.class.getTypeName(),
            OnceAbsoluteMerge.class.getTypeName(),
    };

    public XlsExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLS;
    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        Class<?> aClass = data.get(0).getClass();
        TypeAttribute typeAttribute = TypeAttribute.create(aClass);
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.NO_FILL);
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);


        if (typeAttribute.hasAnyAnnotation(DEFAULT_ANNOTATION)) {
            EasyExcel.write(outputStream, aClass)
                    .registerWriteHandler(horizontalCellStyleStrategy)
                    .registerWriteHandler(new FreezeHandler())
                    .registerWriteHandler(new FilterHandler("A1:Z1"))
                    .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))
                    .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 30, (short) 20))
                    .autoCloseStream(true)
                    .autoTrim(true)
                    .excelType(getExcelTypeEnum())
                    .sheet()
                    .doWrite(data);
            return;
        }


        int size = 0;
        List<List<Object>> rs = new LinkedList<>();
        for (T datum : data) {
            Object[] array = createArray(datum, true);
            size = array.length;
            rs.add(Arrays.asList(array));
        }

        List<List<String>> tpl = new LinkedList<>();
        for (String header : headers) {
            tpl.add(Collections.singletonList(header));
        }

        EasyExcel.write(outputStream)
                .head(tpl)
                .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                .registerWriteHandler(new FreezeHandler())
                .registerWriteHandler(new ColFilterHandler(0, 0, 0, size - 1))
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 30, (short) 20))
                .autoCloseStream(true)
                .autoTrim(true)
                .excelType(getExcelTypeEnum())
                .sheet()
                .doWrite(rs);

    }
}
