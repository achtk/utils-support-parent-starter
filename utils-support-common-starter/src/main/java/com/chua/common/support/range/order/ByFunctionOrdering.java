package com.chua.common.support.range.order;

import com.chua.common.support.utils.ObjectUtils;

import java.io.Serializable;
import java.util.function.Function;

import static com.chua.common.support.utils.Preconditions.checkNotNull;

/**
 * ByFunctionOrdering
 * @author CH
 */
final class ByFunctionOrdering<F extends Object, T extends Object> extends BaseOrdering<F> implements Serializable {
    final Function<F, ? extends T> function;
    final BaseOrdering<T> ordering;

    ByFunctionOrdering(Function<F, ? extends T> function, BaseOrdering<T> ordering) {
        this.function = checkNotNull(function);
        this.ordering = checkNotNull(ordering);
    }

    @Override
    public int compare(F left, F right) {
        return ordering.compare(function.apply(left), function.apply(right));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ByFunctionOrdering) {
            ByFunctionOrdering<?, ?> that = (ByFunctionOrdering<?, ?>) object;
            return this.function.equals(that.function) && this.ordering.equals(that.ordering);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(function, ordering);
    }

    @Override
    public String toString() {
        return ordering + ".onResultOf(" + function + ")";
    }

    private static final long serialVersionUID = 0;
}
