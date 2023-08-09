package com.chua.common.support.engine;

import java.util.List;

/**
 * 引擎
 *
 * @author CH
 */
public interface QueryEngine<T> extends SearchEngine<T> {

    /**
     * 查询数据
     *
     * @param sl   查询语句
     * @param args 参数
     * @return 结果
     */
    List<T> query(String sl, Object... args);

}
