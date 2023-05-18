package com.chua.poi.support.database.transfer.file;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.value.Pair;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * xls
 */
@Spi("xls")
public class XlsWriterChannel extends AbstractWriterChannel implements InitializingAware, DisposableAware {


    private ExcelReaderBuilder readerBuilder;

    private final Queue<Map<String, Object>>  queue = new LinkedBlockingQueue<>();

    public XlsWriterChannel(Object input) {
        super(input);
    }

    public XlsWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        super(configuration, inputStream);
    }

    @Override
    public void destroy() {
    }

    protected ExcelTypeEnum getExcelTypeEnum() {
        return ExcelTypeEnum.XLS;
    }

    @Override
    public void afterPropertiesSet() {
        this.readerBuilder = EasyExcel.read(inputStream, new CustomAnalysisEventListener())
                .excelType(getExcelTypeEnum())
                .headRowNumber(configuration.skip())
                .charset(StandardCharsets.UTF_8);

        readerBuilder.sheet(configuration.sheetNo()).doRead();
    }

    @Override
    public boolean isFinish() {
        return queue.isEmpty() && status.get();
    }

    @Override
    public SinkTable createSinkTable() {
        Map<String, Object> poll;
        List<Map<String, Object>> tpl = new LinkedList<>();
        while ((poll = queue.poll()) != null) {
            tpl.add(poll);
        }

        if(tpl.isEmpty()) {
            return null;
        }
        return new SinkTable(dataMapping, tpl);
    }


    final class CustomAnalysisEventListener extends AnalysisEventListener<Map<String, Object>> {

        private Map<Integer, String> headMap = new LinkedHashMap<>();

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            super.invokeHeadMap(headMap, context);
            for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
                String value = entry.getValue();
                Pair pair = dataMapping.getPair(value);
                if(null == pair) {
                    continue;
                }
                this.headMap.put(entry.getKey(), pair.getLabel());
            }
        }

        @Override
        public void invoke(Map<String, Object> data, AnalysisContext context) {
            if(headMap.isEmpty()) {
                return;
            }
            Map<String, Object> item = new HashMap<>(headMap.size());
            for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
                item.put(entry.getValue(), data.get(entry.getKey()));
            }

            queue.add(item);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            status.set(true);
        }

    }
}
