package com.chua.common.support.collection;

import com.chua.common.support.lang.robin.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

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
    public void addFirst(E e) {

    }

    @Override
    public synchronized void add(E e) {
        if(null == root) {
            this.root = new Node<>(e);
            this.root.prev = root;
            this.root.next = root;
        } else {
            Node<E> newNode = new Node<>(e);
            Node<E> ele = root;
            while (true) {
                Node<E> tpl = ele.next;
                if(root == tpl) {
                    ele.next = newNode;
                    newNode.prev = ele;
                    break;
                }
                ele = tpl;
            }

            newNode.next = root;
            root.prev = newNode;
        }
    }

    @Override
    public void addLast(E e) {

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
        if(index == 0) {
            add(this.root, newNode);
            this.root = newNode;

            return;
        }

        int index1 = 0;
        Node<E> tpl = root;
        while (index1 ++ != index) {
            tpl = tpl.next;
        }
        add(tpl, newNode);
    }

    private void add(Node<E> node, Node<E> newNode) {
        newNode.next = node;
        newNode.prev = node.prev;
        Node<E> prev = node.prev;
        node.prev = newNode;
        prev.next = newNode;
    }

    @Override
    public synchronized void remove(int index) {
        if(0 == index) {
            if(this.root == root.next) {
                this.root = null;
                return;
            }

            link(this.root);
            this.root = this.root.next;
            return;
        }

        int index1 = 0;
        Node<E> tpl = root;
        while (index1 ++ != index) {
            tpl = tpl.next;
        }
        link(tpl);
    }

    private void link(Node<E> tpl) {
        Node<E> next = tpl.next;
        next.prev = tpl.prev;
        tpl.prev.next = next;
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
        if(root.next == root) {
            return root.data;
        }

        Node<E> tpl = root;
        while (root != tpl.next) {
            tpl = tpl.next;
        }
        return tpl.data;
    }


    @Override
    public String toString() {
        return root.data + "";
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    @Accessors(chain = true)
    static class Node<E> {
        @NonNull
        E data;
        Node<E> prev;
        Node<E> next;

        @Override
        public String toString() {
            return data.hashCode() + "";
        }
    }
}
