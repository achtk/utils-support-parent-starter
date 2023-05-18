package com.chua.common.support.extra.el.expression.node;

public interface QuestionNode extends CalculateNode {
    void setConditionNode(CalculateNode node);

    void setLeftNode(CalculateNode node);

    void setRightNode(CalculateNode node);
}
