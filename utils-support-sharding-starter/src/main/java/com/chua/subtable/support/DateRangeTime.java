package com.chua.subtable.support;

import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.lang.date.range.DateRange;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.time.LocalDateTime;

/**
 * 范围
 * (a..b) {x | a < x < b} open
 * [a..b] {x | a <= x <= b} closed
 * (a..b] {x | a < x <= b} openClosed
 * [a..b) {x | a <= x < b} closedOpen
 * (a..+∞) {x | x > a} greaterThan
 * [a..+∞) {x | x >= a} atLeast
 * (-∞..b) {x | x < b} lessThan
 * (-∞..b] {x | x <= b} atMost
 * (-∞..+∞) {x} all
 *
 * @author CH
 */
public class DateRangeTime implements DateRange {

    private final Range<LocalDateTime> valueRange;

    /**
     * 初始化
     *
     * @param valueRange 时间区间
     */
    public DateRangeTime(Range<LocalDateTime> valueRange) {
        this.valueRange = valueRange;
    }

    /**
     * 初始化
     *
     * @param valueRange 时间区间
     * @return this
     */
    public static DateRangeTime of(Range<LocalDateTime> valueRange) {
        return new DateRangeTime(valueRange);
    }


    /**
     * 初始化
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return this
     */
    public static DateRangeTime of(LocalDateTime startTime, LocalDateTime endTime) {
        return new DateRangeTime(Range.closed(startTime, endTime));
    }

    /**
     * 初始化
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return this
     */
    public static DateRangeTime openClosed(LocalDateTime startTime, LocalDateTime endTime) {
        return new DateRangeTime(Range.openClosed(startTime, endTime));
    }

    /**
     * 初始化
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return this
     */
    public static DateRangeTime open(LocalDateTime startTime, LocalDateTime endTime) {
        return new DateRangeTime(Range.open(startTime, endTime));
    }

    /**
     * 初始化
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return this
     */
    public static DateRangeTime closed(LocalDateTime startTime, LocalDateTime endTime) {
        return new DateRangeTime(Range.closed(startTime, endTime));
    }

    /**
     * 初始化
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return this
     */
    public static DateRangeTime closedOpen(LocalDateTime startTime, LocalDateTime endTime) {
        return new DateRangeTime(Range.closedOpen(startTime, endTime));
    }

    /**
     * 初始化
     *
     * @return this
     */
    public static DateRangeTime all() {
        return new DateRangeTime(Range.all());
    }

    /**
     * 初始化
     *
     * @param dateTime 开始时间
     * @return this
     */
    public static DateRangeTime greaterThan(LocalDateTime dateTime) {
        return new DateRangeTime(Range.greaterThan(dateTime));
    }

    /**
     * 初始化
     *
     * @param dateTime 开始时间
     * @return this
     */
    public static DateRangeTime lessThan(LocalDateTime dateTime) {
        return new DateRangeTime(Range.lessThan(dateTime));
    }

    /**
     * 初始化
     *
     * @param dateTime 开始时间
     * @return this
     */
    public static DateRangeTime atLeast(LocalDateTime dateTime) {
        return new DateRangeTime(Range.atLeast(dateTime));
    }

    /**
     * 初始化
     *
     * @param dateTime 开始时间
     * @return this
     */
    public static DateRangeTime atMost(LocalDateTime dateTime) {
        return new DateRangeTime(Range.atMost(dateTime));
    }

    /**
     * 初始化
     *
     * @param valueRange 时间区间
     * @return this
     */
    public static DateRangeTime ofStr(Range<String> valueRange) {
        if (valueRange.hasLowerBound() && valueRange.hasUpperBound()) {
            return of(Range.range(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime(), valueRange.lowerBoundType(), DateTime.of(valueRange.upperEndpoint()).toLocalDateTime(), valueRange.upperBoundType()));
        }

        if (valueRange.hasLowerBound()) {
            return of(valueRange.lowerBoundType() == BoundType.CLOSED ? Range.atLeast(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime()) : Range.greaterThan(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime()));
        }

        if (valueRange.hasUpperBound()) {
            return of(valueRange.upperBoundType() == BoundType.CLOSED ? Range.atMost(DateTime.of(valueRange.upperEndpoint()).toLocalDateTime()) : Range.lessThan(DateTime.of(valueRange.upperEndpoint()).toLocalDateTime()));
        }

        return of(Range.all());

    }

    @Override
    public Range<LocalDateTime> range() {
        return valueRange;
    }

    @Override
    public String toString() {
        return valueRange.toString();
    }
}
