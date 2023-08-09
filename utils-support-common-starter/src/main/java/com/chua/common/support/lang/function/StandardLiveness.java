package com.chua.common.support.lang.function;

/**
 * 活体
 *
 * @author CH
 */
public class StandardLiveness implements Liveness {

    private final int liveness;

    public StandardLiveness(int liveness) {
        this.liveness = liveness;
    }

    @Override
    public int live() {
        return liveness;
    }
}
