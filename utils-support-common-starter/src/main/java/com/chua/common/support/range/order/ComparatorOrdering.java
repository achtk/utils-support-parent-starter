package com.chua.common.support.range.order;


import com.chua.common.support.utils.Preconditions;

import java.io.Serializable;
import java.util.Comparator;

/**
 * ComparatorOrdering
 * @author CH
 */
final class ComparatorOrdering<T extends Object> extends BaseOrdering<T> implements Serializable {
    final Comparator<T> comparator;

    ComparatorOrdering(Comparator<T> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }

    @Override
    public int compare(T a, T b) {
        return comparator.compare(a, b);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComparatorOrdering) {
            ComparatorOrdering<?> that = (ComparatorOrdering<?>) object;
            return this.comparator.equals(that.comparator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return comparator.hashCode();
    }

    @Override
    public String toString() {
        return comparator.toString();
    }

    private static final long serialVersionUID = 0;
}
