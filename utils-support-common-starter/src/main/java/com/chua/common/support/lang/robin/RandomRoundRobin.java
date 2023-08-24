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
public class RandomRoundRobin implements Robin {
    private final List<Node> nodes = new ArrayList<>();

    @Override
    public Node selectNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        Collections.shuffle(nodes);
        return nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
    }

    @Override
    public Robin create() {
        return new RandomRoundRobin();
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
