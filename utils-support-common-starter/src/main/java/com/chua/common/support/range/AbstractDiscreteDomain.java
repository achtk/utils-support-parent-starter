package com.chua.common.support.range;

import com.chua.common.support.lang.math.IntMath;
import com.chua.common.support.utils.Preconditions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * DiscreteDomain
 * @author CH
 */
public abstract class AbstractDiscreteDomain<C extends Comparable> {

    /**
     * Returns the discrete domain for values of type {@code Integer}.
     *
     * @since 14.0 (since 10.0 as {@code DiscreteDomains.integers()})
     */
    public static AbstractDiscreteDomain<Integer> integers() {
        return IntegerDomainAbstract.INSTANCE;
    }

    private static final class IntegerDomainAbstract extends AbstractDiscreteDomain<Integer> implements Serializable {
        private static final IntegerDomainAbstract INSTANCE = new IntegerDomainAbstract();

        IntegerDomainAbstract() {
            super(true);
        }

        @Override
        public Integer next(Integer value) {
            int i = value;
            return (i == Integer.MAX_VALUE) ? null : i + 1;
        }

        @Override
        public Integer previous(Integer value) {
            int i = value;
            return (i == Integer.MIN_VALUE) ? null : i - 1;
        }

        @Override
        Integer offset(Integer origin, long distance) {
            Preconditions.checkNonNegative(distance, "distance");
            return IntMath.checkedCast(origin.longValue() + distance);
        }

        @Override
        public long distance(Integer start, Integer end) {
            return (long) end - start;
        }

        @Override
        public Integer minValue() {
            return Integer.MIN_VALUE;
        }

        @Override
        public Integer maxValue() {
            return Integer.MAX_VALUE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.integers()";
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Returns the discrete domain for values of type {@code Long}.
     *
     * @since 14.0 (since 10.0 as {@code DiscreteDomains.longs()})
     */
    public static AbstractDiscreteDomain<Long> longs() {
        return LongDomainAbstract.INSTANCE;
    }

    private static final class LongDomainAbstract extends AbstractDiscreteDomain<Long> implements Serializable {
        private static final LongDomainAbstract INSTANCE = new LongDomainAbstract();

        LongDomainAbstract() {
            super(true);
        }

        @Override
        public Long next(Long value) {
            long l = value;
            return (l == Long.MAX_VALUE) ? null : l + 1;
        }

        @Override
        public Long previous(Long value) {
            long l = value;
            return (l == Long.MIN_VALUE) ? null : l - 1;
        }

        @Override
        Long offset(Long origin, long distance) {
            Preconditions.checkNonNegative(distance, "distance");
            long result = origin + distance;
            if (result < 0) {
                Preconditions.checkArgument(origin < 0, "overflow");
            }
            return result;
        }

        @Override
        public long distance(Long start, Long end) {
            long result = end - start;
            if (end > start && result < 0) {
                return Long.MAX_VALUE;
            }
            if (end < start && result > 0) {
                return Long.MIN_VALUE;
            }
            return result;
        }

        @Override
        public Long minValue() {
            return Long.MIN_VALUE;
        }

        @Override
        public Long maxValue() {
            return Long.MAX_VALUE;
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.longs()";
        }

        private static final long serialVersionUID = 0;
    }

    /**
     * Returns the discrete domain for values of type {@code BigInteger}.
     *
     * @since 15.0
     */
    public static AbstractDiscreteDomain<BigInteger> bigIntegers() {
        return BigIntegerDomainAbstract.INSTANCE;
    }

    private static final class BigIntegerDomainAbstract extends AbstractDiscreteDomain<BigInteger>
            implements Serializable {
        private static final BigIntegerDomainAbstract INSTANCE = new BigIntegerDomainAbstract();

        BigIntegerDomainAbstract() {
            super(true);
        }

        private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

        @Override
        public BigInteger next(BigInteger value) {
            return value.add(BigInteger.ONE);
        }

        @Override
        public BigInteger previous(BigInteger value) {
            return value.subtract(BigInteger.ONE);
        }

        @Override
        BigInteger offset(BigInteger origin, long distance) {
            Preconditions.checkNonNegative(distance, "distance");
            return origin.add(BigInteger.valueOf(distance));
        }

        @Override
        public long distance(BigInteger start, BigInteger end) {
            return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
        }

        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "DiscreteDomain.bigIntegers()";
        }

        private static final long serialVersionUID = 0;
    }

    final boolean supportsFastOffset;

    /** Constructor for use by subclasses. */
    protected AbstractDiscreteDomain() {
        this(false);
    }

    /** Private constructor for built-in DiscreteDomains supporting fast offset. */
    private AbstractDiscreteDomain(boolean supportsFastOffset) {
        this.supportsFastOffset = supportsFastOffset;
    }

    /**
     * Returns, conceptually, "origin + distance", or equivalently, the result of calling {@link
     * #next} on {@code origin} {@code distance} times.
     */
    C offset(C origin, long distance) {
        C current = origin;
        Preconditions.checkNonNegative(distance, "distance");
        for (long i = 0; i < distance; i++) {
            current = next(current);
            if (current == null) {
                throw new IllegalArgumentException(
                        "overflowed computing offset(" + origin + ", " + distance + ")");
            }
        }
        return current;
    }

    /**
     * Returns the unique least value of type {@code C} that is greater than {@code value}, or {@code
     * null} if none exists. Inverse operation to {@link #previous}.
     *
     * @param value any value of type {@code C}
     * @return the least value greater than {@code value}, or {@code null} if {@code value} is {@code
     *     maxValue()}
     */
    public abstract C next(C value);

    /**
     * Returns the unique greatest value of type {@code C} that is less than {@code value}, or {@code
     * null} if none exists. Inverse operation to {@link #next}.
     *
     * @param value any value of type {@code C}
     * @return the greatest value less than {@code value}, or {@code null} if {@code value} is {@code
     *     minValue()}
     */
    public abstract C previous(C value);

    /**
     * Returns a signed value indicating how many nested invocations of {@link #next} (if positive) or
     * {@link #previous} (if negative) are needed to reach {@code end} starting from {@code start}.
     * For example, if {@code end = next(next(next(start)))}, then {@code distance(start, end) == 3}
     * and {@code distance(end, start) == -3}. As well, {@code distance(a, a)} is always zero.
     *
     * <p>Note that this function is necessarily well-defined for any discrete type.
     *
     * @param start start
     * @param end end
     * @return the distance as described above, or {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} if
     *     the distance is too small or too large, respectively.
     */
    public abstract long distance(C start, C end);

    /**
     * Returns the minimum value of type {@code C}, if it has one. The minimum value is the unique
     * value for which {@link Comparable#compareTo(Object)} never returns a positive value for any
     * input of type {@code C}.
     *
     * <p>The default implementation throws {@code NoSuchElementException}.
     * @return the minimum value of type {@code C}; never null
     * @throws NoSuchElementException if the type has no (practical) minimum value; for example,
     *     {@link java.math.BigInteger}
     */
    public C minValue() {
        throw new NoSuchElementException();
    }

    /**
     * Returns the maximum value of type {@code C}, if it has one. The maximum value is the unique
     * value for which {@link Comparable#compareTo(Object)} never returns a negative value for any
     * input of type {@code C}.
     *
     * <p>The default implementation throws {@code NoSuchElementException}.
     *
     * @return the maximum value of type {@code C}; never null
     * @throws NoSuchElementException if the type has no (practical) maximum value; for example,
     *     {@link java.math.BigInteger}
     */
    public C maxValue() {
        throw new NoSuchElementException();
    }
}
