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


    public static class CollectionBuilder<E> {
        private final List list = new LinkedList<>();

        public CollectionBuilder(Collection<E> list) {
            this.list.addAll(list);
        }

        public CollectionBuilder(E[] elements) {
            this.list.addAll(Arrays.asList(elements));
        }

        public CollectionBuilder(Iterable<? extends E> elements) {
            add(elements);
        }

        public CollectionBuilder(Iterator<? extends E> elements) {
            add(elements);
        }


        public CollectionBuilder<E> add(Iterable<? extends E> elements) {
            return add(elements.iterator());
        }

        public CollectionBuilder<E> add(Iterator<? extends E> elements) {
            while (elements.hasNext()) {
                add(elements.next());
            }
            return this;
        }

        public CollectionBuilder<E> add(E e) {
            list.add(e);
            return this;
        }

        public CollectionBuilder<E> add(Collection<E> e) {
            list.addAll(e);
            return this;
        }


        public CollectionBuilder<E> add(E... e) {
            list.addAll(Arrays.asList(e));
            return this;
        }


        public CollectionBuilder<E> addAll(Collection<E> elements) {
            list.addAll(elements);
            return this;
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

    public static class MapBuilder<K, V> {
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

    public static class TableBuilder<R, C, V> {
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
