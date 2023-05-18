package com.chua.common.support.database;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.reflection.FieldStation;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * ResultSet工具类
 *
 * @author CH
 */
public class ResultSetUtils {
    private static final String SHARDING = "org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset.ShardingResultSet";

    /**
     * 逐行遍历
     * @param rs 结果集
     * @param consumer 消费者
     */
    public static void doLine(ResultSet rs, SafeConsumer<ResultSet> consumer) throws SQLException {
        if(null == rs) {
            return;
        }

        while (rs.next()) {
            consumer.accept(rs);
        }

    }

    /**
     * 处理结果
     *
     * @param rs       结果
     * @param beanType 类型
     * @param <T>      类型
     * @return 结果
     */
    public static <T> T handleOne(ResultSet rs, Class<T> beanType) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        if (!rs.next()) {
            return null;
        }

        Map<String, Object> item = new HashMap<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            item.put(metaData.getColumnName(i), rs.getObject(i));
        }
        return BeanUtils.copyProperties(item, beanType);
    }

    /**
     * 处理结果
     *
     * @param rs       结果
     * @param beanType 类型
     * @param <T>      类型
     * @return 结果
     */
    @SuppressWarnings("ALL")
    public static <T> List<T> handle(ResultSet rs, Class<T> beanType) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        boolean isSharding = rs.getClass().getTypeName().equals(SHARDING);
        Map columns = new LinkedHashMap();
        if (isSharding) {
            Map columnLabelAndIndexMap = (Map) FieldStation.of(rs).getFieldValue("columnLabelAndIndexMap");
            columnLabelAndIndexMap.forEach((k, v) -> {
                columns.put(v, k);
            });
        }
        if (columnCount == 0 && isSharding) {
            columnCount = columns.size();
        }
        List<T> result = new LinkedList<>();
        if (!rs.next()) {
            return Collections.emptyList();
        }

        do {
            Map<String, Object> item = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                item.put(isSharding ? MapUtils.getString(columns, i) : StringUtils.defaultString(metaData.getColumnLabel(i), metaData.getColumnName(i)), rs.getObject(i));
            }
            T property = BeanUtils.copyProperties(item, beanType);
            result.add(property);
        } while (rs.next());

        return result;
    }
}
