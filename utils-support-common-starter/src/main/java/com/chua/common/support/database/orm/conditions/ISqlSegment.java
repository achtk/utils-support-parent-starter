
package com.chua.common.support.database.orm.conditions;


import java.io.Serializable;

/**
 * SQL 片段接口
 *
 * @author hubin miemie HCL
 * @since 2018-05-28
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * SQL 片段
     * @return SQL 片段
     */
    String getSqlSegment();

}
