package com.chua.common.support.collection;

import java.util.Iterator;

/**
 * 双向链表
 *
 * @author CH
 */
public class DoubleLinkedLinkedList<E> implements DoubleLinkedList<E> {

    private Node<E> root;

    public DoubleLinkedLinkedList() {
    }

    public DoubleLinkedLinkedList(Node<E> root) {
        this.root = root;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            Node<E> rs = root;

            @Override
            public boolean hasNext() {
                return null != rs;
            }

            @Override
            public E next() {
                E data = rs.data;
                rs = null == rs ? null : rs.next;
                return data;
            }
        };
    }

    @Override
    public void addFirst(E e) {

    }

    @Override
    public Iterator<Node<E>> iteratorNode() {
        return new Iterator<Node<E>>() {
            Node<E> rs = root;

            @Override
            public boolean hasNext() {
                return null != rs;
            }

            @Override
            public Node<E> next() {
                Node<E> next = rs;
                rs = null == rs ? null : next.next;
                return next;
            }
        };
    }

    @Override
    public synchronized void addLast(E e) {
        if (null == root) {
            this.root = new Node<>(e);
        } else {
            Node<E> newNode = new Node<>(e);
            Node<E> ele = root;
            while (true) {
                Node<E> tpl = ele.next;
                if (null == tpl) {
                    ele.next = newNode;
                    newNode.prev = ele;
                    break;
                }
                ele = tpl;
            }
        }
    }

    @Override
    public boolean contains(E e) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void add(int index, E e) {
        Node<E> newNode = new Node<>(e);
        if (index == 0) {
            newNode.next = root;
            root.prev = newNode;
            this.root = newNode;
            return;
        }

        int index1 = 0;
        Node<E> tpl = root;
        while (index1++ != index) {
            tpl = tpl.next;
        }

        newNode.next = tpl;
        newNode.prev = tpl.prev;
        tpl.prev.next = newNode;
        tpl.prev = newNode;
    }

    @Override
    public synchronized void remove(int index) {
        if (0 == index) {
            this.root = root.next;
            this.root.prev = null;
            return;
        }

        int index1 = 0;
        Node<E> tpl = root;
        while (index1++ != index) {
            tpl = tpl.next;
        }
        tpl.prev.next = tpl.next;
        tpl.next.prev = tpl.prev;
    }


    @Override
    public void removeAllKey(E e) {

    }

    @Override
    public void clear() {

    }

    @Override
    public E first() {
        return root.data;
    }

    @Override
    public E last() {
        if (root.next == root) {
            return root.data;
        }

        Node<E> tpl = root;
        while (null != tpl.next) {
            tpl = tpl.next;
        }
        return tpl.data;
    }


    @Override
    public String toString() {
        return root.data + "";
    }


}
