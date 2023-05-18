package com.chua.poi.support.excel.imports;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.imports.AbstractImportFile;
import com.chua.common.support.file.imports.ImportListener;
import com.chua.common.support.json.JsonArray;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导入
 *
 * @author CH
 */
@Spi("xls")
public class XlsImportFile extends AbstractImportFile {

    public XlsImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLS;
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        Class<?> aClass = type;
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
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


        EasyExcel.read(inputStream, new XlsReadListener(type, skip, listener))
                .charset(Charset.forName(configuration.charset()))
                .autoCloseStream(true)
                .excelType(getExcelTypeEnum())
                .sheet(configuration.sheetNo())
                .autoTrim(true)
                .headRowNumber(1)
                .doRead();
    }


    @SuppressWarnings("ALL")
    final class XlsReadListener<T> implements ReadListener {

        private final int skip;
        private AtomicLong atomicLong = new AtomicLong(0);
        private final Class<T> type;
        private final ImportListener<T> listener;
        private Map headMap;
        private final List<String> column = new LinkedList<>();
        private final JsonArray jsonArray = new JsonArray(column);

        public XlsReadListener(Class<T> type, int skip, ImportListener<T> listener) {
            this.type = type;
            this.listener = listener;
            this.skip = skip - 1;
        }


        @Override
        public void invokeHead(Map headMap, AnalysisContext context) {
            this.headMap = headMap;
            headMap.forEach((k, v) -> {
                jsonArray.add((Integer) k, ((CellData) v).getStringValue());
            });
            ReadListener.super.invokeHead(headMap, context);
        }

        @Override
        public void invoke(Object data, AnalysisContext context) {
            if (skip > 0 && atomicLong.get() < skip) {
                return;
            }
            listener.accept(doAnalysis(jsonArray, type, new JsonArray(((Map) data).values())));
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

        }

        @Override
        public boolean hasNext(AnalysisContext context) {
            return !listener.isEnd((int) atomicLong.getAndIncrement() + 1);
        }
    }

}
