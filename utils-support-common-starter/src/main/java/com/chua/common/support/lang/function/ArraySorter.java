package com.chua.common.support.lang.function;


import java.util.Arrays;
import java.util.Comparator;

/**
 * Sorts and returns arrays in the fluent style.
 * @author Administrator
 * @since 3.12.0
 */
public class ArraySorter {

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(byte[])
     */
    public static byte[] sort(final byte[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(char[])
     */
    public static char[] sort(final char[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(double[])
     */
    public static double[] sort(final double[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(float[])
     */
    public static float[] sort(final float[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(int[])
     */
    public static int[] sort(final int[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(long[])
     */
    public static long[] sort(final long[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(short[])
     */
    public static short[] sort(final short[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param <T>   the array type.
     * @param array the array to sort.
     * @return the given array.
     * @see Arrays#sort(Object[])
     */
    public static <T> T[] sort(final T[] array) {
        Arrays.sort(array);
        return array;
    }

    /**
     * Sorts and returns the given array.
     *
     * @param <T>        the array type.
     * @param array      the array to sort.
     * @param comparator the comparator to determine the order of the array. A {@code null} value uses the elements'
     *                   {@link Comparable natural ordering}.
     * @return the given array.
     * @see Arrays#sort(Object[])
     */
    public static <T> T[] sort(final T[] array, final Comparator<? super T> comparator) {
        Arrays.sort(array, comparator);
        return array;
    }

}
