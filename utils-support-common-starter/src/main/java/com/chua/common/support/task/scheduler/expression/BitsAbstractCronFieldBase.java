package com.chua.common.support.task.scheduler.expression;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.utils.StringUtils;

import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.ValueRange;

/**
 * bits
 * <p>spring-content</p>
 *
 * @author CH
 * @since 2022-02-15
 */
final class BitsAbstractCronFieldBase extends AbstractCronFieldBase {

    private static final long MASK = 0xFFFFFFFFFFFFFFFFL;

    private static BitsAbstractCronFieldBase zeroNanos = null;


    private long bits;


    private BitsAbstractCronFieldBase(Type type) {
        super(type);
    }

    /**
     * Return a {@code BitsCronField} enabled for 0 nano seconds.
     */
    public static BitsAbstractCronFieldBase zeroNanos() {
        if (zeroNanos == null) {
            BitsAbstractCronFieldBase field = new BitsAbstractCronFieldBase(Type.NANO);
            field.setBit(0);
            zeroNanos = field;
        }
        return zeroNanos;
    }

    /**
     * Parse the given value into a seconds {@code BitsCronField}, the first entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseSeconds(String value) {
        return parseField(value, Type.SECOND);
    }

    /**
     * Parse the given value into a minutes {@code BitsCronField}, the second entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseMinutes(String value) {
        return BitsAbstractCronFieldBase.parseField(value, Type.MINUTE);
    }

    /**
     * Parse the given value into a hours {@code BitsCronField}, the third entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseHours(String value) {
        return BitsAbstractCronFieldBase.parseField(value, Type.HOUR);
    }

    /**
     * Parse the given value into a days of months {@code BitsCronField}, the fourth entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseDaysOfMonth(String value) {
        return parseDate(value, Type.DAY_OF_MONTH);
    }

    /**
     * Parse the given value into a month {@code BitsCronField}, the fifth entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseMonth(String value) {
        return BitsAbstractCronFieldBase.parseField(value, Type.MONTH);
    }

    /**
     * Parse the given value into a days of week {@code BitsCronField}, the sixth entry of a cron expression.
     */
    public static BitsAbstractCronFieldBase parseDaysOfWeek(String value) {
        BitsAbstractCronFieldBase result = parseDate(value, Type.DAY_OF_WEEK);
        if (result.getBit(0)) {
            // cron supports 0 for Sunday; we use 7 like java.time
            result.setBit(7);
            result.clearBit(0);
        }
        return result;
    }


    private static BitsAbstractCronFieldBase parseDate(String value, Type type) {
        if (CommonConstant.SYMBOL_QUESTION.equals(value)) {
            value = CommonConstant.SYMBOL_ASTERISK;
        }
        return BitsAbstractCronFieldBase.parseField(value, type);
    }

    private static BitsAbstractCronFieldBase parseField(String value, Type type) {
        try {
            BitsAbstractCronFieldBase result = new BitsAbstractCronFieldBase(type);
            String[] fields = StringUtils.delimitedListToStringArray(value, ",");
            for (String field : fields) {
                int slashPos = field.indexOf('/');
                if (slashPos == -1) {
                    ValueRange range = parseRange(field, type);
                    result.setBits(range);
                } else {
                    String rangeStr = field.substring(0, slashPos);
                    String deltaStr = field.substring(slashPos + 1);
                    ValueRange range = parseRange(rangeStr, type);
                    if (rangeStr.indexOf('-') == -1) {
                        range = ValueRange.of(range.getMinimum(), type.range().getMaximum());
                    }
                    int delta = Integer.parseInt(deltaStr);
                    if (delta <= 0) {
                        throw new IllegalArgumentException("Incrementer delta must be 1 or higher");
                    }
                    result.setBits(range, delta);
                }
            }
            return result;
        } catch (DateTimeException | IllegalArgumentException ex) {
            String msg = ex.getMessage() + " '" + value + "'";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    private static ValueRange parseRange(String value, Type type) {
        if (CommonConstant.SYMBOL_ASTERISK.equals(value)) {
            return type.range();
        } else {
            int hyphenPos = value.indexOf('-');
            if (hyphenPos == -1) {
                int result = type.checkValidValue(Integer.parseInt(value));
                return ValueRange.of(result, result);
            } else {
                int min = Integer.parseInt(value.substring(0, hyphenPos));
                int max = Integer.parseInt(value.substring(hyphenPos + 1));
                min = type.checkValidValue(min);
                max = type.checkValidValue(max);
                if (type == Type.DAY_OF_WEEK && min == NumberConstant.SEVEN) {
                    // If used as a minimum in a range, Sunday means 0 (not 7)
                    min = 0;
                }
                return ValueRange.of(min, max);
            }
        }
    }

    @Override
    public <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        int current = type().get(temporal);
        int next = nextSetBit(current);
        if (next == -1) {
            temporal = type().rollForward(temporal);
            next = nextSetBit(0);
        }
        if (next == current) {
            return temporal;
        } else {
            int count = 0;
            current = type().get(temporal);
            while (current != next && count++ < Cron2Expression.MAX_ATTEMPTS) {
                temporal = type().elapseUntil(temporal, next);
                current = type().get(temporal);
                next = nextSetBit(current);
                if (next == -1) {
                    temporal = type().rollForward(temporal);
                    next = nextSetBit(0);
                }
            }
            if (count >= Cron2Expression.MAX_ATTEMPTS) {
                return null;
            }
            return type().reset(temporal);
        }
    }

    boolean getBit(int index) {
        return (this.bits & (1L << index)) != 0;
    }

    private int nextSetBit(int fromIndex) {
        long result = this.bits & (MASK << fromIndex);
        if (result != 0) {
            return Long.numberOfTrailingZeros(result);
        } else {
            return -1;
        }

    }

    private void setBits(ValueRange range) {
        if (range.getMinimum() == range.getMaximum()) {
            setBit((int) range.getMinimum());
        } else {
            long minMask = MASK << range.getMinimum();
            long maxMask = MASK >>> -(range.getMaximum() + 1);
            this.bits |= (minMask & maxMask);
        }
    }

    private void setBits(ValueRange range, int delta) {
        if (delta == 1) {
            setBits(range);
        } else {
            for (int i = (int) range.getMinimum(); i <= range.getMaximum(); i += delta) {
                setBit(i);
            }
        }
    }

    private void setBit(int index) {
        this.bits |= (1L << index);
    }

    private void clearBit(int index) {
        this.bits &= ~(1L << index);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.bits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitsAbstractCronFieldBase)) {
            return false;
        }
        BitsAbstractCronFieldBase other = (BitsAbstractCronFieldBase) o;
        return type() == other.type() && this.bits == other.bits;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(type().toString());
        builder.append(" {");
        int i = nextSetBit(0);
        if (i != -1) {
            builder.append(i);
            i = nextSetBit(i + 1);
            while (i != -1) {
                builder.append(", ");
                builder.append(i);
                i = nextSetBit(i + 1);
            }
        }
        builder.append('}');
        return builder.toString();
    }

}
