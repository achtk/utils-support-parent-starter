package com.chua.common.support.json.jsonpath.internal.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */
public class RelationalExpressionNode extends AbstractExpressionNode {

    private static final Logger logger = LoggerFactory.getLogger(RelationalExpressionNode.class);

    private final BaseValueNode left;
    private final RelationalOperator relationalOperator;
    private final BaseValueNode right;

    public RelationalExpressionNode(BaseValueNode left, RelationalOperator relationalOperator, BaseValueNode right) {
        this.left = left;
        this.relationalOperator = relationalOperator;
        this.right = right;

        logger.trace("ExpressionNode {}", toString());
    }

    @Override
    public String toString() {
        if (relationalOperator == RelationalOperator.EXISTS) {
            return left.toString();
        } else {
            return left.toString() + " " + relationalOperator.toString() + " " + right.toString();
        }
    }

    @Override
    public boolean apply(PredicateContext ctx) {
        BaseValueNode l = left;
        BaseValueNode r = right;

        if (left.isPathNode()) {
            l = left.asPathNode().evaluate(ctx);
        }
        if (right.isPathNode()) {
            r = right.asPathNode().evaluate(ctx);
        }
        Evaluator evaluator = EvaluatorFactory.createEvaluator(relationalOperator);
        if (evaluator != null) {
            return evaluator.evaluate(l, r, ctx);
        }
        return false;
    }
}