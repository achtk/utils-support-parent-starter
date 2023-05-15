package com.chua.common.support.range.order;

import com.chua.common.support.utils.Preconditions;

import java.io.Serializable;

/**
 * 自然排序
 * @author CH
 */
final class NaturalOrdering extends Ordering<Comparable<?>> implements Serializable {
    static final NaturalOrdering INSTANCE = new NaturalOrdering();

    private transient Ordering<Comparable<?>> nullsFirst;
    private transient Ordering<Comparable<?>> nullsLast;

    @Override
    public int compare(Comparable<?> left, Comparable<?> right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        return ((Comparable<Object>) left).compareTo(right);
    }


    @Override
    public <S extends Comparable<?>> Ordering<S> reverse() {
        return (Ordering<S>) ReverseNaturalOrdering.INSTANCE;
    }

    /**
     * preserving singleton-ness gives equals()/hashCode() for free
     */
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Ordering.natural()";
    }

    private NaturalOrdering() {}

    private static final long serialVersionUID = 0;
}
