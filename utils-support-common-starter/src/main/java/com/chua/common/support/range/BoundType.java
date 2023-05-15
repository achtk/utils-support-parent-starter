package com.chua.common.support.range;

/**
 * BoundType
 * @author CH
 */
public enum BoundType {
    /**
     * open
     */
    OPEN(false),
    /**
     * close
     */
    CLOSED(true);

    final boolean inclusive;

    BoundType(boolean inclusive) {
        this.inclusive = inclusive;
    }

    /** Returns the bound type corresponding to a boolean value for inclusivity. */
    static BoundType forBoolean(boolean inclusive) {
        return inclusive ? CLOSED : OPEN;
    }
}
