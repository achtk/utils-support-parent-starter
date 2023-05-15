package com.chua.common.support.utils;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Consumer;

/**
 * 集合工具类
 *
 * @author CH
 */
public class CollectionUtils {
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * 随机获取数据
     *
     * @param source 集合
     * @param <T>    类型
     * @return 元素
     */
    public static <T> T getRandom(final Collection<T> source) {
        if (isEmpty(source)) {
            return null;
        }
        SecureRandom random = new SecureRandom();
        int i = random.nextInt(source.size());
        return CollectionUtils.find(source, i);
    }

    /**
     * 将一个List均分成n个list,主要通过偏移量来实现的
     *
     * @param source 源集合
     * @param limit  最大值
     * @return 集合
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int limit) {
        if (null == source || source.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        int listCount = (source.size() - 1) / limit + 1;
        // (先计算出余数)
        int remainder = source.size() % listCount;
        // 然后是商
        int number = source.size() / listCount;
        // 偏移量
        int offset = 0;
        for (int i = 0; i < listCount; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @param <E>        类型
     * @return 集合是否为空
     */
    public static <E> boolean isEmpty(Collection<E> collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * 集合是否不为空
     *
     * @param collection 集合
     * @param <E>        类型
     * @return 集合是否为空
     */
    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }

    /**
     * 元素数量
     *
     * @param collection 集合
     * @return 数量
     */
    public static int size(Collection<?> collection) {
        return null == collection ? 0 : collection.size();
    }

    /**
     * 对象转集合
     *
     * @param value 对象
     * @return 集合
     */
    public static List<Object> ifList(Object value) {
        return value instanceof List ? (List<Object>) value : Collections.emptyList();
    }

    /**
     * 是否是集合
     *
     * @param source 数据
     * @return 集合返回true
     */
    public static boolean isList(Object source) {
        return source instanceof List;
    }

    /**
     * 获取索引对应的数据
     *
     * @param source 数据
     * @param <T>    类型
     * @return 数据
     */
    public static <T> T findFirst(final Collection<T> source) {
        if (null == source || source.isEmpty()) {
            return null;
        }
        if (source instanceof List) {
            return ((List<T>) source).get(0);
        }
        Iterator<T> iterator = source.iterator();
        return iterator.next();
    }

    /**
     * 获取索引对应的数据
     *
     * @param source 数据
     * @param <T>    类型
     * @return 数据
     */
    public static <T> T findLast(final Collection<T> source) {
        if (null == source || source.isEmpty()) {
            return null;
        }
        if (source instanceof List) {
            return ((List<T>) source).get(source.size() - 1);
        }
        return source.stream().skip(source.size() - 1).findFirst().get();
    }

    /**
     * 获取索引对应的数据
     *
     * @param source 数据
     * @param index  索引
     * @param <T>    类型
     * @return 数据
     */
    public static <T> T find(final Collection<T> source, final int index) {
        return find(source, index, null);
    }

    /**
     * 获取索引对应的数据
     *
     * @param source 数据
     * @param index  索引
     * @param <T>    类型
     * @return 数据
     */
    public static <T> T find(final Collection<T> source, final int index, final T defaultValue) {
        if (null == source) {
            return defaultValue;
        }

        if (index < 0) {
            ArrayList<T> ts = new ArrayList<>(source);
            Collections.reverse(ts);
            return find(ts, Math.abs(index), defaultValue);
        }

        int length = source.size();
        if (index >= length) {
            return defaultValue;
        }
        if (source instanceof List) {
            return Optional.ofNullable(((List<T>) source).get(index)).orElse(defaultValue);
        }
        return source.stream().skip(index).findFirst().orElse(defaultValue);
    }

    /**
     * 是否存在元素
     *
     * @param collection 集合
     * @return 是否存在元素
     */
    public static boolean hasElement(Collection<?> collection) {
        return size(collection) > 0;
    }


    // ------------------------------------------------------------------------------------------------- sort

    /**
     * 对指定List分页取值
     *
     * @param <T>      集合元素类型
     * @param pageNo   页码，第一页的页码取决于{@link PageUtils#getFirstPageNo()}，默认0
     * @param pageSize 每页的条目数
     * @param list     列表
     * @return 分页后的段落内容
     * @since 4.1.20
     */
    public static <T> List<T> page(int pageNo, int pageSize, List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>(0);
        }

        int resultSize = list.size();
        // 每页条目数大于总数直接返回所有
        if (resultSize <= pageSize) {
            if (pageNo < (PageUtils.getFirstPageNo() + 1)) {
                return unmodifiable(list);
            } else {
                // 越界直接返回空
                return new ArrayList<>(0);
            }
        }
        // 相乘可能会导致越界 临时用long
        if (((long) (pageNo - PageUtils.getFirstPageNo()) * pageSize) > resultSize) {
            // 越界直接返回空
            return new ArrayList<>(0);
        }

        final int[] startEnd = PageUtils.transToStartEnd(pageNo, pageSize);
        if (startEnd[1] > resultSize) {
            startEnd[1] = resultSize;
            if (startEnd[0] > startEnd[1]) {
                return new ArrayList<>(0);
            }
        }

        return sub(list, startEnd[0], startEnd[1]);
    }

    /**
     * 将对应List转换为不可修改的List
     *
     * @param list List
     * @param <T>  元素类型
     * @return 不可修改List
     * @since 5.2.6
     */
    public static <T> List<T> unmodifiable(List<T> list) {
        if (null == list) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 对指定List进行分页，逐页返回数据
     *
     * @param <T>              集合元素类型
     * @param list             源数据列表
     * @param pageSize         每页的条目数
     * @param pageListConsumer 单页数据函数式返回
     * @since 5.7.10
     */
    public static <T> void page(List<T> list, int pageSize, Consumer<List<T>> pageListConsumer) {
        if (isEmpty(list) || pageSize <= 0) {
            return;
        }

        final int total = list.size();
        final int totalPage = PageUtils.totalPage(total, pageSize);
        for (int pageNo = PageUtils.getFirstPageNo(); pageNo < totalPage + PageUtils.getFirstPageNo(); pageNo++) {
            // 获取当前页在列表中对应的起止序号
            final int[] startEnd = PageUtils.transToStartEnd(pageNo, pageSize);
            if (startEnd[1] > total) {
                startEnd[1] = total;
            }

            // 返回数据
            pageListConsumer.accept(sub(list, startEnd[0], startEnd[1]));
        }
    }

    /**
     * 排序集合，排序不会修改原集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }


    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     */
    public static <T> List<T> sub(List<T> list, int start, int end) {
        return sub(list, start, end, 1);
    }

    /**
     * 截取集合的部分<br>
     * 此方法与{@link List#subList(int, int)} 不同在于子列表是新的副本，操作子列表不会影响原列表。
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @since 4.0.6
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }

        final int size = list.size();
        if (start < 0) {
            start += size;
        }
        if (end < 0) {
            end += size;
        }
        if (start == size) {
            return new ArrayList<>(0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > size) {
            if (start >= size) {
                return new ArrayList<>(0);
            }
            end = size;
        }

        if (step < 1) {
            step = 1;
        }

        final List<T> result = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            result.add(list.get(i));
        }
        return result;
    }


    /**
     * 从列表中获取number个随机元素
     *
     * @param elementList 列表
     * @param number      获取的个数
     * @param <T>         泛型
     * @return 1个随机元素
     */
    public static <T> List<T> getRandomElement(List<T> elementList, int number) {
        if (CollectionUtils.isEmpty(elementList) || number < 1) {
            return Collections.emptyList();
        }
        // 如果获取元素的个数大于等于集合的大小, 直接返回乱序的原集合,不再抛出异常
        if (number >= elementList.size()) {
            List<T> result = new ArrayList<>(elementList);
            Collections.shuffle(result);
            return result;
        } else {
            List<T> result = new ArrayList<>(number);
            for (int i = 0; i < number; i++) {
                int index = RandomUtils.randomInt(0, elementList.size());
                T t = elementList.get(index);
                if (result.contains(t)) {
                    i--;
                } else {
                    result.add(t);
                }
            }
            Collections.shuffle(result);
            return result;
        }
    }

    /**
     * 添加数据
     *
     * @param source  元数据
     * @param element 元素
     */
    public static <E> void addAll(List<E> source, E... element) {
        if (null == source || element.length == 0) {
            return;
        }

        for (E e : element) {
            if (null == e) {
                continue;
            }
            source.add(e);
        }
    }

    /**
     * 添加数据
     *
     * @param source  元数据
     * @param element 元素
     */
    public static <E> void addAll(List<E> source, List<E> element) {
        if (null == source || null == element) {
            return;
        }

        source.addAll(element);
    }

    /**
     * 返回集合
     * @param list 数组
     * @return 集合
     * @param <T> 类型
     */
    public static <T>List<T> newArrayList(T[] list) {
        return null == list ? Collections.emptyList() : Arrays.asList(list);
    }
    /**
     * 返回集合
     * @param list 数组
     * @return 集合
     * @param <T> 类型
     */
    public static <T>List<T> newLinkedList(T[] list) {
        return null == list ? Collections.emptyList() : new LinkedList<>(Arrays.asList(list));
    }
}
