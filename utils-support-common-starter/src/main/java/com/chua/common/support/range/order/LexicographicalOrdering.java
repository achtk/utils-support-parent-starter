package com.chua.common.support.range.order;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

/**LexicographicalOrdering
 * @author CH
 */
final class LexicographicalOrdering<T extends Object> extends BaseOrdering<Iterable<T>> implements Serializable {
    final Comparator<? super T> elementOrder;

    LexicographicalOrdering(Comparator<? super T> elementOrder) {
        this.elementOrder = elementOrder;
    }

    @Override
    public int compare(Iterable<T> leftIterable, Iterable<T> rightIterable) {
        Iterator<T> left = leftIterable.iterator();
        Iterator<T> right = rightIterable.iterator();
        while (left.hasNext()) {
            if (!right.hasNext()) {
                return LEFT_IS_GREATER;
            }
            int result = elementOrder.compare(left.next(), right.next());
            if (result != 0) {
                return result;
            }
        }
        if (right.hasNext()) {
            return RIGHT_IS_GREATER;
        }
        return 0;
    }

    @Override
    public boolean equals( Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LexicographicalOrdering) {
            LexicographicalOrdering<?> that = (LexicographicalOrdering<?>) object;
            return this.elementOrder.equals(that.elementOrder);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return elementOrder.hashCode() ^ 2075626741;
    }

    @Override
    public String toString() {
        return elementOrder + ".lexicographical()";
    }

    private static final long serialVersionUID = 0;
}
