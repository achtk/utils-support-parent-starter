package com.chua.common.support.range;

import com.chua.common.support.lang.math.IntMath;
import com.chua.common.support.lang.math.LongMath;

import java.util.Comparator;

/**
 * ComparisonChain
 *
 * @author CH
 */
public abstract class BaseComparisonChain {
    private BaseComparisonChain() {
    }

    /**
     * Begins a new chained comparison statement. See example in the class documentation.
     */
    public static BaseComparisonChain start() {
        return ACTIVE;
    }

    private static final BaseComparisonChain ACTIVE =
            new BaseComparisonChain() {
                @SuppressWarnings("unchecked") // unsafe; see discussion on supertype
                @Override
                public BaseComparisonChain compare(Comparable<?> left, Comparable<?> right) {
                    return classify(((Comparable<Object>) left).compareTo(right));
                }

                @Override
                public <T extends Object> BaseComparisonChain compare(
                        T left, T right, Comparator<T> comparator) {
                    return classify(comparator.compare(left, right));
                }

                @Override
                public BaseComparisonChain compare(int left, int right) {
                    return classify(IntMath.compare(left, right));
                }

                @Override
                public BaseComparisonChain compare(long left, long right) {
                    return classify(LongMath.compare(left, right));
                }

                @Override
                public BaseComparisonChain compare(float left, float right) {
                    return classify(Float.compare(left, right));
                }

                @Override
                public BaseComparisonChain compare(double left, double right) {
                    return classify(Double.compare(left, right));
                }

                @Override
                public BaseComparisonChain compareTrueFirst(boolean left, boolean right) {
                    return classify(AbstractCut.compare(right, left));
                }

                @Override
                public BaseComparisonChain compareFalseFirst(boolean left, boolean right) {
                    return classify(AbstractCut.compare(left, right));
                }

                BaseComparisonChain classify(int result) {
                    return (result < 0) ? LESS : (result > 0) ? GREATER : ACTIVE;
                }

                @Override
                public int result() {
                    return 0;
                }
            };

    private static final BaseComparisonChain LESS = new InactiveComparisonChain(-1);

    private static final BaseComparisonChain GREATER = new InactiveComparisonChain(1);

    private static final class InactiveComparisonChain extends BaseComparisonChain {
        final int result;

        InactiveComparisonChain(int result) {
            this.result = result;
        }

        @Override
        public BaseComparisonChain compare(Comparable<?> left, Comparable<?> right) {
            return this;
        }

        @Override
        public <T extends Object> BaseComparisonChain compare(
                T left, T right, Comparator<T> comparator) {
            return this;
        }

        @Override
        public BaseComparisonChain compare(int left, int right) {
            return this;
        }

        @Override
        public BaseComparisonChain compare(long left, long right) {
            return this;
        }

        @Override
        public BaseComparisonChain compare(float left, float right) {
            return this;
        }

        @Override
        public BaseComparisonChain compare(double left, double right) {
            return this;
        }

        @Override
        public BaseComparisonChain compareTrueFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public BaseComparisonChain compareFalseFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public int result() {
            return result;
        }
    }

    /**
     * Compares two comparable objects as specified by {@link Comparable#compareTo}, <i>if</i> the
     * result of this comparison chain has not already been determined.
     *
     * <p>This method is declared to accept any 2 {@code Comparable} objects, even if they are not <a
     * href="https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html">mutually
     * comparable</a>. If you pass objects that are not mutually comparable, this method may throw an
     * exception. (The reason for this decision is lost to time, but the reason <i>might</i> be that
     * we wanted to support legacy classes that implement the raw type {@code Comparable} (instead of
     * implementing {@code Comparable<Foo>}) without producing warnings. If so, we would prefer today
     * to produce warnings in that case, and we may change this method to do so in the future. Support
     * for raw {@code Comparable} types in Guava in general is tracked as <a
     * href="https://github.com/google/guava/issues/989">#989</a>.)
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     * @throws ClassCastException if the parameters are not mutually comparable
     */
    public abstract BaseComparisonChain compare(Comparable<?> left, Comparable<?> right);

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     *
     * @param left       Comparable
     * @param right      Comparable
     * @param comparator comparator
     * @return ComparisonChain
     */
    public abstract <T extends Object> BaseComparisonChain compare(
            T left, T right, Comparator<T> comparator);

    /**
     * Compares two {@code int} values , <i>if</i> the result of
     * this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     */
    public abstract BaseComparisonChain compare(int left, int right);

    /**
     * Compares two {@code long} values , <i>if</i> the result of
     * this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     */
    public abstract BaseComparisonChain compare(long left, long right);

    /**
     * Compares two {@code float} values as specified by {@link Float#compare}, <i>if</i> the result
     * of this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     */
    public abstract BaseComparisonChain compare(float left, float right);

    /**
     * Compares two {@code double} values as specified by {@link Double#compare}, <i>if</i> the result
     * of this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     */
    public abstract BaseComparisonChain compare(double left, double right);

    /**
     * Discouraged synonym for {@link #compareFalseFirst}.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     * @since 19.0
     * @deprecated Use {@link #compareFalseFirst}; or, if the parameters passed are being either
     * negated or reversed, undo the negation or reversal and use {@link #compareTrueFirst}.
     */
    @Deprecated
    public final BaseComparisonChain compare(Boolean left, Boolean right) {
        return compareFalseFirst(left, right);
    }

    /**
     * Compares two {@code boolean} values, considering {@code true} to be less than {@code false},
     * <i>if</i> the result of this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     * @since 12.0
     */
    public abstract BaseComparisonChain compareTrueFirst(boolean left, boolean right);

    /**
     * Compares two {@code boolean} values, considering {@code false} to be less than {@code true},
     * <i>if</i> the result of this comparison chain has not already been determined.
     *
     * @param left  Comparable
     * @param right Comparable
     * @return ComparisonChain
     * @since 12.0 (present as {@code compare} since 2.0)
     */
    public abstract BaseComparisonChain compareFalseFirst(boolean left, boolean right);

    /**
     * Ends this comparison chain and returns its result: a value having the same sign as the first
     * nonzero comparison result in the chain, or zero if every result was zero.
     *
     * @return result
     */
    public abstract int result();
}
