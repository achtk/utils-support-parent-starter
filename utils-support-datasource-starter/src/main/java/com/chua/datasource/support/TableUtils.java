package com.chua.datasource.support;

import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.MapUtils;
import com.chua.datasource.support.adator.CalciteTable;
import com.chua.datasource.support.adator.TableAdaptor;
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * sql name
 *
 * @author CH
 * @since 2021-11-10
 */
public class TableUtils {

    /**
     * 创建字段
     *
     * @param mapping 映射
     * @param column  字段
     * @return 字段
     */
    public static Map<String, SqlTypeName> createColumn(Map<String, String> mapping, Object column) {
        Map<String, SqlTypeName> rs = new LinkedHashMap<>();
        if (column instanceof Map) {
            ((Map<?, ?>) column).forEach((k, v) -> {
                SqlTypeName sqlTypeName = null;
                if (v instanceof Class) {
                    sqlTypeName = SqlNameUtils.get((Class<?>) v);
                } else if (v instanceof String) {
                    try {
                        sqlTypeName = SqlTypeName.get(((String) v));
                    } catch (Exception ignored) {
                    }

                    if (null == sqlTypeName) {
                        sqlTypeName = SqlNameUtils.get((String) v);
                    }
                }


                if (null == sqlTypeName) {
                    sqlTypeName = SqlTypeName.VARCHAR;
                }

                rs.put(mapping.getOrDefault(k.toString(), k.toString()), sqlTypeName);
            });
        }

        return rs;
    }

    /**
     * 创建表
     *
     * @param directory 目录/文件
     * @param name      实现
     * @return 表
     */
    public static List<CalciteTable> createTable(String directory, String name) {
        TableAdaptor tableAdaptor = ServiceProvider.of(TableAdaptor.class).getExtension(name);
        if (null == tableAdaptor) {
            return Collections.emptyList();
        }

        return tableAdaptor.createTable(directory);
    }

    /**
     * 映射
     *
     * @param mapping 映射
     * @param s       参数
     * @return 结果
     */
    public static String mapping(Map mapping, String s) {
        return MapUtils.getString(mapping, s, s);
    }
}
