package com.chua.common.support.utils;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.matcher.PathMatcher;
import com.chua.common.support.objects.definition.element.ParameterDescribe;

import java.lang.reflect.Array;
import java.util.*;

import static com.chua.common.support.constant.CommonConstant.INDEX_NOT_FOUND;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_ASTERISK;

/**
 * array
 *
 * @author CH
 */
public class ArrayUtils {
    /**
     * 是否匹配
     *
     * @param array 数组
     * @param name  名称
     * @return 是否匹配
     */
    public static boolean isMatch(String[] array, String name) {
        if (isEmpty(array)) {
            return false;
        }

        for (String s : array) {
            if (s.contains(SYMBOL_ASTERISK)) {
                if (PathMatcher.INSTANCE.match(s, name)) {
                    return true;
                }
                continue;
            }

            if (s.equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (!isEmpty(array)) {
            for (T element : array) {
                if (Objects.isNull(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取默认数组
     *
     * @param source       数组
     * @param defaultArray 默认数组
     * @param <T>          类型
     * @return 结果
     */
    public static <T> T[] defaultIfEmpty(T[] source, T[] defaultArray) {
        if (null == source || source.length == 0) {
            return defaultArray;
        }

        return source;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param length 拷贝数组长度
     * @return 目标数组
     * @since 3.0.6
     */
    public static <T> T copyRange(T src, int length) {
        int max = Array.getLength(src);
        T temp = null;
        if (max <= length) {
            temp = (T) Array.newInstance(Array.get(src, 0).getClass(), max);
            System.arraycopy(src, 0, temp, 0, max);
            return temp;
        }
        temp = (T) Array.newInstance(Array.get(src, 0).getClass(), length);
        System.arraycopy(src, 0, temp, 0, length);
        return temp;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     * @return 目标数组
     * @since 3.0.6
     */
    public static Object copy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 取得字符串数组的第一个元素
     *
     * @param stringArray  字符串数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 字符串数组的第一个元素
     * @see NullPointerException
     */
    public static <T> T getIndex(T[] stringArray, int index, T defaultValue) {
        return stringArray == null || stringArray.length == 0 || stringArray.length <= index ? defaultValue : stringArray[index];
    }

    /**
     * 取得字符串数组的第一个元素
     *
     * @param stringArray 字符串数组
     * @param index       索引
     * @return 字符串数组的第一个元素
     * @see NullPointerException
     */
    public static <T> T getIndex(T[] stringArray, int index) {
        return getIndex(stringArray, index, null);
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(byte[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (byte o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(long[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (long o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(float[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (float o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(double[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (double o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(short[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (short o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(int[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (int o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(boolean[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (boolean o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(Object[] value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, value.length);
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (Object o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }


    /**
     * 转化为 T[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> T[] transToArray(List value, Class<T> type) {
        T[] newInstance = (T[]) Array.newInstance(type, null == value ? 0 : value.size());
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (Object o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, type);
        }
        return newInstance;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 判断字符串数组是否包含指定的字符串
     *
     * @param array 字符串数组
     * @param str   指定的字符串
     * @return 包含true，否则false
     */
    public static <T> boolean contains(T[] array, T str) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (T item : array) {
            if (null == item && null == str) {
                return true;
            }

            if (null == str) {
                return false;
            }

            if (null != item && item.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串数组是否包含指定的字符串
     *
     * @param array 字符串数组
     * @param str   指定的字符串
     * @return 包含true，否则false
     */
    public static <T> boolean containsIgnoreCase(T[] array, T str) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (T item : array) {
            if (null == item && null == str) {
                return true;
            }

            if (null == str) {
                return false;
            }

            if (null != item && item.toString().toLowerCase().equalsIgnoreCase(str.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link CommonConstant#INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link CommonConstant#INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(char[] array, char value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 判断两个数组是否相等，判断依据包括数组长度和每个元素都相等。
     *
     * @param array1 数组1
     * @param array2 数组2
     * @return 是否相等
     * @since 5.4.2
     */
    public static boolean isEquals(Object array1, Object array2) {
        if (array1 == array2) {
            return true;
        }
        if (hasNull(array1, array2)) {
            return false;
        }

        if (array1 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        } else if (array1 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        } else if (array1 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        } else if (array1 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        } else if (array1 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        } else if (array1 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        } else if (array1 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        } else if (array1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        } else {
            // Not an array of primitives
            return Arrays.deepEquals((Object[]) array1, (Object[]) array2);
        }
    }

    /**
     * 数据类型是否一致
     *
     * @param source 数组
     * @param target 数组
     * @return 数组 = {target} 返回 true
     */
    public static boolean isEquals(Class<?>[] source, Class<?>[] target) {
        if (source == target && source == null) {
            return true;
        }

        if (source.length != target.length) {
            return false;
        }

        boolean isEquals = true;
        for (int i = 0; i < source.length; i++) {
            Class<?> sourceClass = source[i];
            Class<?> targetClass = target[i];

            if (null == sourceClass || void.class == sourceClass || Void.class == sourceClass) {
                continue;
            }

            if (!targetClass.isAssignableFrom(sourceClass)) {
                isEquals = false;
                break;
            }
        }
        return isEquals;
    }

    /**
     * 数组是否一致
     *
     * @param source 数组
     * @param target 数组
     * @return 数组 = {target} 返回 true
     */
    @SuppressWarnings("ALL")
    public static <T> boolean isEquals(T[] source, T[] target) {
        if (source == target && source == null) {
            return true;
        }

        if (source.length != target.length) {
            return false;
        }

        boolean isEquals = true;
        for (int i = 0; i < source.length; i++) {
            T sourceClass = source[i];
            if (sourceClass instanceof ParameterDescribe) {
                sourceClass = (T) ((ParameterDescribe) sourceClass).returnTypeType();
            }

            T targetClass = target[i];
            if (targetClass instanceof ParameterDescribe) {
                targetClass = (T) ((ParameterDescribe) targetClass).returnTypeType();
            }

            if (null != sourceClass && !sourceClass.equals(targetClass)) {
                isEquals = false;
                break;
            }
        }
        return isEquals;
    }

    /**
     * 参数是否一致
     *
     * @param params 参数类型
     * @param args   值
     * @return 是否一致
     */
    public static boolean isEquals(Class<?>[] params, Object[] args) {
        if (params.length != args.length) {
            return false;
        }

        int index = 0;
        for (Object arg : args) {
            Class<?> item = params[index++];
            boolean rs = null != arg && (!ClassUtils.resolvePrimitiveClassName(item.getName()).isAssignableFrom(arg.getClass())) || !item.isAssignableFrom(arg.getClass());
            if (rs) {
                return false;
            }
        }
        return true;
    }

    /**
     * 参数是否一致
     *
     * @param params 参数类型
     * @param args   值
     * @return 是否一致
     */
    public static boolean isEquals(Class<?>[] params, String[] args) {
        if (params.length != args.length) {
            return false;
        }

        int index = 0;
        for (Object arg : args) {
            Class<?> item = params[index];
            boolean rs = null != arg && item.getTypeName().equals(args[index]);
            index++;
            if (!rs) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是空集合
     *
     * @param array 集合
     * @return 是空集合
     */
    public static <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }

    /**
     * 是空集合
     *
     * @param array 集合
     * @return 是空集合
     */
    public static boolean isEmpty(final int[] array) {
        return null == array || array.length == 0;
    }

    /**
     * 是空集合
     *
     * @param array 集合
     * @return 是空集合
     */
    public static boolean isEmpty(final float[] array) {
        return null == array || array.length == 0;
    }

    /**
     * 是空集合
     *
     * @param array 集合
     * @return 是空集合
     */
    public static boolean isEmpty(final byte[] array) {
        return null == array || array.length == 0;
    }

    // ----------------------------------------------------------------------

    /**
     * <p>Checks if an array of Objects is not empty and not {@code null}.
     *
     * @param <T>   the component type of the array
     * @param array the array to test
     * @return {@code true} if the array is not empty and not {@code null}
     * @since 2.5
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    /**
     * <p>Checks if an array of Objects is not empty and not {@code null}.
     *
     * @param <T>   the component type of the array
     * @param array the array to test
     * @return {@code true} if the array is not empty and not {@code null}
     * @since 2.5
     */
    public static <T> boolean isNotEmpty(final byte[] array) {
        return !isEmpty(array);
    }

    /**
     * Converts from 1D array index to 1D on x axis.
     *
     * @param index      The index of 1D array.
     * @param arrayWidth 2D Array width (length of rows on x axis).
     * @return Corresponding index of x axis.
     */
    public static int convert1DtoX(final int index, final int arrayWidth) {
        return index % arrayWidth;
    }

    /**
     * Converts from 1D array index to 1D on y axis.
     *
     * @param index      The index of 1D array.
     * @param arrayWidth 2D Array width (length of rows on x axis).
     * @return Corresponding index of y axis.
     */
    public static int convert1DtoY(final int index, final int arrayWidth) {
        return index / arrayWidth;
    }

    /**
     * Converts from 2D array index to 1D.
     *
     * @param x          The index on x axis.
     * @param y          The index on x axis.
     * @param arrayWidth 2D Array width (length of rows on x axis).
     * @return Corresponding index if the array was 1D.
     */
    public static int convert2dTo1d(final int x, final int y, final int arrayWidth) {
        return y * arrayWidth + x;
    }

    /**
     * 联合两个数组
     *
     * @param target 第一个数组
     * @param source 元素
     * @return 内容合并后的数组
     */
    public static <T> T[] addElement(T[] target, T... source) {
        if (target.length == 0 && source.length == 0) {
            return null;
        }

        List<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(target));
        result.addAll(Arrays.asList(source));
        T[] instance = (T[]) Array.newInstance(result.get(0).getClass(), 0);
        return result.toArray(instance);
    }

    /**
     * 对象转为数组
     *
     * @param iterable iterable
     * @param array    对象
     * @return 数组
     */
    public static <T> T[] toArray(Iterable<? extends T> iterable, T[] array) {
        List<? extends T> ts = CollectionUtils.newArrayList(iterable);
        return ts.toArray(array);
    }

    /**
     * 对象转为数组
     *
     * @param iterable iterable
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Iterable<? extends T> iterable) {
        List<? extends T> ts = CollectionUtils.newArrayList(iterable);
        if (null == ts || ts.isEmpty()) {
            return null;
        }
        return ts.toArray((T[]) Array.newInstance(ts.get(0).getClass(), 0));
    }

    /**
     * 去除空格和分隔符开尾
     *
     * @param split     数据
     * @param separator 分隔符
     * @return 结果
     */
    public static String[] trimOrSeparator(String[] split, String separator) {
        String[] rs = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            rs[i] = StringUtils.startWithMove(StringUtils.endWithMove(s.trim(), separator), separator);
        }

        return rs;
    }

    /**
     * 转化为 byte[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static byte[] transToByteArray(String[] value) {
        if(value == null) {
            return new byte[0];
        }

        byte[] bytes = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            String s = value[i];
            try {
                bytes[i] = Converter.convertIfNecessary(s, byte.class);
            } catch (Exception e) {
                break;
            }
        }
        return new byte[0];
    }
    /**
     * 转化为 byte[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static byte[] transToByteArray(Object value) {
        if(value == null) {
            return new byte[0];
        }

        if(value.getClass().isArray()) {
            return transToByteArray((Object[])value);
        }

        if(value instanceof Collection) {
            return transToByteArray(new LinkedList((Collection) value));
        }
        return new byte[0];
    }

    /**
     * 转化为 byte[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static byte[] transToByteArray(List value) {
        byte[] newInstance = new byte[value.size()];
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (Object o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, Byte.class).byteValue();
        }
        return newInstance;
    }

    /**
     * 转化为 byte[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> byte[] transToByteArray(T[] value) {
        byte[] newInstance = new byte[value.length];
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (Object o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, Byte.class).byteValue();
        }
        return newInstance;
    }

    /**
     * 转化为 byte[]
     *
     * @param value 待转化值
     * @return 能够转化返回T[], 反之返回 null
     */
    @SuppressWarnings("all")
    public static <T> int[] transToIntArray(byte[] value) {
        int[] newInstance = new int[value.length];
        if (newInstance.length == 0) {
            return newInstance;
        }
        int count = 0;
        for (Object o : value) {
            newInstance[count++] = Converter.convertIfNecessary(o, int.class).byteValue();
        }
        return newInstance;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }
        T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 新建一个空数组
     *
     * @param newSize 大小
     * @return 空数组
     * @since 3.3.0
     */
    public static Object[] newArray(int newSize) {
        return new Object[newSize];
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 是否全部为空
     *
     * @param arr 数组
     * @return 是否全部为空
     */
    public static boolean allEmpty(Object[] arr) {
        for (Object o : arr) {
            if (null != o) {
                return false;
            }
        }

        return true;
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (ArrayUtils.isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }


    /**
     * Transfer a String array to String List
     */
    public static List<String> strArrayToList(String[] arr) {
        List<String> result = new ArrayList<String>();
        if (arr == null || arr.length == 0) {
            return result;
        }
        for (String str : arr) {
            result.add(str);
        }
        return result;
    }

    /**
     * 集合转为数组
     *
     * @param columns    字段顺序
     * @param collection 集合
     * @return 数组
     */
    public static Object[][] toArrays(List<String> columns, List<?> collection) {
        Object[][] rs = new Object[collection.size()][columns.size()];
        for (int i = 0; i < collection.size(); i++) {
            Object o = collection.get(i);
            Object[] objects = toArray(columns, o);
            for (int j = 0; j < columns.size(); j++) {
                rs[i][j] = objects[j];
            }
        }

        return rs;
    }

    /**
     * 对象转为数组
     *
     * @param columns 字段顺序
     * @param o       对象
     * @return 数组
     */
    private static Object[] toArray(List<String> columns, Object o) {
        BeanMap beanMap = BeanMap.of(o);
        Object[] rs = new Object[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            rs[i] = beanMap.get(column);
        }

        return rs;
    }

    /**
     * 转为集合
     *
     * @param value 数组
     * @return 集合
     */
    public static Collection<?> toList(Object value) {
        List<Object> rs = new LinkedList<>();
        int length = Array.getLength(value);
        for (int i = 0; i < length; i++) {
            rs.add(Array.get(value, i));
        }

        return rs;
    }

    /**
     * 截取数组
     *
     * @param arr    数组
     * @param offset 位置
     * @return 截取数组
     */
    @SuppressWarnings("ALL")
    public static <T>T[] subArray(T[] arr, int offset) {
        if(arr.length < offset) {
            return null;
        }

        T[] v = (T[]) Array.newInstance(arr[0].getClass(), arr.length - offset);
        System.arraycopy(arr, offset, v, 0, v.length);
        return v;
    }
    /**
     * 截取数组
     *
     * @param arr    数组
     * @param offset 位置1
     * @param offset2 位置2
     * @return 截取数组
     */
    @SuppressWarnings("ALL")
    public static <T>T[] subArray(T[] arr, int offset, int offset2) {
        if(arr.length < offset) {
            return null;
        }

        if(offset2 <= offset) {
            return null;
        }

        T[] v = (T[]) Array.newInstance(arr[0].getClass(), arr.length - offset);
        System.arraycopy(arr, offset, v, 0, offset2 - offset);
        return v;
    }
}
