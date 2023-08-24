package com.chua.common.support.lang.robin;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round
 *
 * @author CH
 */
@SpiDefault
@Spi({"round", "polling"})
public class RoundRobin implements Robin {
    final AtomicInteger count = new AtomicInteger(0);
    private final List<Node> nodes = new ArrayList<>();

    @Override
    public Node selectNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        int andIncrement = count.getAndIncrement();
        return nodes.get(andIncrement % nodes.size());
    }

    @Override
    public Robin create() {
        return new RoundRobin();
    }

    @Override
    public synchronized Robin clear() {
        nodes.clear();
        return this;
    }

    @Override
    public Robin addNode(Node node) {
        this.nodes.add(node);
        return this;
    }
}
