package com.chua.common.support.database.inquirer;

import java.io.Serializable;
import java.util.List;

/**
 * 实体查询
 *
 * @author CH
 */
public interface SqlSubstanceInquirer<T> extends Inquirer {
    /**
     * 更新实体
     *
     * @param entity 实体
     * @return 结果
     */
    T updateById(T entity);

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return 结果
     */
    T save(T entity);

    /**
     * id查询实体
     *
     * @param id id
     * @return 结果
     */
    T queryById(Serializable id);

    /**
     * 查询实体
     *
     * @param entity 实体
     * @return 结果
     */
    List<T> query(T entity);
}
