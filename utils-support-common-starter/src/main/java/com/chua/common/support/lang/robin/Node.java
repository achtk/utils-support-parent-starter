package com.chua.common.support.lang.robin;

import com.chua.common.support.converter.Converter;
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
public class Node implements Comparable<Node> {

    private Object content;
    private Integer weight;
    private Integer effectiveWeight;
    private Integer currentWeight;

    public Node(Object content) {
        this.content = content;
    }

    /**
     * 获取数据
     * @param targetType 目标类型
     * @return 结果
     * @param <T> 类型
     */
    public <T>T getValue(Class<T> targetType) {
        return Converter.convertIfNecessary(content, targetType);
    }

    @Override
    public int compareTo(Node node) {
        return currentWeight > node.currentWeight ? 1 : (currentWeight.equals(node.currentWeight) ? 0 : -1);
    }
}
