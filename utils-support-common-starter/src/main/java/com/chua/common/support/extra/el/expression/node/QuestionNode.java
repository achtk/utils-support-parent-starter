package com.chua.common.support.extra.el.expression.node;
/**
 * 基础类
 *
 * @author CH
 */
public interface QuestionNode extends CalculateNode {
    /**
     * setConditionNode
     *
     * @param node node
     */
    void setConditionNode(CalculateNode node);

    /**
     * setLeftNode
     *
     * @param node node
     */
    void setLeftNode(CalculateNode node);

    /**
     * setRightNode
     *
     * @param node node
     */
    void setRightNode(CalculateNode node);
}
