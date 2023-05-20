package com.chua.common.support.collection;

import com.chua.common.support.lang.math.IntMath;

import java.util.*;

import static com.chua.common.support.utils.Preconditions.checkElementIndex;

/**
 * 笛卡尔积
 * @author CH
 */
public class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {

    private final transient List<List<E>> axes;
    private final transient int[] axesSizeProduct;

    static <E> List<List<E>> create(List<? extends List<? extends E>> lists) {
        List<List<E>> axesBuilder = new ArrayList<>(lists.size());
        for (List<? extends E> list : lists) {
            List<E> copy = new ArrayList<>(list);
            if (copy.isEmpty()) {
                return Collections.emptyList();
            }
            axesBuilder.add(copy);
        }
        return new CartesianList<>(axesBuilder);
    }

    CartesianList(List<List<E>> axes) {
        this.axes = axes;
        int[] axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        try {
            for (int i = axes.size() - 1; i >= 0; i--) {
                axesSizeProduct[i] = IntMath.checkedMultiply(axesSizeProduct[i + 1], axes.get(i).size());
            }
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "Cartesian product too large; must have size at most Integer.MAX_VALUE");
        }
        this.axesSizeProduct = axesSizeProduct;
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof List)) {
            return -1;
        }
        List<?> list = (List<?>) o;
        if (list.size() != axes.size()) {
            return -1;
        }
        ListIterator<?> itr = list.listIterator();
        int computedIndex = 0;
        while (itr.hasNext()) {
            int axisIndex = itr.nextIndex();
            int elemIndex = axes.get(axisIndex).indexOf(itr.next());
            if (elemIndex == -1) {
                return -1;
            }
            computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
        }
        return computedIndex;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof List)) {
            return -1;
        }
        List<?> list = (List<?>) o;
        if (list.size() != axes.size()) {
            return -1;
        }
        ListIterator<?> itr = list.listIterator();
        int computedIndex = 0;
        while (itr.hasNext()) {
            int axisIndex = itr.nextIndex();
            int elemIndex = axes.get(axisIndex).lastIndexOf(itr.next());
            if (elemIndex == -1) {
                return -1;
            }
            computedIndex += elemIndex * axesSizeProduct[axisIndex + 1];
        }
        return computedIndex;
    }

    @Override
    public List<E> get(int index) {
        checkElementIndex(index, size());
        return new AbstractList<E>() {

            @Override
            public int size() {
                return axes.size();
            }

            @Override
            public E get(int axis) {
                checkElementIndex(axis, size());
                int axisIndex = getAxisIndexForProductIndex(index, axis);
                return axes.get(axis).get(axisIndex);
            }

        };
    }

    @Override
    public int size() {
        return axesSizeProduct[0];
    }

    @Override
    public boolean contains(Object object) {
        if (!(object instanceof List)) {
            return false;
        }
        List<?> list = (List<?>) object;
        if (list.size() != axes.size()) {
            return false;
        }
        int i = 0;
        for (Object o : list) {
            if (!axes.get(i).contains(o)) {
                return false;
            }
            i++;
        }
        return true;
    }
}
