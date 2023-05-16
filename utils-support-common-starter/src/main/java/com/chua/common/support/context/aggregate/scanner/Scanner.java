package com.chua.common.support.context.aggregate.scanner;

import com.chua.common.support.context.aggregate.Aggregate;

/**
 * 扫描器
 * @author CH
 */
public interface Scanner {
    /**
     * 扫描
     * @param aggregate 聚合体
     */
    void scan(Aggregate aggregate);
}
