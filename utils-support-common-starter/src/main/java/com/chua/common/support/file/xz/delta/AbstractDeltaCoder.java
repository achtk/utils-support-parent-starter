package com.chua.common.support.file.xz.delta;

abstract class AbstractDeltaCoder {
    static final int DISTANCE_MIN = 1;
    static final int DISTANCE_MAX = 256;
    static final int DISTANCE_MASK = DISTANCE_MAX - 1;

    final int distance;
    final byte[] history = new byte[DISTANCE_MAX];
    int pos = 0;

    AbstractDeltaCoder(int distance) {
        if (distance < DISTANCE_MIN || distance > DISTANCE_MAX) {
            throw new IllegalArgumentException();
        }

        this.distance = distance;
    }
}
