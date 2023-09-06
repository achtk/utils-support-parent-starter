package com.chua.agent.support.plugin.apm;

import com.chua.agent.support.span.Span;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 树
 * 顶层PID必须为 0
 *
 * @author CH
 */
@Data
public class CustomTreeNode {

    private List<Span> data = new LinkedList<>();

    /**
     * 添加数据
     * @param constructs 数据
     */
    public void add(List<Span> constructs) {
        data.addAll(constructs);
    }

    /**
     * 添加数据
     * @param constructs 数据
     */
    public void add(Span... constructs) {
        data.addAll(Arrays.asList(constructs));
    }

    /**
     * 转化
     * @return T
     */
    public List<Span> transferAll() {
        Map<String, List<Span>> tpl = new LinkedHashMap<>();
        for (Span datum : data) {
            tpl.computeIfAbsent(datum.getPid(), it -> new LinkedList<>()).add(datum);
        }

        for (Span datum : data) {
            List<Span> ts = tpl.get(datum.getId());
            if(null == ts) {
                continue;
            }
            doChildren(datum, ts);
        }

        return data.stream().filter(it -> it.getId().equals(it.getLinkId())).collect(Collectors.toList());
    }

    private void doChildren(Span datum, List<Span> ts) {
        datum.setChildren(ts);
    }

}
