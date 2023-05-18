package com.chua.poi.support.excel.listener;

/**
 * 监听
 *
 * @author CH
 */
public interface ExcelHandlerListener {
    /**
     * 是否需要设置下拉数据
     *
     * @param fieldName 字段
     * @return 是否需要设置下拉数据
     */
    boolean isSelect(String fieldName);

    /**
     * 获取下拉数据
     *
     * @param fieldName   字段
     * @param rowIndex    行号
     * @param columnIndex 列号
     * @return 下拉数据
     */
    String[] getSelectValue(String fieldName, int rowIndex, int columnIndex);
}
