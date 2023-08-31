package com.chua.common.support.json.jsonpath.internal.filter;

import com.chua.common.support.json.jsonpath.internal.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 */
public class LogicalExpressionNode extends AbstractExpressionNode {
    protected List<AbstractExpressionNode> chain = new ArrayList<AbstractExpressionNode>();
    private final LogicalOperator operator;

    public static AbstractExpressionNode createLogicalNot(AbstractExpressionNode op) {
        return new LogicalExpressionNode(op, LogicalOperator.NOT, null);
    }

    public static LogicalExpressionNode createLogicalOr(AbstractExpressionNode left, AbstractExpressionNode right) {
        return new LogicalExpressionNode(left, LogicalOperator.OR, right);
    }

    public static LogicalExpressionNode createLogicalOr(Collection<AbstractExpressionNode> operands) {
        return new LogicalExpressionNode(LogicalOperator.OR, operands);
    }

    public static LogicalExpressionNode createLogicalAnd(AbstractExpressionNode left, AbstractExpressionNode right) {
        return new LogicalExpressionNode(left, LogicalOperator.AND, right);
    }

    public static LogicalExpressionNode createLogicalAnd(Collection<AbstractExpressionNode> operands) {
        return new LogicalExpressionNode(LogicalOperator.AND, operands);
    }

    private LogicalExpressionNode(AbstractExpressionNode left, LogicalOperator operator, AbstractExpressionNode right) {
        chain.add(left);
        chain.add(right);
        this.operator = operator;
    }

    private LogicalExpressionNode(LogicalOperator operator, Collection<AbstractExpressionNode> operands) {
        chain.addAll(operands);
        this.operator = operator;
    }

    public LogicalExpressionNode and(LogicalExpressionNode other) {
        return createLogicalAnd(this, other);
    }

    public LogicalExpressionNode or(LogicalExpressionNode other) {
        return createLogicalOr(this, other);
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public LogicalExpressionNode append(AbstractExpressionNode expressionNode) {
        chain.add(0, expressionNode);
        return this;
    }

    @Override
    public String toString() {
        return "(" + Utils.join(" " + operator.getOperatorString() + " ", chain) + ")";
    }

    @Override
    public boolean apply(PredicateContext ctx) {
        if (operator == LogicalOperator.OR) {
            for (AbstractExpressionNode expression : chain) {
                if (expression.apply(ctx)) {
                    return true;
                }
            }
            return false;
        } else if (operator == LogicalOperator.AND) {
            for (AbstractExpressionNode expression : chain) {
                if (!expression.apply(ctx)) {
                    return false;
                }
            }
            return true;
        } else {
            AbstractExpressionNode expression = chain.get(0);
            return !expression.apply(ctx);
        }
    }

}
