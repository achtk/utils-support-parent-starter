package com.chua.common.support.database.subtable;

import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.range.Range;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段测率，根据指定字段组装数据表
 * <p>
 * 假设设置字段未sys_time, 对应数据为 2022-10-01 则db_20221001或者table_20221001
 * </p>
 *
 * @author CH
 */
public class ColumnSubTableStrategy implements SubTableStrategy {

    private final String column;
    private final Strategy strategy;

    public ColumnSubTableStrategy(String column, Strategy strategy) {
        this.column = column;
        this.strategy = strategy;
    }

    @Override
    public Strategy getStrategy() {
        return strategy;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public String doSharding(Collection<String> collection, String logicTableName, String value) {
        String tbName = logicTableName + "_";
        try {
            Date date = DateUtils.parseDate(value);
            String year = String.format("%tY", date);
            String mon = String.format("%tm", date);
            String dat = String.format("%td", date);
            tbName = tbName + year + mon + dat;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return tbName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, String logicTableName, Range<String> value) {
        String lowerEndpoint = value.lowerEndpoint();
        String endpoint = value.upperEndpoint();
        List<Date> dates = DateTime.of(lowerEndpoint).betweenDate(DateTime.of(endpoint));
        return dates.stream().map(DateTime::of).map(it -> logicTableName + "_" + it.toString("yyyyMMdd")).collect(Collectors.toList());
    }
}
