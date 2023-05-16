package com.chua.common.support.collection;

import java.util.*;

/**
 * 排序数组集合
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/4/7
 */
public class SortedArrayList<E> implements SortedList<E>, RandomAccess {

    public static final SortedList<?> EMPTY = new SortedArrayList<>((o1, o2) -> 0);
    private transient final Comparator<? super E> comparator;
    private transient List<E> list = new ArrayList<>();


    public SortedArrayList(Comparator<? super E> comparator) {
        this(Collections.emptyList(), comparator);
    }

    public SortedArrayList(Collection<? extends E> c, Comparator<? super E> comparator) {
        this.addAll(c);
        //按对象的字符串形式做字典排序
        this.comparator = comparator;
    }

    @Override
    public synchronized boolean add(E e) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.add(e);
        if (changed) {
            newList.sort(this.comparator);
        }

        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized void add(int index, E element) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        newList.add(index, element);
        this.list.sort(this.comparator);
        this.list = Collections.unmodifiableList(newList);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.addAll(c);
        if (changed) {
            newList.sort(this.comparator);
        }

        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.addAll(index, c);
        if (changed) {
            newList.sort(this.comparator);
        }

        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized void clear() {
        this.list = Collections.emptyList();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public E first() {
        return isEmpty() ? null : list.get(0);
    }

    public List<E> get() {
        return this.list;
    }

    @Override
    public synchronized E get(int index) {
        return this.list.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @Override
    public E last() {
        return isEmpty() ? null : list.get(size() - 1);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return this.list.listIterator(index);
    }

    @Override
    public synchronized boolean remove(Object o) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.remove(o);
        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized E remove(int index) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        E result = newList.remove(index);
        this.list = Collections.unmodifiableList(newList);
        return result;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.removeAll(c);
        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        boolean changed = newList.retainAll(c);
        this.list = Collections.unmodifiableList(newList);
        return changed;
    }

    @Override
    public synchronized E set(int index, E element) {
        ArrayList<E> newList = new ArrayList<>(this.list);
        E result = newList.set(index, element);
        this.list.sort(this.comparator);
        this.list = Collections.unmodifiableList(newList);
        return result;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.list.toArray(a);
    }
}
