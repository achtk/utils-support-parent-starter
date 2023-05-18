package com.chua.poi.support.table;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * excel
 *
 * @author CH
 */
public class DataReadListener implements ReadListener<Map<String, Object>> {

    private Map mapping;
    private final List<Object[]> data = new LinkedList<>();


    public List<Object[]> getData() {
        return data;
    }

    @Override
    public void invoke(Map<String, Object> data, AnalysisContext context) {
        this.data.add(data.values().toArray());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
