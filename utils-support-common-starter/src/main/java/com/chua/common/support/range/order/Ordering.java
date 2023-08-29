package com.chua.common.support.range.order;

import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.MapUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static com.chua.common.support.constant.NumberConstant.TWE;
import static com.chua.common.support.utils.Preconditions.checkNonNegative;
import static com.chua.common.support.utils.Preconditions.checkNotNull;

/**
 * order
 *
 * @author CH
 */
public abstract class Ordering<T extends Object> implements Comparator<T> {
    /**
     * Returns a serializable ordering that uses the natural order of the values. The ordering throws
     * a {@link NullPointerException} when passed a null parameter.
     *
     * <p>The type specification is {@code <C extends Comparable>}, instead of the technically correct
     * {@code <C extends Comparable<? super C>>}, to support legacy types from before Java 5.
     *
     * <p><b>Java 8 users:</b> use {@link Comparator#naturalOrder} instead.
     */

    @SuppressWarnings("unchecked")
    public static <C extends Comparable> Ordering<C> natural() {
        return (Ordering<C>) NaturalOrdering.INSTANCE;
    }

    // Static factories

    /**
     * Returns an ordering based on an <i>existing</i> comparator instance. Note that it is
     * unnecessary to create a <i>new</i> anonymous inner class implementing {@code Comparator} just
     * to pass it in here. Instead, simply subclass {@code Ordering} and implement its {@code compare}
     * method directly.
     *
     * <p><b>Java 8 users:</b> this class is now obsolete as explained in the class documentation, so
     * there is no need to use this method.
     *
     * @param comparator the comparator that defines the order
     * @return comparator itself if it is already an {@code Ordering}; otherwise an ordering that
     * wraps that comparator
     */

    public static <T extends Object> Ordering<T> from(Comparator<T> comparator) {
        return (comparator instanceof Ordering)
                ? (Ordering<T>) comparator
                : new ComparatorOrdering<T>(comparator);
    }

    /**
     * Simply returns its argument.
     *
     * @deprecated no need to use this
     */

    @Deprecated
    public static <T extends Object> Ordering<T> from(Ordering<T> ordering) {
        return checkNotNull(ordering);
    }

    /**
     * Returns an ordering that compares objects by the natural ordering of their string
     * representations as returned by {@code toString()}. It does not support null values.
     *
     * <p>The comparator is serializable.
     *
     * <p><b>Java 8 users:</b> Use {@code Comparator.comparing(Object::toString)} instead.
     */

    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }

    /**
     * Constructs a new instance of this class (only invokable by the subclass constructor, typically
     * implicit).
     */
    protected Ordering() {
    }

    // Instance-based factories (and any static equivalents)

    /**
     * Returns the reverse of this ordering; the {@code Ordering} equivalent to {@link
     * Collections#reverseOrder(Comparator)}.
     *
     * <p><b>Java 8 users:</b> Use {@code thisComparator.reversed()} instead.
     */
    // type parameter <S> lets us avoid the extra <String> in statements like:
    // Ordering<String> o = Ordering.<String>natural().reverse();
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<S>(this);
    }

    /**
     * Returns a new ordering on {@code F} which orders elements by first applying a function to them,
     * then comparing those results using {@code this}. For example, to compare objects by their
     * string forms, in a case-insensitive manner, use:
     *
     * <pre>{@code
     * Ordering.from(String.CASE_INSENSITIVE_ORDER)
     *     .onResultOf(Functions.toStringFunction())
     * }</pre>
     *
     * <p><b>Java 8 users:</b> Use {@code Comparator.comparing(function, thisComparator)} instead (you
     * can omit the comparator if it is the natural order).
     */

    public <F extends Object> Ordering<F> onResultOf(Function<F, ? extends T> function) {
        return new ByFunctionOrdering<>(function, this);
    }

    <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
        return onResultOf(MapUtils.<T2>keyFunction());
    }

    /**
     * Returns an ordering which first uses the ordering {@code this}, but which in the event of a
     * "tie", then delegates to {@code secondaryComparator}. For example, to sort a bug list first by
     * status and second by priority, you might use {@code byStatus.compound(byPriority)}. For a
     * compound ordering with three or more components, simply chain multiple calls to this method.
     *
     * <p>An ordering produced by this method, or a chain of calls to this method, is equivalent to
     * one created using {@link Ordering#compound(Iterable)} on the same component comparators.
     *
     * <p><b>Java 8 users:</b> Use {@code thisComparator.thenComparing(secondaryComparator)} instead.
     * Depending on what {@code secondaryComparator} is, one of the other overloads of {@code
     * thenComparing} may be even more useful.
     */

    public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator) {
        return new CompoundOrdering<U>(this, checkNotNull(secondaryComparator));
    }

    /**
     * Returns an ordering which tries each given comparator in order until a non-zero result is
     * found, returning that result, and returning zero only if all comparators return zero. The
     * returned ordering is based on the state of the {@code comparators} iterable at the time it was
     * provided to this method.
     *
     * <p>The returned ordering is equivalent to that produced using {@code
     * Ordering.from(comp1).compound(comp2).compound(comp3) . . .}.
     *
     * <p><b>Warning:</b> Supplying an argument with undefined iteration order, such as a {@link
     * HashSet}, will produce non-deterministic results.
     *
     * <p><b>Java 8 users:</b> Use a chain of calls to {@link Comparator#thenComparing(Comparator)},
     * or {@code comparatorCollection.stream().reduce(Comparator::thenComparing).get()} (if the
     * collection might be empty, also provide a default comparator as the {@code identity} parameter
     * to {@code reduce}).
     *
     * @param comparators the comparators to try in order
     */

    public static <T extends Object> Ordering<T> compound(
            Iterable<? extends Comparator<? super T>> comparators) {
        return new CompoundOrdering<T>(comparators);
    }

    /**
     * Returns a new ordering which sorts iterables by comparing corresponding elements pairwise until
     * a nonzero result is found; imposes "dictionary order". If the end of one iterable is reached,
     * but not the other, the shorter iterable is considered to be less than the longer one. For
     * example, a lexicographical natural ordering over integers considers {@code [] < [1] < [1, 1] <
     * [1, 2] < [2]}.
     *
     * <p>Note that {@code ordering.lexicographical().reverse()} is not equivalent to {@code
     * ordering.reverse().lexicographical()} (consider how each would order {@code [1]} and {@code [1,
     * 1]}).
     *
     * @since 2.0
     */
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        /*
         * Note that technically the returned ordering should be capable of
         * handling not just {@code Iterable<S>} instances, but also any {@code
         * Iterable<? extends S>}. However, the need for this comes up so rarely
         * that it doesn't justify making everyone else deal with the very ugly
         * wildcard.
         */
        return new LexicographicalOrdering<S>(this);
    }

    /**
     * Regular instance methods
     *
     * @param left  value
     * @param right value
     * @return instance
     */

    @Override
    public abstract int compare(T left, T right);

    /**
     * Returns the least of the specified values according to this ordering. If there are multiple
     * least values, the first of those is returned. The iterator will be left exhausted: its {@code
     * hasNext()} method will return {@code false}.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterator).min(thisComparator).get()} instead
     * (but note that it does not guarantee which tied minimum element is returned).
     *
     * @param iterator the iterator whose minimum element is to be determined
     * @throws NoSuchElementException if {@code iterator} is empty
     * @throws ClassCastException     if the parameters are not <i>mutually comparable</i> under this
     *                                ordering.
     * @since 11.0
     */

    public <E extends T> E min(Iterator<E> iterator) {
        // let this throw NoSuchElementException as necessary
        E minSoFar = iterator.next();

        while (iterator.hasNext()) {
            minSoFar = min(minSoFar, iterator.next());
        }

        return minSoFar;
    }

    /**
     * Returns the least of the specified values according to this ordering. If there are multiple
     * least values, the first of those is returned.
     *
     * <p><b>Java 8 users:</b> If {@code iterable} is a {@link Collection}, use {@code
     * Collections.min(collection, thisComparator)} instead. Otherwise, use {@code
     * Streams.stream(iterable).min(thisComparator).get()} instead. Note that these alternatives do
     * not guarantee which tied minimum element is returned)
     *
     * @param iterable the iterable whose minimum element is to be determined
     * @throws NoSuchElementException if {@code iterable} is empty
     * @throws ClassCastException     if the parameters are not <i>mutually comparable</i> under this
     *                                ordering.
     */

    public <E extends T> E min(Iterable<E> iterable) {
        return min(iterable.iterator());
    }

    /**
     * Returns the lesser of the two values according to this ordering. If the values compare as 0,
     * the first is returned.
     *
     * <p><b>Implementation note:</b> this method is invoked by the default implementations of the
     * other {@code min} overloads, so overriding it will affect their behavior.
     *
     * <p><b>Note:</b> Consider using {@code Comparators.min(a, b, thisComparator)} instead. If {@code
     * thisComparator} is {@link Ordering#natural}, then use {@code Comparators.min(a, b)}.
     *
     * @param a value to compare, returned if less than or equal to b.
     * @param b value to compare.
     * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
     *                            ordering.
     */

    public <E extends T> E min(E a, E b) {
        return (compare(a, b) <= 0) ? a : b;
    }

    /**
     * Returns the least of the specified values according to this ordering. If there are multiple
     * least values, the first of those is returned.
     *
     * <p><b>Java 8 users:</b> Use {@code Collections.min(Arrays.asList(a, b, c...), thisComparator)}
     * instead (but note that it does not guarantee which tied minimum element is returned).
     *
     * @param a    value to compare, returned if less than or equal to the rest.
     * @param b    value to compare
     * @param c    value to compare
     * @param rest values to compare
     * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
     *                            ordering.
     */

    public <E extends T> E min(
            E a, E b, E c, E... rest) {
        E minSoFar = min(min(a, b), c);

        for (E r : rest) {
            minSoFar = min(minSoFar, r);
        }

        return minSoFar;
    }

    /**
     * Returns the greatest of the specified values according to this ordering. If there are multiple
     * greatest values, the first of those is returned. The iterator will be left exhausted: its
     * {@code hasNext()} method will return {@code false}.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterator).max(thisComparator).get()} instead
     * (but note that it does not guarantee which tied maximum element is returned).
     *
     * @param iterator the iterator whose maximum element is to be determined
     * @throws NoSuchElementException if {@code iterator} is empty
     * @throws ClassCastException     if the parameters are not <i>mutually comparable</i> under this
     *                                ordering.
     * @since 11.0
     */

    public <E extends T> E max(Iterator<E> iterator) {
        // let this throw NoSuchElementException as necessary
        E maxSoFar = iterator.next();

        while (iterator.hasNext()) {
            maxSoFar = max(maxSoFar, iterator.next());
        }

        return maxSoFar;
    }

    /**
     * Returns the greatest of the specified values according to this ordering. If there are multiple
     * greatest values, the first of those is returned.
     *
     * <p><b>Java 8 users:</b> If {@code iterable} is a {@link Collection}, use {@code
     * Collections.max(collection, thisComparator)} instead. Otherwise, use {@code
     * Streams.stream(iterable).max(thisComparator).get()} instead. Note that these alternatives do
     * not guarantee which tied maximum element is returned)
     *
     * @param iterable the iterable whose maximum element is to be determined
     * @throws NoSuchElementException if {@code iterable} is empty
     * @throws ClassCastException     if the parameters are not <i>mutually comparable</i> under this
     *                                ordering.
     */

    public <E extends T> E max(Iterable<E> iterable) {
        return max(iterable.iterator());
    }

    /**
     * Returns the greater of the two values according to this ordering. If the values compare as 0,
     * the first is returned.
     *
     * <p><b>Implementation note:</b> this method is invoked by the default implementations of the
     * other {@code max} overloads, so overriding it will affect their behavior.
     *
     * <p><b>Note:</b> Consider using {@code Comparators.max(a, b, thisComparator)} instead. If {@code
     * thisComparator} is {@link Ordering#natural}, then use {@code Comparators.max(a, b)}.
     *
     * @param a value to compare, returned if greater than or equal to b.
     * @param b value to compare.
     * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
     *                            ordering.
     */

    public <E extends T> E max(E a, E b) {
        return (compare(a, b) >= 0) ? a : b;
    }

    /**
     * Returns the greatest of the specified values according to this ordering. If there are multiple
     * greatest values, the first of those is returned.
     *
     * <p><b>Java 8 users:</b> Use {@code Collections.max(Arrays.asList(a, b, c...), thisComparator)}
     * instead (but note that it does not guarantee which tied maximum element is returned).
     *
     * @param a    value to compare, returned if greater than or equal to the rest.
     * @param b    value to compare
     * @param c    value to compare
     * @param rest values to compare
     * @throws ClassCastException if the parameters are not <i>mutually comparable</i> under this
     *                            ordering.
     */

    public <E extends T> E max(
            E a, E b, E c, E... rest) {
        E maxSoFar = max(max(a, b), c);

        for (E r : rest) {
            maxSoFar = max(maxSoFar, r);
        }

        return maxSoFar;
    }

    /**
     * Returns the {@code k} least elements of the given iterable according to this ordering, in order
     * from least to greatest. If there are fewer than {@code k} elements present, all will be
     * included.
     *
     * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
     * elements are equivalent, it is undefined which will come first.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterable).collect(Comparators.least(k,
     * thisComparator))} instead.
     *
     * @return an immutable {@code RandomAccess} list of the {@code k} least elements in ascending
     * order
     * @throws IllegalArgumentException if {@code k} is negative
     * @since 8.0
     */
    public <E extends T> List<E> leastOf(Iterable<E> iterable, int k) {
        if (iterable instanceof Collection) {
            Collection<E> collection = (Collection<E>) iterable;
            if (collection.size() <= TWE * k) {
                // In this case, just dumping the collection to an array and sorting is
                // faster than using the implementation for Iterator, which is
                // specialized for k much smaller than n.

                @SuppressWarnings("unchecked")
                E[] array = collection.toArray((E[]) Array.newInstance(collection.iterator().next().getClass(), 0));
                Arrays.sort(array, this);
                if (array.length > k) {
                    array = Arrays.copyOf(array, k);
                }
                return Collections.unmodifiableList(Arrays.asList(array));
            }
        }
        return leastOf(iterable.iterator(), k);
    }

    /**
     * Returns the {@code k} least elements from the given iterator according to this ordering, in
     * order from least to greatest. If there are fewer than {@code k} elements present, all will be
     * included.
     *
     * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
     * elements are equivalent, it is undefined which will come first.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterator).collect(Comparators.least(k,
     * thisComparator))} instead.
     *
     * @return an immutable {@code RandomAccess} list of the {@code k} least elements in ascending
     * order
     * @throws IllegalArgumentException if {@code k} is negative
     * @since 14.0
     */
    public <E extends T> List<E> leastOf(Iterator<E> iterator, int k) {
        checkNotNull(iterator);
        checkNonNegative(k, "k");

        if (k == 0 || !iterator.hasNext()) {
            return Collections.emptyList();
        } else if (k >= Integer.MAX_VALUE / TWE) {
            // k is really large; just do a straightforward sorted-copy-and-sublist
            ArrayList<E> list = new ArrayList<>(CollectionUtils.newArrayList(iterator));
            Collections.sort(list, this);
            if (list.size() > k) {
                list.subList(k, list.size()).clear();
            }
            list.trimToSize();
            return Collections.unmodifiableList(list);
        } else {
            TopKSelector<E> selector = TopKSelector.least(k, this);
            selector.offerAll(iterator);
            return selector.topK();
        }
    }

    /**
     * Returns the {@code k} greatest elements of the given iterable according to this ordering, in
     * order from greatest to least. If there are fewer than {@code k} elements present, all will be
     * included.
     *
     * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
     * elements are equivalent, it is undefined which will come first.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterable).collect(Comparators.greatest(k,
     * thisComparator))} instead.
     *
     * @return an immutable {@code RandomAccess} list of the {@code k} greatest elements in
     * <i>descending order</i>
     * @throws IllegalArgumentException if {@code k} is negative
     * @since 8.0
     */
    public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k) {
        // TODO(kevinb): see if delegation is hurting performance noticeably
        // TODO(kevinb): if we change this implementation, add full unit tests.
        return reverse().leastOf(iterable, k);
    }

    /**
     * Returns the {@code k} greatest elements from the given iterator according to this ordering, in
     * order from greatest to least. If there are fewer than {@code k} elements present, all will be
     * included.
     *
     * <p>The implementation does not necessarily use a <i>stable</i> sorting algorithm; when multiple
     * elements are equivalent, it is undefined which will come first.
     *
     * <p><b>Java 8 users:</b> Use {@code Streams.stream(iterator).collect(Comparators.greatest(k,
     * thisComparator))} instead.
     *
     * @return an immutable {@code RandomAccess} list of the {@code k} greatest elements in
     * <i>descending order</i>
     * @throws IllegalArgumentException if {@code k} is negative
     * @since 14.0
     */
    public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k) {
        return reverse().leastOf(iterator, k);
    }


    /**
     * {@link Collections#binarySearch(List, Object, Comparator) Searches} {@code sortedList} for
     * {@code key} using the binary search algorithm. The list must be sorted using this ordering.
     *
     * @param sortedList the list to be searched
     * @param key        the key to be searched for
     * @deprecated Use {@link Collections#binarySearch(List, Object, Comparator)} directly.
     */
    @Deprecated
    public int binarySearch(
            List<? extends T> sortedList, T key) {
        return Collections.binarySearch(sortedList, key, this);
    }

    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;
}
