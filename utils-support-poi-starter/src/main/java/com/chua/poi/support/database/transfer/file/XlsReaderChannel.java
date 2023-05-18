package com.chua.poi.support.database.transfer.file;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.database.transfer.datasource.DataSourceReaderChannel;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.value.MapValue;
import com.chua.common.support.value.Pair;
import com.chua.poi.support.excel.handler.ColFilterHandler;
import com.chua.poi.support.excel.handler.EasyExcelCustomCellWriteHandler;
import com.chua.poi.support.excel.handler.FreezeHandler;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * xls
 */
@Spi("xls")
public class XlsReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {


    List<List<String>> tpl = new LinkedList<>();
    private Pair[] pairs;
    private String[] headers;
    private HorizontalCellStyleStrategy horizontalCellStyleStrategy;
    private ExcelWriterSheetBuilder writerSheetBuilder;

    public XlsReaderChannel(Object obj) {
        super(obj);
    }

    public XlsReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
    }

    @Override
    public void destroy() {
    }

    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLS;
    }

    @Override
    public void afterPropertiesSet() {
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
        this.horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        this.pairs = configuration.header();
        if(null == pairs) {
            pairs = dataMapping.getValuePair();
        }

        this.headers = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair pair = pairs[i];
            headers[i] = pair.getLabel();
        }
        for (String header : headers) {
            tpl.add(Collections.singletonList(header));
        }

        this.writerSheetBuilder = EasyExcel.write(outputStream)
                .head(tpl)
                .registerWriteHandler(new EasyExcelCustomCellWriteHandler())
                .registerWriteHandler(new FreezeHandler())
                .registerWriteHandler(new ColFilterHandler(0, 0, 0, tpl.size() - 1))
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(20))
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 30, (short) 20))
                .autoCloseStream(true)
                .autoTrim(true)
                .excelType(getExcelTypeEnum())
                .sheet();
    }

    @Override
    public void read(SinkTable sinkTable) {
        List<Object> rs = new LinkedList<>();
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                Object[] array = DataSourceReaderChannel.createArgs(dataMapping, null, mapValue);
                rs.add(Arrays.asList(array));
            });

            writerSheetBuilder.doWrite(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
