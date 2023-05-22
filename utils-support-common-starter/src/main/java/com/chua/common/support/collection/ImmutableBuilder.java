package com.chua.common.support.collection;

import java.util.*;

/**
 * 集合
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ImmutableBuilder {
    /**
     * 创建 collection
     *
     * @param <E> e
     * @return collection
     */
    public static <E> CollectionBuilder<E> builder() {
        return new CollectionBuilder<>(Collections.emptyList());
    }

    /**
     * 创建 collection
     *
     * @param <E>        e
     * @param comparable 比较器
     * @return collection
     */
    public static <E> CollectionComparableBuilder<E> builderComparable(Comparator<E> comparator) {
        return new CollectionComparableBuilder<>(comparator);
    }

    /**
     * 创建 collection
     *
     * @param <E>      e
     * @param elements elements
     * @return collection
     */
    public static <E> CollectionBuilder<E> copyOf(E... elements) {
        return new CollectionBuilder<>(elements);
    }

    /**
     * 创建 collection
     *
     * @param <E>      e
     * @param elements elements
     * @return collection
     */
    public static <E> CollectionBuilder<E> copyOf(Iterable<? extends E> elements) {
        return new CollectionBuilder<>(elements);
    }

    /**
     * 创建 collection
     *
     * @param <E>      e
     * @param elements elements
     * @return collection
     */
    public static <E> CollectionBuilder<E> copyOf(Iterator<? extends E> elements) {
        return new CollectionBuilder<>(elements);
    }

    /**
     * 创建 collection
     *
     * @param <E>  e
     * @param list list
     * @return collection
     */
    public static <E> CollectionBuilder<E> copyOf(Collection<E> list) {
        return new CollectionBuilder<>(list);
    }

    /**
     * 创建 collection
     *
     * @param <K> V
     * @param <V> V
     * @param map map
     * @return collection
     */
    public static <K, V> MapBuilder<K, V> copyOf(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    /**
     * 创建 Map
     *
     * @param <K> K
     * @param <V> V
     * @return Map
     */
    public static <K, V> MapBuilder<K, V> newMap() {
        return new MapBuilder<>(Collections.emptyMap());
    }

    /**
     * 创建table
     *
     * @param <R> r
     * @param <C> c
     * @param <V> v
     * @return table
     */
    public static <R, C, V> TableBuilder<R, C, V> newTable() {
        return new TableBuilder<>();
    }

    public static <T> List<T> newArrayList() {
        return ImmutableBuilder.<T>builder().newArrayList();
    }

    public static <K, V> Map<K, V> newHashMap() {
        return ImmutableBuilder.<K, V>newMap().newHashMap();
    }


    static final class CollectionComparableBuilder<E> extends CollectionMethodBuilder<E, CollectionComparableBuilder<E>> {

        private final Comparator<E> comparable;

        public CollectionComparableBuilder(Comparator<E> comparator) {
            super(Collections.emptyList());
            this.comparable = comparator;
        }

        public TreeSet<E> build() {
            TreeSet<E> es = new TreeSet<>(comparable);
            es.addAll(list);
            return es;
        }

    }

    public static class CollectionMethodBuilder<E, R extends CollectionMethodBuilder<E, R>> {
        protected final List list = new LinkedList<>();

        public CollectionMethodBuilder(Collection<E> list) {
            this.list.addAll(list);
        }

        public CollectionMethodBuilder(E[] elements) {
            this.list.addAll(Arrays.asList(elements));
        }

        public CollectionMethodBuilder(Iterable<? extends E> elements) {
            add(elements);
        }

        public CollectionMethodBuilder(Iterator<? extends E> elements) {
            add(elements);
        }


        public R add(Iterable<? extends E> elements) {
            return add(elements.iterator());
        }

        public R add(Iterator<? extends E> elements) {
            while (elements.hasNext()) {
                add(elements.next());
            }
            return (R) this;
        }

        public R add(E e) {
            list.add(e);
            return (R) this;
        }

        public R add(Collection<E> e) {
            list.addAll(e);
            return (R) this;
        }


        public R add(E... e) {
            list.addAll(Arrays.asList(e));
            return (R) this;
        }


        public R addAll(Collection<E> elements) {
            list.addAll(elements);
            return (R) this;
        }


    }

    public static final class CollectionBuilder<E> extends CollectionMethodBuilder<E, CollectionBuilder<E>> {

        public CollectionBuilder(Collection<E> list) {
            super(list);
        }

        public CollectionBuilder(E[] elements) {
            super(elements);
        }

        public CollectionBuilder(Iterable<? extends E> elements) {
            super(elements);
        }

        public CollectionBuilder(Iterator<? extends E> elements) {
            super(elements);
        }

        public List<E> build() {
            return list;
        }

        public Set<E> newSet() {
            return new HashSet<>(build());
        }


        public Set<E> newLinkedHashSet() {
            return new LinkedHashSet<>(build());
        }

        public LinkedList<E> newLinkedList() {
            return new LinkedList<>(build());
        }

        public Collection<E> newCollection() {
            return build();
        }

        public List<E> newUnmodifiableList() {
            return Collections.unmodifiableList(build());
        }


        public List<E> newArrayList() {
            return new ArrayList<>(build());
        }

        public CartesianList<E> newCartesianList() {
            return (CartesianList<E>) CartesianList.create(list);
        }
    }

    public static final class MapBuilder<K, V> {
        private final Map<K, V> map = new LinkedHashMap<>();

        public MapBuilder(Map<K, V> map) {
            this.map.putAll(map);
        }


        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public MapBuilder<K, V> put(Map<K, V> map) {
            map.putAll(map);
            return this;
        }


        public Map<K, V> build() {
            return map;
        }

        public Map<K, V> newHashMap() {
            return new HashMap<>(build());
        }

        public Map<K, V> unmodifiableMap() {
            return Collections.unmodifiableMap(build());
        }

        public Map<K, V> synchronizedMap() {
            return Collections.synchronizedMap(build());
        }

    }

    public static final class TableBuilder<R, C, V> {
        private Table<R, C, V> table1 = new ConcurrentReferenceTable();


        public TableBuilder<R, C, V> put(R r, C c, V v) {
            table1.put(r, c, v);
            return this;
        }

        public Table<R, C, V> build() {
            return table1;
        }

    }

}
