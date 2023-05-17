package com.chua.common.support.lang.robin;

import com.chua.common.support.annotations.Spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * round
 *
 * @author CH
 */
@Spi("random")
public class RandomRoundRobin<T> implements Robin<T> {
    private final List<Node<T>> nodes = new ArrayList<>();

    @Override
    public Node<T> selectNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        Collections.shuffle(nodes);
        return nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
    }

    @Override
    public Robin<T> create() {
        return new RandomRoundRobin<>();
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
