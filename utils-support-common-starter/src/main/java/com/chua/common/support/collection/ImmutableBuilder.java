package com.chua.common.support.collection;

import java.util.*;

/**
 * 集合
 *
 * @author CH
 */
public class ImmutableBuilder {
    /**
     * 创建 collection
     *
     * @param <E> e
     * @return collection
     */
    public static <E> CollectionBuilder<E> builder() {
        return new CollectionBuilder<>();
    }

    /**
     * 创建 Map
     * @return Map
     * @param <K> K
     * @param <V> V
     */
    public static <K, V>MapBuilder<K, V> newMap() {
        return new MapBuilder<>();
    }

    /**
     * 创建table
     * @return table
     * @param <R> r
     * @param <C> c
     * @param <V> v
     */
    public static <R, C, V>TableBuilder<R, C, V> newTable() {
        return new TableBuilder<>();
    }

    public static <T>List<T> newArrayList() {
        return ImmutableBuilder.<T>builder().newArrayList();
    }

    public static <K, V>Map<K, V> newHashMap() {
        return ImmutableBuilder.<K, V>newMap().newHashMap();
    }


    public static class CollectionBuilder<E>{
        private List<E> list = new LinkedList<>();

        public CollectionBuilder<E> add(E e) {
            list.add(e);
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

        public List<E> unmodifiableList() {
            return Collections.unmodifiableList(build());
        }


        public List<E> newArrayList() {
            return new ArrayList<>(build());
        }
    }

    public static class MapBuilder<K, V>{
        private Map<K, V> map = new LinkedHashMap<>();


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
    public static class TableBuilder<R, C, V>{
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
