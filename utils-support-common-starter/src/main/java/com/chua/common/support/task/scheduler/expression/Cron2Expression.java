package com.chua.common.support.task.scheduler.expression;

import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.utils.StringUtils;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;

/**
 * cron
 *
 * @author CH
 * @since 2022-02-15
 */
public class Cron2Expression {

    static final int MAX_ATTEMPTS = 366;

    private static final String[] MACROS = new String[]{
            "@yearly", "0 0 0 1 1 *",
            "@annually", "0 0 0 1 1 *",
            "@monthly", "0 0 0 1 * *",
            "@weekly", "0 0 0 * * 0",
            "@daily", "0 0 0 * * *",
            "@midnight", "0 0 0 * * *",
            "@hourly", "0 0 * * * *"
    };


    private final AbstractCronFieldBase[] fields;

    private final String expression;


    private Cron2Expression(
            AbstractCronFieldBase seconds,
            AbstractCronFieldBase minutes,
            AbstractCronFieldBase hours,
            AbstractCronFieldBase daysOfMonth,
            AbstractCronFieldBase months,
            AbstractCronFieldBase daysOfWeek,
            String expression) {

        // to make sure we end up at 0 nanos, we add an extra field
        this.fields = new AbstractCronFieldBase[]{AbstractCronFieldBase.zeroNanos(), seconds, minutes, hours, daysOfMonth, months, daysOfWeek};
        this.expression = expression;
    }


    /**
     * Parse the given
     * <a href="https://www.manpagez.com/man/5/crontab/">crontab expression</a>
     * string into a {@code CronExpression}.
     * The string has six single space-separated time and date fields:
     * <pre>
     * &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; second (0-59)
     * &#9474; &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; minute (0 - 59)
     * &#9474; &#9474; &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; hour (0 - 23)
     * &#9474; &#9474; &#9474; &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; day of the month (1 - 31)
     * &#9474; &#9474; &#9474; &#9474; &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; month (1 - 12) (or JAN-DEC)
     * &#9474; &#9474; &#9474; &#9474; &#9474; &#9484;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472;&#9472; day of the week (0 - 7)
     * &#9474; &#9474; &#9474; &#9474; &#9474; &#9474;          (0 or 7 is Sunday, or MON-SUN)
     * &#9474; &#9474; &#9474; &#9474; &#9474; &#9474;
     * &#42; &#42; &#42; &#42; &#42; &#42;
     * </pre>
     *
     * <p>The following rules apply:
     * <ul>
     * <li>
     * A field may be an asterisk ({@code *}), which always stands for
     * "first-last". For the "day of the month" or "day of the week" fields, a
     * question mark ({@code ?}) may be used instead of an asterisk.
     * </li>
     * <li>
     * Ranges of numbers are expressed by two numbers separated with a hyphen
     * ({@code -}). The specified range is inclusive.
     * </li>
     * <li>Following a range (or {@code *}) with {@code /n} specifies
     * the interval of the number's value through the range.
     * </li>
     * <li>
     * English names can also be used for the "month" and "day of week" fields.
     * Use the first three letters of the particular day or month (case does not
     * matter).
     * </li>
     * <li>
     * The "day of month" and "day of week" fields can contain a
     * {@code L}-character, which stands for "last", and has a different meaning
     * in each field:
     * <ul>
     * <li>
     * In the "day of month" field, {@code L} stands for "the last day of the
     * month". If followed by an negative offset (i.e. {@code L-n}), it means
     * "{@code n}th-to-last day of the month". If followed by {@code W} (i.e.
     * {@code LW}), it means "the last weekday of the month".
     * </li>
     * <li>
     * In the "day of week" field, {@code L} stands for "the last day of the
     * week".
     * If prefixed by a number or three-letter name (i.e. {@code dL} or
     * {@code DDDL}), it means "the last day of week {@code d} (or {@code DDD})
     * in the month".
     * </li>
     * </ul>
     * </li>
     * <li>
     * The "day of month" field can be {@code nW}, which stands for "the nearest
     * weekday to day of the month {@code n}".
     * If {@code n} falls on Saturday, this yields the Friday before it.
     * If {@code n} falls on Sunday, this yields the Monday after,
     * which also happens if {@code n} is {@code 1} and falls on a Saturday
     * (i.e. {@code 1W} stands for "the first weekday of the month").
     * </li>
     * <li>
     * The "day of week" field can be {@code d#n} (or {@code DDD#n}), which
     * stands for "the {@code n}-th day of week {@code d} (or {@code DDD}) in
     * the month".
     * </li>
     * </ul>
     *
     * <p>Example expressions:
     * <ul>
     * <li>{@code "0 0 * * * *"} = the top of every hour of every day.</li>
     * <li><code>"*&#47;10 * * * * *"</code> = every ten seconds.</li>
     * <li>{@code "0 0 8-10 * * *"} = 8, 9 and 10 o'clock of every day.</li>
     * <li>{@code "0 0 6,19 * * *"} = 6:00 AM and 7:00 PM every day.</li>
     * <li>{@code "0 0/30 8-10 * * *"} = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.</li>
     * <li>{@code "0 0 9-17 * * MON-FRI"} = on the hour nine-to-five weekdays</li>
     * <li>{@code "0 0 0 25 12 ?"} = every Christmas Day at midnight</li>
     * <li>{@code "0 0 0 L * *"} = last day of the month at midnight</li>
     * <li>{@code "0 0 0 L-3 * *"} = third-to-last day of the month at midnight</li>
     * <li>{@code "0 0 0 1W * *"} = first weekday of the month at midnight</li>
     * <li>{@code "0 0 0 LW * *"} = last weekday of the month at midnight</li>
     * <li>{@code "0 0 0 * * 5L"} = last Friday of the month at midnight</li>
     * <li>{@code "0 0 0 * * THUL"} = last Thursday of the month at midnight</li>
     * <li>{@code "0 0 0 ? * 5#2"} = the second Friday in the month at midnight</li>
     * <li>{@code "0 0 0 ? * MON#1"} = the first Monday in the month at midnight</li>
     * </ul>
     *
     * <p>The following macros are also supported:
     * <ul>
     * <li>{@code "@yearly"} (or {@code "@annually"}) to run un once a year, i.e. {@code "0 0 0 1 1 *"},</li>
     * <li>{@code "@monthly"} to run once a month, i.e. {@code "0 0 0 1 * *"},</li>
     * <li>{@code "@weekly"} to run once a week, i.e. {@code "0 0 0 * * 0"},</li>
     * <li>{@code "@daily"} (or {@code "@midnight"}) to run once a day, i.e. {@code "0 0 0 * * *"},</li>
     * <li>{@code "@hourly"} to run once an hour, i.e. {@code "0 0 * * * *"}.</li>
     * </ul>
     *
     * @param expression the expression string to parse
     * @return the parsed {@code CronExpression} object
     * @throws IllegalArgumentException in the expression does not conform to
     *                                  the cron format
     */
    public static Cron2Expression parse(String expression) {

        expression = resolveMacros(expression);

        String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (fields.length != NumberConstant.SIXTH) {
            throw new IllegalArgumentException(String.format(
                    "Cron expression must consist of 6 fields (found %d in \"%s\")", fields.length, expression));
        }
        try {
            AbstractCronFieldBase seconds = AbstractCronFieldBase.parseSeconds(fields[0]);
            AbstractCronFieldBase minutes = AbstractCronFieldBase.parseMinutes(fields[1]);
            AbstractCronFieldBase hours = AbstractCronFieldBase.parseHours(fields[2]);
            AbstractCronFieldBase daysOfMonth = AbstractCronFieldBase.parseDaysOfMonth(fields[3]);
            AbstractCronFieldBase months = AbstractCronFieldBase.parseMonth(fields[4]);
            AbstractCronFieldBase daysOfWeek = AbstractCronFieldBase.parseDaysOfWeek(fields[5]);

            return new Cron2Expression(seconds, minutes, hours, daysOfMonth, months, daysOfWeek, expression);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage() + " in cron expression \"" + expression + "\"";
            throw new IllegalArgumentException(msg, ex);
        }
    }

    /**
     * Determine whether the given string represents a valid cron expression.
     *
     * @param expression the expression to evaluate
     * @return {@code true} if the given expression is a valid cron expression
     * @since 5.3.8
     */
    public static boolean isValidExpression(String expression) {
        if (expression == null) {
            return false;
        }
        try {
            parse(expression);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }


    private static String resolveMacros(String expression) {
        expression = expression.trim();
        for (int i = 0; i < MACROS.length; i = i + NumberConstant.TWE) {
            if (MACROS[i].equalsIgnoreCase(expression)) {
                return MACROS[i + 1];
            }
        }
        return expression;
    }


    /**
     * Calculate the next {@link Temporal} that matches this expression.
     *
     * @param temporal the seed value
     * @param <T>      the type of temporal
     * @return the next temporal that matches this expression, or {@code null}
     * if no such temporal can be found
     */
    public <T extends Temporal & Comparable<? super T>> T next(T temporal) {
        return nextOrSame(ChronoUnit.NANOS.addTo(temporal, 1));
    }


    private <T extends Temporal & Comparable<? super T>> T nextOrSame(T temporal) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            T result = nextOrSameInternal(temporal);
            if (result == null || result.equals(temporal)) {
                return result;
            }
            temporal = result;
        }
        return null;
    }

    private <T extends Temporal & Comparable<? super T>> T nextOrSameInternal(T temporal) {
        for (AbstractCronFieldBase field : this.fields) {
            temporal = field.nextOrSame(temporal);
            if (temporal == null) {
                return null;
            }
        }
        return temporal;
    }


    @Override
    public int hashCode() {
        return Arrays.hashCode(this.fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Cron2Expression) {
            Cron2Expression other = (Cron2Expression) o;
            return Arrays.equals(this.fields, other.fields);
        } else {
            return false;
        }
    }

    /**
     * Return the expression string used to create this {@code CronExpression}.
     *
     * @return the expression string
     */
    @Override
    public String toString() {
        return this.expression;
    }

}
