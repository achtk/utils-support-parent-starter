package com.chua.poi.support.table;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.chua.common.support.utils.MapUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * excel
 *
 * @author CH
 */
public class HeaderReadListener implements ReadListener<Map<String, Object>> {

    private Map mapping;
    private final List<String> columns = new LinkedList<>();
    private Map<Integer, ReadCellData<?>> headMap;

    public HeaderReadListener(Map mapping) {
        this.mapping = mapping;
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap = headMap;
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            String stringValue = entry.getValue().getStringValue();
            columns.add(MapUtils.getString(mapping, stringValue, stringValue));
        }
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return false;
    }

    @Override
    public void invoke(Map<String, Object> data, AnalysisContext context) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
