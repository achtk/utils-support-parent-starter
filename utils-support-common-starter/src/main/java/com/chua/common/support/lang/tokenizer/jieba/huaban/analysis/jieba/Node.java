package com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba;

/**
 * @author Administrator
 */
public class Node {
    public Character value;
    public Node parent;

    public Node(Character value, Node parent) {
        this.value = value;
        this.parent = parent;
    }
}
