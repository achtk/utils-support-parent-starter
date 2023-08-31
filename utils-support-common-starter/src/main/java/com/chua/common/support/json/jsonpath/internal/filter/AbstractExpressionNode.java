package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.jsonpath.Predicate;

/**
 * @author Administrator
 */
public abstract class AbstractExpressionNode implements Predicate {

    public static AbstractExpressionNode createExpressionNode(AbstractExpressionNode right, LogicalOperator operator, AbstractExpressionNode left) {
        if (operator == LogicalOperator.AND) {
            if ((right instanceof LogicalExpressionNode) && ((LogicalExpressionNode) right).getOperator() == LogicalOperator.AND) {
                LogicalExpressionNode len = (LogicalExpressionNode) right;
                return len.append(left);
            } else {
                return LogicalExpressionNode.createLogicalAnd(left, right);
            }
        } else {
            if ((right instanceof LogicalExpressionNode) && ((LogicalExpressionNode) right).getOperator() == LogicalOperator.OR) {
                LogicalExpressionNode len = (LogicalExpressionNode) right;
                return len.append(left);
            } else {
                return LogicalExpressionNode.createLogicalOr(left, right);
            }
        }
    }
}
