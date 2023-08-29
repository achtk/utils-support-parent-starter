package com.chua.common.support.range.order;

import com.chua.common.support.utils.Preconditions;

import java.io.Serializable;
import java.util.Iterator;

/**
 * 自然倒敘
 * @author CH
 */
final class ReverseNaturalOrdering extends Ordering<Comparable<?>> implements Serializable {
    static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();

    @Override
    public int compare(Comparable<?> left, Comparable<?> right) {
        Preconditions.checkNotNull(left);
        if (left == right) {
            return 0;
        }

        return ((Comparable<Object>) right).compareTo(left);
    }

    @Override
    public <S extends Comparable<?>> Ordering<S> reverse() {
        return Ordering.natural();
    }

    // Override the min/max methods to "hoist" delegation outside loops

    @Override
    public <E extends Comparable<?>> E min(E a, E b) {
        return NaturalOrdering.INSTANCE.max(a, b);
    }

    @Override
    public <E extends Comparable<?>> E min(E a, E b, E c, E... rest) {
        return NaturalOrdering.INSTANCE.max(a, b, c, rest);
    }

    @Override
    public <E extends Comparable<?>> E min(Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.max(iterator);
    }

    @Override
    public <E extends Comparable<?>> E min(Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.max(iterable);
    }

    @Override
    public <E extends Comparable<?>> E max(E a, E b) {
        return NaturalOrdering.INSTANCE.min(a, b);
    }

    @Override
    public <E extends Comparable<?>> E max(E a, E b, E c, E... rest) {
        return NaturalOrdering.INSTANCE.min(a, b, c, rest);
    }

    @Override
    public <E extends Comparable<?>> E max(Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.min(iterator);
    }

    @Override
    public <E extends Comparable<?>> E max(Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.min(iterable);
    }

    /**
     * preserving singleton-ness gives equals()/hashCode() for free
     */
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Ordering.natural().reverse()";
    }

    private ReverseNaturalOrdering() {}

    private static final long serialVersionUID = 0;
}
