package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.jsonpath.Predicate;

/**
 * @author Administrator
 */
public interface Evaluator {
    /**
     * 计算树
     * @param left left node
     * @param right right node
     * @param ctx context
     * @return true | false
     */
    boolean evaluate(BaseValueNode left, BaseValueNode right, Predicate.PredicateContext ctx);
}