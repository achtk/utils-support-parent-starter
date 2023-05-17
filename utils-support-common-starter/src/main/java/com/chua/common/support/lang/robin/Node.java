package com.chua.common.support.lang.robin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * node
 *
 * @author CH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node<T> implements Comparable<Node<T>> {

    private T content;
    private Integer weight;
    private Integer effectiveWeight;
    private Integer currentWeight;

    public Node(T content) {
        this.content = content;
    }

    @Override
    public int compareTo(Node node) {
        return currentWeight > node.currentWeight ? 1 : (currentWeight.equals(node.currentWeight) ? 0 : -1);
    }
}
