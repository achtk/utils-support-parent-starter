package com.chua.common.support.range.order;

import com.chua.common.support.utils.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * CompoundOrdering
 * @author CH
 */
final class CompoundOrdering<T extends Object> extends BaseOrdering<T> implements Serializable {
    final Comparator<? super T>[] comparators;

    CompoundOrdering(Comparator<? super T> primary, Comparator<? super T> secondary) {
        this.comparators = (Comparator<? super T>[]) new Comparator[] {primary, secondary};
    }

    CompoundOrdering(Iterable<? extends Comparator<? super T>> comparators) {
        this.comparators = ArrayUtils.toArray(comparators, new Comparator[0]);
    }

    @Override
    public int compare(T left, T right) {
        for (int i = 0; i < comparators.length; i++) {
            int result = comparators[i].compare(left, right);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CompoundOrdering) {
            CompoundOrdering<?> that = (CompoundOrdering<?>) object;
            return Arrays.equals(this.comparators, that.comparators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(comparators);
    }

    @Override
    public String toString() {
        return "Ordering.compound(" + Arrays.toString(comparators) + ")";
    }

    private static final long serialVersionUID = 0;
}
