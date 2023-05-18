package com.chua.datasource.support.file;

import com.chua.common.support.lang.profile.Profile;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;

import java.util.List;
import java.util.Map;

/**
 * 文件适配器
 *
 * @author CH
 */
public interface FileSupport {

    /**
     * 表字段
     *
     * @param typeFactory 表字段
     * @return 表字段
     */
    RelDataType getRowType(RelDataTypeFactory typeFactory);

    /**
     * Executes a "find" operation on the underlying collection.
     *
     * <p>For example,
     * <code>zipsTable.find("{state: 'OR'}", "{city: 1, zipcode: 1}")</code></p>
     *
     * @param attributes  Table connection
     * @param filterJson  Filter JSON string, or null
     * @param projectJson Project JSON string, or null
     * @param fields      List of fields to project; or null to return map
     * @return Enumerator of results
     */
    Enumerable<Object> find(Profile attributes, String filterJson, String projectJson, List<Map.Entry<String, Class>> fields);

    /**
     * Executes an "aggregate" operation on the underlying collection.
     *
     * <p>For example:
     * <code>zipsTable.aggregate(
     * "{$filter: {state: 'OR'}",
     * "{$group: {_id: '$city', c: {$sum: 1}, p: {$sum: '$pop'}}}")
     * </code></p>
     *
     * @param configureAttributes Table connection
     * @param fields              List of fields to project; or null to return map
     * @param operations          One or more JSON strings
     * @return Enumerator of results
     */
    Enumerable aggregate(final Profile configureAttributes,
                         final List<Map.Entry<String, Class>> fields,
                         final List<String> operations);

}
