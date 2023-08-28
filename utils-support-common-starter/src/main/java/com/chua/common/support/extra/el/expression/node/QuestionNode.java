package com.chua.common.support.extra.el.expression.node;
/**
 * 基础类
 * @author CH
 */
public interface QuestionNode extends CalculateNode {
    void setConditionNode(CalculateNode node);

    void setLeftNode(CalculateNode node);

    void setRightNode(CalculateNode node);
}
