package com.chua.common.support.range.order;

import com.chua.common.support.lang.math.IntMath;

import java.math.RoundingMode;
import java.util.*;

import static com.chua.common.support.constant.NumberConstant.NUM_2;
import static com.chua.common.support.utils.Preconditions.checkArgument;
import static com.chua.common.support.utils.Preconditions.uncheckedCastNullableToT;
import static lombok.Lombok.checkNotNull;

/**
 * TopSelector
 * @author CH
 */
public class TopSelector<T extends Object> {

    /**
     * Returns a {@code TopSelector} that collects the lowest {@code k} elements added to it,
     * relative to the natural ordering of the elements, and returns them via {@link #topK} in
     * ascending order.
     *
     * @throws IllegalArgumentException if {@code k < 0} or {@code k > Integer.MAX_VALUE / 2}
     */
    public static <T extends Comparable<? super T>> TopSelector<T> least(int k) {
        return least(k, Ordering.natural());
    }

    /**
     * Returns a {@code TopSelector} that collects the lowest {@code k} elements added to it,
     * relative to the specified comparator, and returns them via {@link #topK} in ascending order.
     *
     * @throws IllegalArgumentException if {@code k < 0} or {@code k > Integer.MAX_VALUE / 2}
     */
    public static <T extends Object> TopSelector<T> least(
            int k, Comparator<? super T> comparator) {
        return new TopSelector<T>(comparator, k);
    }

    /**
     * Returns a {@code TopSelector} that collects the greatest {@code k} elements added to it,
     * relative to the natural ordering of the elements, and returns them via {@link #topK} in
     * descending order.
     *
     * @throws IllegalArgumentException if {@code k < 0} or {@code k > Integer.MAX_VALUE / 2}
     */
    public static <T extends Comparable<? super T>> TopSelector<T> greatest(int k) {
        return greatest(k, Ordering.natural());
    }

    /**
     * Returns a {@code TopSelector} that collects the greatest {@code k} elements added to it,
     * relative to the specified comparator, and returns them via {@link #topK} in descending order.
     *
     * @throws IllegalArgumentException if {@code k < 0} or {@code k > Integer.MAX_VALUE / 2}
     */
    public static <T extends Object> TopSelector<T> greatest(
            int k, Comparator<? super T> comparator) {
        return new TopSelector<T>(Ordering.from(comparator).reverse(), k);
    }

    private final int k;
    private final Comparator<? super T> comparator;

    /**
     * We are currently considering the elements in buffer in the range [0, bufferSize) as candidates
     * for the top k elements. Whenever the buffer is filled, we quickselect the top k elements to the
     * range [0, k) and ignore the remaining elements.
     */
    private final T[] buffer;
    private int bufferSize;

    /**
     * The largest of the lowest k elements we've seen so far relative to this comparator. If
     * bufferSize ≥ k, then we can ignore any elements greater than this value.
     */
    private T threshold;

    private TopSelector(Comparator<? super T> comparator, int k) {
        this.comparator = checkNotNull(comparator, "comparator");
        this.k = k;
        checkArgument(k >= 0, "k (%s) must be >= 0", k);
        checkArgument(k <= Integer.MAX_VALUE / 2, "k (%s) must be <= Integer.MAX_VALUE / 2", k);
        this.buffer = (T[]) new Object[IntMath.checkedMultiply(k, 2)];
        this.bufferSize = 0;
        this.threshold = null;
    }

    /**
     * Adds {@code elem} as a candidate for the top {@code k} elements. This operation takes amortized
     * O(1) time.
     */
    public void offer(T elem) {
        if (k == 0) {
            return;
        } else if (bufferSize == 0) {
            buffer[0] = elem;
            threshold = elem;
            bufferSize = 1;
        } else if (bufferSize < k) {
            buffer[bufferSize++] = elem;
            // uncheckedCastNullableTToT is safe because bufferSize > 0.
            if (comparator.compare(elem, uncheckedCastNullableToT(threshold)) > 0) {
                threshold = elem;
            }
            // uncheckedCastNullableTToT is safe because bufferSize > 0.
        } else if (comparator.compare(elem, uncheckedCastNullableToT(threshold)) < 0) {
            // Otherwise, we can ignore elem; we've seen k better elements.
            buffer[bufferSize++] = elem;
            if (bufferSize == NUM_2 * k) {
                trim();
            }
        }
    }

    /**
     * Quickselects the top k elements from the 2k elements in the buffer. O(k) expected time, O(k log
     * k) worst case.
     */
    private void trim() {
        int left = 0;
        int right = 2 * k - 1;

        int minThresholdPosition = 0;
        // The leftmost position at which the greatest of the k lower elements
        // -- the new value of threshold -- might be found.

        int iterations = 0;
        int maxIterations = IntMath.log2(right - left, RoundingMode.CEILING) * 3;
        while (left < right) {
            int pivotIndex = (left + right + 1) >>> 1;

            int pivotNewIndex = partition(left, right, pivotIndex);

            if (pivotNewIndex > k) {
                right = pivotNewIndex - 1;
            } else if (pivotNewIndex < k) {
                left = Math.max(pivotNewIndex, left + 1);
                minThresholdPosition = pivotNewIndex;
            } else {
                break;
            }
            iterations++;
            if (iterations >= maxIterations) {
                @SuppressWarnings("nullness") // safe because we pass sort() a range that contains real Ts
                T[] castBuffer = (T[]) buffer;
                // We've already taken O(k log k), let's make sure we don't take longer than O(k log k).
                Arrays.sort(castBuffer, left, right + 1, comparator);
                break;
            }
        }
        bufferSize = k;

        threshold = uncheckedCastNullableToT(buffer[minThresholdPosition]);
        for (int i = minThresholdPosition + 1; i < k; i++) {
            if (comparator.compare(
                    uncheckedCastNullableToT(buffer[i]), uncheckedCastNullableToT(threshold))
                    > 0) {
                threshold = buffer[i];
            }
        }
    }

    /**
     * Partitions the contents of buffer in the range [left, right] around the pivot element
     * previously stored in buffer[pivotValue]. Returns the new index of the pivot element,
     * pivotNewIndex, so that everything in [left, pivotNewIndex] is ≤ pivotValue and everything in
     * (pivotNewIndex, right] is greater than pivotValue.
     */
    private int partition(int left, int right, int pivotIndex) {
        T pivotValue = uncheckedCastNullableToT(buffer[pivotIndex]);
        buffer[pivotIndex] = buffer[right];

        int pivotNewIndex = left;
        for (int i = left; i < right; i++) {
            if (comparator.compare(uncheckedCastNullableToT(buffer[i]), pivotValue) < 0) {
                swap(pivotNewIndex, i);
                pivotNewIndex++;
            }
        }
        buffer[right] = buffer[pivotNewIndex];
        buffer[pivotNewIndex] = pivotValue;
        return pivotNewIndex;
    }

    private void swap(int i, int j) {
        T tmp = buffer[i];
        buffer[i] = buffer[j];
        buffer[j] = tmp;
    }

    TopSelector<T> combine(TopSelector<T> other) {
        for (int i = 0; i < other.bufferSize; i++) {
            this.offer(uncheckedCastNullableToT(other.buffer[i]));
        }
        return this;
    }

    /**
     * Adds each member of {@code elements} as a candidate for the top {@code k} elements. This
     * operation takes amortized linear time in the length of {@code elements}.
     *
     * <p>If all input data to this {@code TopSelector} is in a single {@code Iterable}, prefer
     * {@link Ordering#leastOf(Iterable, int)}, which provides a simpler API for that use case.
     */
    public void offerAll(Iterable<? extends T> elements) {
        offerAll(elements.iterator());
    }

    /**
     * Adds each member of {@code elements} as a candidate for the top {@code k} elements. This
     * operation takes amortized linear time in the length of {@code elements}. The iterator is
     * consumed after this operation completes.
     *
     * <p>If all input data to this {@code TopSelector} is in a single {@code Iterator}, prefer
     * {@link Ordering#leastOf(Iterator, int)}, which provides a simpler API for that use case.
     */
    public void offerAll(Iterator<? extends T> elements) {
        while (elements.hasNext()) {
            offer(elements.next());
        }
    }

    /**
     * Returns the top {@code k} elements offered to this {@code TopSelector}, or all elements if
     * fewer than {@code k} have been offered, in the order specified by the factory used to create
     * this {@code TopSelector}.
     *
     * <p>The returned list is an unmodifiable copy and will not be affected by further changes to
     * this {@code TopSelector}. This method returns in O(k log k) time.
     */
    public List<T> topK() {
        @SuppressWarnings("nullness") // safe because we pass sort() a range that contains real Ts
        T[] castBuffer = (T[]) buffer;
        Arrays.sort(castBuffer, 0, bufferSize, comparator);
        if (bufferSize > k) {
            Arrays.fill(buffer, k, buffer.length, null);
            bufferSize = k;
            threshold = buffer[k - 1];
        }
        // we have to support null elements, so no ImmutableList for us
        return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(buffer, bufferSize)));
    }
}
