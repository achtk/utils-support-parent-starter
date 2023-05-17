package com.chua.common.support.lang.robin;

import com.chua.common.support.annotations.Spi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round
 *
 * @author CH
 */
@Spi({"round", "polling"})
public class RoundRobin<T> implements Robin<T> {
    final AtomicInteger count = new AtomicInteger(0);
    private final List<Node<T>> nodes = new ArrayList<>();

    @Override
    public Node<T> selectNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        int andIncrement = count.getAndIncrement();
        return nodes.get(andIncrement % nodes.size());
    }

    @Override
    public Robin<T> create() {
        return new RoundRobin<>();
    }

    @Override
    public synchronized Robin<T> clear() {
        nodes.clear();
        return this;
    }

    @Override
    public Robin<T> addNode(Node<T> node) {
        this.nodes.add(node);
        return this;
    }
}
