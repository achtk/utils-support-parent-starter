package com.chua.common.support.database.structure;

import java.util.List;

/**
 * 表结构
 *
 * @author CH
 */
public interface TableStructure {
    /**
     * 分析结构
     *
     * @param schema         数据库
     * @param structureValue 结构数据
     * @return 结构
     */
    List<Structure> analysis(String schema, StructureValue structureValue);
}
