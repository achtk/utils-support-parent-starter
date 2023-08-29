package com.chua.common.support.range;

import com.chua.common.support.range.order.Ordering;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NumberConstant.NUM_2;
import static com.chua.common.support.utils.Preconditions.checkArgument;
import static com.chua.common.support.utils.Preconditions.checkNotNull;
import static com.chua.common.support.utils.RandomUtils.*;

/**
 * Range
 *
 * <blockquote>
 *
 * <table>
 * <caption>Range Types</caption>
 * <tr><th>Notation        <th>Definition               <th>Factory method
 * <tr><td>{@code (a..b)}  <td>{@code {x | a < x < b}}  <td>{@link Range#open open}
 * <tr><td>{@code [a..b]}  <td>{@code {x | a <= x <= b}}<td>{@link Range#closed closed}
 * <tr><td>{@code (a..b]}  <td>{@code {x | a < x <= b}} <td>{@link Range#openClosed openClosed}
 * <tr><td>{@code [a..b)}  <td>{@code {x | a <= x < b}} <td>{@link Range#closedOpen closedOpen}
 * <tr><td>{@code (a..+∞)} <td>{@code {x | x > a}}      <td>{@link Range#greaterThan greaterThan}
 * <tr><td>{@code [a..+∞)} <td>{@code {x | x >= a}}     <td>{@link Range#atLeast atLeast}
 * <tr><td>{@code (-∞..b)} <td>{@code {x | x < b}}      <td>{@link Range#lessThan lessThan}
 * <tr><td>{@code (-∞..b]} <td>{@code {x | x <= b}}     <td>{@link Range#atMost atMost}
 * <tr><td>{@code (-∞..+∞)}<td>{@code {x}}              <td>{@link Range#all all}
 * </table>
 *
 * </blockquote>
 * @author CH
 */
public final class Range<C extends Comparable> implements Predicate<C>, Serializable {
    static final String REG = "[\\,\\~]{1}";

    /**
     * 构建数据
     * @param base 数据
     * @return 结果
     */
    public static Range<Double> of(String base) {
        if (!base.startsWith(SYMBOL_LEFT_BRACKETS) && !base.startsWith(SYMBOL_LEFT_SQUARE_BRACKET)) {
            return Range.all();
        }

        String[] split = base.split(REG, 2);
        String item = split[0];
        if (split.length == NUM_2) {
            String item1 = split[1];
            //(1 ~  +∞) or [1 ~  +∞)
            if (!StringUtils.isNullOrEmpty(item) && StringUtils.isNullOrEmpty(item1)) {
                String end = item.trim().replace(SYMBOL_LEFT_BRACKETS, EMPTY);
                String end1 = item.trim().replace(SYMBOL_LEFT_SQUARE_BRACKET, EMPTY);
                return item.startsWith(SYMBOL_LEFT_BRACKETS) ?
                        Range.greaterThan(item.contains(".") ? NumberUtils.toDouble(end) : NumberUtils.toLong(end)) :
                        Range.atLeast(item.contains(".") ? NumberUtils.toDouble(end1) : NumberUtils.toLong(end1));
            }
            //(-∞ ~ 1) or [-∞ ~  1]
            if (StringUtils.isNullOrEmpty(item) && !StringUtils.isNullOrEmpty(item1)) {
                String end = item1.trim().replace(SYMBOL_LEFT_BRACKETS, EMPTY);
                String end1 = item1.trim().replace(SYMBOL_LEFT_SQUARE_BRACKET, EMPTY);

                return item1.startsWith(SYMBOL_RIGHT_BRACKETS) ?
                        Range.lessThan(item1.contains(".") ? NumberUtils.toDouble(end) : NumberUtils.toLong(end)) :
                        Range.atLeast(item1.contains(".") ? NumberUtils.toDouble(end1) : NumberUtils.toLong(end1));
            }

            //(-∞ ~ 1) or [-∞ ~  1]
            if (StringUtils.isNullOrEmpty(item) && StringUtils.isNullOrEmpty(item1)) {
                return Range.all();
            }

            //(0 ~ 1) or (0 ~  1]
            String value = item1.trim().replace(SYMBOL_RIGHT_BRACKETS, EMPTY);
            String value1 = item1.trim().replace(SYMBOL_RIGHT_SQUARE_BRACKET, EMPTY);

            Number end = item1.contains(".") ? NumberUtils.toDouble(value) : NumberUtils.toLong(value);
            Number end1 = item1.contains(".") ? NumberUtils.toDouble(value1) : NumberUtils.toLong(value1);
            if (item.startsWith(SYMBOL_LEFT_BRACKETS)) {
                String firstValue = item.replace(SYMBOL_LEFT_BRACKETS, EMPTY);
                Number start = firstValue.contains(".") ? Double.parseDouble(firstValue) : Long.parseLong(firstValue);
                return item1.startsWith(SYMBOL_RIGHT_BRACKETS) ?
                        Range.openClosed(firstValue.contains(".") ? start.doubleValue() : start.longValue(), firstValue.contains(".") ? end.doubleValue() : end.longValue()) :
                        Range.open(firstValue.contains(".") ? start.doubleValue() : start.longValue(), firstValue.contains(".") ? end1.doubleValue() : end1.longValue());
            }

            //[0 ~ 1) or [0 ~  1]
            String firstValue = item.replace(SYMBOL_LEFT_SQUARE_BRACKET, EMPTY);
            Number start = firstValue.contains(".") ? Double.parseDouble(firstValue) : Long.parseLong(firstValue);
            return item1.startsWith(SYMBOL_RIGHT_BRACKETS) ?
                    Range.closedOpen(firstValue.contains(".") ? start.doubleValue() : start.longValue(), firstValue.contains(".") ? end.doubleValue() : end.longValue()) :
                    Range.closed(firstValue.contains(".") ? start.doubleValue() : start.longValue(), firstValue.contains(".") ? end1.doubleValue() : end1.longValue());

        }

        return Range.all();
    }

    public C random() {
        if (hasLowerBound() && hasUpperBound()) {
            C lowerEndpoint = lowerEndpoint();
            C upperEndpoint = upperEndpoint();
            if (lowerEndpoint instanceof Long) {
                Long c = randomLong((Long) lowerEndpoint, (Long) upperEndpoint);
                return (C) c;
            }

            if (lowerEndpoint instanceof Double) {
                Double c = randomDouble((Double) lowerEndpoint, (Double) upperEndpoint);
                return (C) c;
            }


            if (lowerEndpoint instanceof String) {
                String v = lowerEndpoint.toString() + upperEndpoint.toString();
                String c = randomString(v, 10);
                return (C) c;
            }
        }
        return null;
    }

    static class LowerBoundFn implements Function<Range, AbstractCut> {
        static final LowerBoundFn INSTANCE = new LowerBoundFn();

        @Override
        public AbstractCut apply(Range range) {
            return range.lowerBound;
        }
    }

    static class UpperBoundFn implements Function<Range, AbstractCut> {
        static final UpperBoundFn INSTANCE = new UpperBoundFn();

        @Override
        public AbstractCut apply(Range range) {
            return range.upperBound;
        }
    }

    @SuppressWarnings("unchecked")
    static <C extends Comparable<?>> Function<Range<C>, AbstractCut<C>> lowerBoundFn() {
        return (Function) LowerBoundFn.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    static <C extends Comparable<?>> Function<Range<C>, AbstractCut<C>> upperBoundFn() {
        return (Function) UpperBoundFn.INSTANCE;
    }

    static <C extends Comparable<?>> Ordering<Range<C>> rangeLexOrdering() {
        return (Ordering) RangeLexOrdering.INSTANCE;
    }

    static <C extends Comparable<?>> Range<C> create(AbstractCut<C> lowerBound, AbstractCut<C> upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    /**
     * Returns a range that contains all values strictly greater than {@code lower} and strictly less
     * than {@code upper}.
     *
     * @throws IllegalArgumentException if {@code lower} is greater than <i>or equal to</i> {@code
     *     upper}
     * @throws ClassCastException if {@code lower} and {@code upper} are not mutually comparable
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> open(C lower, C upper) {
        return create(AbstractCut.aboveValue(lower), AbstractCut.belowValue(upper));
    }

    /**
     * Returns a range that contains all values greater than or equal to {@code lower} and less than
     * or equal to {@code upper}.
     *
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}
     * @throws ClassCastException if {@code lower} and {@code upper} are not mutually comparable
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> closed(C lower, C upper) {
        return create(AbstractCut.belowValue(lower), AbstractCut.aboveValue(upper));
    }

    /**
     * Returns a range that contains all values greater than or equal to {@code lower} and strictly
     * less than {@code upper}.
     *
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}
     * @throws ClassCastException if {@code lower} and {@code upper} are not mutually comparable
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper) {
        return create(AbstractCut.belowValue(lower), AbstractCut.belowValue(upper));
    }

    /**
     * Returns a range that contains all values strictly greater than {@code lower} and less than or
     * equal to {@code upper}.
     *
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}
     * @throws ClassCastException if {@code lower} and {@code upper} are not mutually comparable
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper) {
        return create(AbstractCut.aboveValue(lower), AbstractCut.aboveValue(upper));
    }

    /**
     * Returns a range that contains any value from {@code lower} to {@code upper}, where each
     * endpoint may be either inclusive (closed) or exclusive (open).
     *
     * @throws IllegalArgumentException if {@code lower} is greater than {@code upper}
     * @throws ClassCastException if {@code lower} and {@code upper} are not mutually comparable
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> range(
            C lower, BoundType lowerType, C upper, BoundType upperType) {
        checkNotNull(lowerType);
        checkNotNull(upperType);

        AbstractCut<C> lowerBound =
                (lowerType == BoundType.OPEN) ? AbstractCut.aboveValue(lower) : AbstractCut.belowValue(lower);
        AbstractCut<C> upperBound =
                (upperType == BoundType.OPEN) ? AbstractCut.belowValue(upper) : AbstractCut.aboveValue(upper);
        return create(lowerBound, upperBound);
    }

    /**
     * Returns a range that contains all values strictly less than {@code endpoint}.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> lessThan(C endpoint) {
        return create(AbstractCut.<C>belowAll(), AbstractCut.belowValue(endpoint));
    }

    /**
     * Returns a range that contains all values less than or equal to {@code endpoint}.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> atMost(C endpoint) {
        return create(AbstractCut.<C>belowAll(), AbstractCut.aboveValue(endpoint));
    }

    /**
     * Returns a range with no lower bound up to the given endpoint, which may be either inclusive
     * (closed) or exclusive (open).
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> upTo(C endpoint, BoundType boundType) {
        switch (boundType) {
            case OPEN:
                return lessThan(endpoint);
            case CLOSED:
                return atMost(endpoint);
            default:
                throw new AssertionError();
        }
    }

    /**
     * Returns a range that contains all values strictly greater than {@code endpoint}.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint) {
        return create(AbstractCut.aboveValue(endpoint), AbstractCut.<C>aboveAll());
    }

    /**
     * Returns a range that contains all values greater than or equal to {@code endpoint}.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> atLeast(C endpoint) {
        return create(AbstractCut.belowValue(endpoint), AbstractCut.<C>aboveAll());
    }

    /**
     * Returns a range from the given endpoint, which may be either inclusive (closed) or exclusive
     * (open), with no upper bound.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> downTo(C endpoint, BoundType boundType) {
        switch (boundType) {
            case OPEN:
                return greaterThan(endpoint);
            case CLOSED:
                return atLeast(endpoint);
            default:
                throw new AssertionError();
        }
    }

    private static final Range<Comparable> ALL = new Range<>(AbstractCut.belowAll(), AbstractCut.aboveAll());

    /**
     * Returns a range that contains every value of type {@code C}.
     *
     * @since 14.0
     */
    @SuppressWarnings("unchecked")
    public static <C extends Comparable<?>> Range<C> all() {
        return (Range) ALL;
    }

    /**
     * Returns a range that {@linkplain Range#contains(Comparable) contains} only the given value. The
     * returned range is {@linkplain BoundType#CLOSED closed} on both ends.
     *
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> singleton(C value) {
        return closed(value, value);
    }

    /**
     * Returns the minimal range that {@linkplain Range#contains(Comparable) contains} all of the
     * given values. The returned range is {@linkplain BoundType#CLOSED closed} on both ends.
     *
     * @throws ClassCastException if the values are not mutually comparable
     * @throws NoSuchElementException if {@code values} is empty
     * @throws NullPointerException if any of {@code values} is null
     * @since 14.0
     */
    public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> values) {
        checkNotNull(values);
        if (values instanceof SortedSet) {
            SortedSet<C> set = (SortedSet<C>) values;
            Comparator<?> comparator = set.comparator();
            if (Ordering.natural().equals(comparator) || comparator == null) {
                return closed(set.first(), set.last());
            }
        }
        Iterator<C> valueIterator = values.iterator();
        C min = checkNotNull(valueIterator.next());
        C max = min;
        while (valueIterator.hasNext()) {
            C value = checkNotNull(valueIterator.next());
            min = Ordering.natural().min(min, value);
            max = Ordering.natural().max(max, value);
        }
        return closed(min, max);
    }

    final AbstractCut<C> lowerBound;
    final AbstractCut<C> upperBound;

    private Range(AbstractCut<C> lowerBound, AbstractCut<C> upperBound) {
        this.lowerBound = checkNotNull(lowerBound);
        this.upperBound = checkNotNull(upperBound);
        if (lowerBound.compareTo(upperBound) > 0
                || lowerBound == AbstractCut.<C>aboveAll()
                || upperBound == AbstractCut.<C>belowAll()) {
            throw new IllegalArgumentException("Invalid range: " + toString(lowerBound, upperBound));
        }
    }

    /** Returns {@code true} if this range has a lower endpoint. */
    public boolean hasLowerBound() {
        return lowerBound != AbstractCut.belowAll();
    }

    /**
     * Returns the lower endpoint of this range.
     *
     * @throws IllegalStateException if this range is unbounded below (that is, {@link
     *     #hasLowerBound()} returns {@code false})
     */
    public C lowerEndpoint() {
        return lowerBound.endpoint();
    }

    /**
     * Returns the type of this range's lower bound: {@link BoundType#CLOSED} if the range includes
     * its lower endpoint, {@link BoundType#OPEN} if it does not.
     *
     * @throws IllegalStateException if this range is unbounded below (that is, {@link
     *     #hasLowerBound()} returns {@code false})
     */
    public BoundType lowerBoundType() {
        return lowerBound.typeAsLowerBound();
    }

    /** Returns {@code true} if this range has an upper endpoint. */
    public boolean hasUpperBound() {
        return upperBound != AbstractCut.aboveAll();
    }

    /**
     * Returns the upper endpoint of this range.
     *
     * @throws IllegalStateException if this range is unbounded above (that is, {@link
     *     #hasUpperBound()} returns {@code false})
     */
    public C upperEndpoint() {
        return upperBound.endpoint();
    }

    /**
     * Returns the type of this range's upper bound: {@link BoundType#CLOSED} if the range includes
     * its upper endpoint, {@link BoundType#OPEN} if it does not.
     *
     * @throws IllegalStateException if this range is unbounded above (that is, {@link
     *     #hasUpperBound()} returns {@code false})
     */
    public BoundType upperBoundType() {
        return upperBound.typeAsUpperBound();
    }

    /**
     * Returns {@code true} if this range is of the form {@code [v..v)} or {@code (v..v]}. (This does
     * not encompass ranges of the form {@code (v..v)}, because such ranges are <i>invalid</i> and
     * can't be constructed at all.)
     *
     * <p>Note that certain discrete ranges such as the integer range {@code (3..4)} are <b>not</b>
     * considered empty, even though they contain no actual values. In these cases, it may be helpful
     * to preprocess ranges with {@link #canonical(DiscreteDomain)}.
     */
    public boolean isEmpty() {
        return lowerBound.equals(upperBound);
    }

    /**
     * Returns {@code true} if {@code value} is within the bounds of this range. For example, on the
     * range {@code [0..2)}, {@code contains(1)} returns {@code true}, while {@code contains(2)}
     * returns {@code false}.
     */
    public boolean contains(C value) {
        checkNotNull(value);
        // let this throw CCE if there is some trickery going on
        return lowerBound.isLessThan(value) && !upperBound.isLessThan(value);
    }

    /**
     * @deprecated Provided only to satisfy the {@link Predicate} interface; use {@link #contains}
     *     instead.
     */
    @Deprecated
    @Override
    public boolean test(C input) {
        return contains(input);
    }

    /**
     * Returns {@code true} if every element in {@code values} is {@linkplain #contains contained} in
     * this range.
     */
    public boolean containsAll(Iterable<? extends C> values) {
        if (CollectionUtils.isEmpty(values)) {
            return true;
        }

        // this optimizes testing equality of two range-backed sets
        if (values instanceof SortedSet) {
            SortedSet<? extends C> set = (SortedSet<? extends C>) values;
            Comparator<?> comparator = set.comparator();
            if (Ordering.natural().equals(comparator) || comparator == null) {
                return contains(set.first()) && contains(set.last());
            }
        }

        for (C value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the bounds of {@code other} do not extend outside the bounds of this
     * range. Examples:
     *
     * <ul>
     *   <li>{@code [3..6]} encloses {@code [4..5]}
     *   <li>{@code (3..6)} encloses {@code (3..6)}
     *   <li>{@code [3..6]} encloses {@code [4..4)} (even though the latter is empty)
     *   <li>{@code (3..6]} does not enclose {@code [3..6]}
     *   <li>{@code [4..5]} does not enclose {@code (3..6)} (even though it contains every value
     *       contained by the latter range)
     *   <li>{@code [3..6]} does not enclose {@code (1..1]} (even though it contains every value
     *       contained by the latter range)
     * </ul>
     *
     * <p>Note that if {@code a.encloses(b)}, then {@code b.contains(v)} implies {@code
     * a.contains(v)}, but as the last two examples illustrate, the converse is not always true.
     *
     * <p>Being reflexive, antisymmetric and transitive, the {@code encloses} relation defines a
     * <i>partial order</i> over ranges. There exists a unique {@linkplain Range#all maximal} range
     * according to this relation, and also numerous {@linkplain #isEmpty minimal} ranges. Enclosure
     * also implies {@linkplain #isConnected connectedness}.
     */
    public boolean encloses(Range<C> other) {
        return lowerBound.compareTo(other.lowerBound) <= 0
                && upperBound.compareTo(other.upperBound) >= 0;
    }

    /**
     * Returns {@code true} if there exists a (possibly empty) range which is {@linkplain #encloses
     * enclosed} by both this range and {@code other}.
     *
     * <p>For example,
     *
     * <ul>
     *   <li>{@code [2, 4)} and {@code [5, 7)} are not connected
     *   <li>{@code [2, 4)} and {@code [3, 5)} are connected, because both enclose {@code [3, 4)}
     *   <li>{@code [2, 4)} and {@code [4, 6)} are connected, because both enclose the empty range
     *       {@code [4, 4)}
     * </ul>
     *
     * <p>Note that this range and {@code other} have a well-defined {@linkplain #span union} and
     * {@linkplain #intersection intersection} (as a single, possibly-empty range) if and only if this
     * method returns {@code true}.
     *
     * <p>Note that certain discrete ranges are not considered connected, even though there are no
     * elements "between them." For example, {@code [3, 5]} is not considered connected to {@code [6,
     * 10]}. In these cases, it may be desirable for both input ranges to be preprocessed with {@link
     * #canonical(DiscreteDomain)} before testing for connectedness.
     */
    public boolean isConnected(Range<C> other) {
        return lowerBound.compareTo(other.upperBound) <= 0
                && other.lowerBound.compareTo(upperBound) <= 0;
    }

    /**
     * Returns the maximal range {@linkplain #encloses enclosed} by both this range and {@code
     * connectedRange}, if such a range exists.
     *
     * <p>For example, the intersection of {@code [1..5]} and {@code (3..7)} is {@code (3..5]}. The
     * resulting range may be empty; for example, {@code [1..5)} intersected with {@code [5..7)}
     * yields the empty range {@code [5..5)}.
     *
     * <p>The intersection exists if and only if the two ranges are {@linkplain #isConnected
     * connected}.
     *
     * <p>The intersection operation is commutative, associative and idempotent, and its identity
     * element is {@link Range#all}).
     *
     * @throws IllegalArgumentException if {@code isConnected(connectedRange)} is {@code false}
     */
    public Range<C> intersection(Range<C> connectedRange) {
        int lowerCmp = lowerBound.compareTo(connectedRange.lowerBound);
        int upperCmp = upperBound.compareTo(connectedRange.upperBound);
        if (lowerCmp >= 0 && upperCmp <= 0) {
            return this;
        } else if (lowerCmp <= 0 && upperCmp >= 0) {
            return connectedRange;
        } else {
            AbstractCut<C> newLower = (lowerCmp >= 0) ? lowerBound : connectedRange.lowerBound;
            AbstractCut<C> newUpper = (upperCmp <= 0) ? upperBound : connectedRange.upperBound;

            // create() would catch this, but give a confusing error message
            checkArgument(
                    newLower.compareTo(newUpper) <= 0,
                    "intersection is undefined for disconnected ranges %s and %s",
                    this,
                    connectedRange);

            // TODO(kevinb): all the precondition checks in the constructor are redundant...
            return create(newLower, newUpper);
        }
    }

    /**
     * Returns the maximal range lying between this range and {@code otherRange}, if such a range
     * exists. The resulting range may be empty if the two ranges are adjacent but non-overlapping.
     *
     * <p>For example, the gap of {@code [1..5]} and {@code (7..10)} is {@code (5..7]}. The resulting
     * range may be empty; for example, the gap between {@code [1..5)} {@code [5..7)} yields the empty
     * range {@code [5..5)}.
     *
     * <p>The gap exists if and only if the two ranges are either disconnected or immediately adjacent
     * (any intersection must be an empty range).
     *
     * <p>The gap operation is commutative.
     *
     * @throws IllegalArgumentException if this range and {@code otherRange} have a nonempty
     *     intersection
     * @since 27.0
     */
    public Range<C> gap(Range<C> otherRange) {
        /*
         * For an explanation of the basic principle behind this check, see
         * https://stackoverflow.com/a/35754308/28465
         *
         * In that explanation's notation, our `overlap` check would be `x1 < y2 && y1 < x2`. We've
         * flipped one part of the check so that we're using "less than" in both cases (rather than a
         * mix of "less than" and "greater than"). We've also switched to "strictly less than" rather
         * than "less than or equal to" because of *handwave* the difference between "endpoints of
         * inclusive ranges" and "Cuts."
         */
        if (lowerBound.compareTo(otherRange.upperBound) < 0
                && otherRange.lowerBound.compareTo(upperBound) < 0) {
            throw new IllegalArgumentException(
                    "Ranges have a nonempty intersection: " + this + ", " + otherRange);
        }

        boolean isThisFirst = this.lowerBound.compareTo(otherRange.lowerBound) < 0;
        Range<C> firstRange = isThisFirst ? this : otherRange;
        Range<C> secondRange = isThisFirst ? otherRange : this;
        return create(firstRange.upperBound, secondRange.lowerBound);
    }

    /**
     * Returns the minimal range that {@linkplain #encloses encloses} both this range and {@code
     * other}. For example, the span of {@code [1..3]} and {@code (5..7)} is {@code [1..7)}.
     *
     * <p><i>If</i> the input ranges are {@linkplain #isConnected connected}, the returned range can
     * also be called their <i>union</i>. If they are not, note that the span might contain values
     * that are not contained in either input range.
     *
     * <p>Like {@link #intersection(Range) intersection}, this operation is commutative, associative
     * and idempotent. Unlike it, it is always well-defined for any two input ranges.
     */
    public Range<C> span(Range<C> other) {
        int lowerCmp = lowerBound.compareTo(other.lowerBound);
        int upperCmp = upperBound.compareTo(other.upperBound);
        if (lowerCmp <= 0 && upperCmp >= 0) {
            return this;
        } else if (lowerCmp >= 0 && upperCmp <= 0) {
            return other;
        } else {
            AbstractCut<C> newLower = (lowerCmp <= 0) ? lowerBound : other.lowerBound;
            AbstractCut<C> newUpper = (upperCmp >= 0) ? upperBound : other.upperBound;
            return create(newLower, newUpper);
        }
    }

    /**
     * Returns the canonical form of this range in the given domain. The canonical form has the
     * following properties:
     *
     * <ul>
     *   <li>equivalence: {@code a.canonical().contains(v) == a.contains(v)} for all {@code v} (in
     *       other words, {@code ContiguousSet.create(a.canonical(domain), domain).equals(
     *       ContiguousSet.create(a, domain))}
     *   <li>uniqueness: unless {@code a.isEmpty()}, {@code ContiguousSet.create(a,
     *       domain).equals(ContiguousSet.create(b, domain))} implies {@code
     *       a.canonical(domain).equals(b.canonical(domain))}
     *   <li>idempotence: {@code a.canonical(domain).canonical(domain).equals(a.canonical(domain))}
     * </ul>
     *
     * <p>Furthermore, this method guarantees that the range returned will be one of the following
     * canonical forms:
     *
     * <ul>
     *   <li>[start..end)
     *   <li>[start..+∞)
     *   <li>(-∞..end) (only if type {@code C} is unbounded below)
     *   <li>(-∞..+∞) (only if type {@code C} is unbounded below)
     * </ul>
     */
    public Range<C> canonical(DiscreteDomain<C> domain) {
        checkNotNull(domain);
        AbstractCut<C> lower = lowerBound.canonical(domain);
        AbstractCut<C> upper = upperBound.canonical(domain);
        return (lower == lowerBound && upper == upperBound) ? this : create(lower, upper);
    }

    /**
     * Returns {@code true} if {@code object} is a range having the same endpoints and bound types as
     * this range. Note that discrete ranges such as {@code (1..4)} and {@code [2..3]} are <b>not</b>
     * equal to one another, despite the fact that they each contain precisely the same set of values.
     * Similarly, empty ranges are not equal unless they have exactly the same representation, so
     * {@code [3..3)}, {@code (3..3]}, {@code (4..4]} are all unequal.
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Range) {
            Range<?> other = (Range<?>) object;
            return lowerBound.equals(other.lowerBound) && upperBound.equals(other.upperBound);
        }
        return false;
    }

    /** Returns a hash code for this range. */
    @Override
    public int hashCode() {
        return lowerBound.hashCode() * 31 + upperBound.hashCode();
    }

    /**
     * Returns a string representation of this range, such as {@code "[3..5)"} (other examples are
     * listed in the class documentation).
     */
    @Override
    public String toString() {
        return toString(lowerBound, upperBound);
    }

    private static String toString(AbstractCut<?> lowerBound, AbstractCut<?> upperBound) {
        StringBuilder sb = new StringBuilder(16);
        lowerBound.describeAsLowerBound(sb);
        sb.append("..");
        upperBound.describeAsUpperBound(sb);
        return sb.toString();
    }

    Object readResolve() {
        if (this.equals(ALL)) {
            return all();
        } else {
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    static int compareOrThrow(Comparable left, Comparable right) {
        return left.compareTo(right);
    }

    /** Needed to serialize sorted collections of Ranges. */
    private static class RangeLexOrdering extends Ordering<Range<?>> implements Serializable {
        static final Ordering<Range<?>> INSTANCE = new RangeLexOrdering();

        @Override
        public int compare(Range<?> left, Range<?> right) {
            return ComparisonChain.start()
                    .compare(left.lowerBound, right.lowerBound)
                    .compare(left.upperBound, right.upperBound)
                    .result();
        }

        private static final long serialVersionUID = 0;
    }

    private static final long serialVersionUID = 0;
}
