package com.chua.example.collection;

import com.chua.common.support.collection.DoubleLinkedLinkedList;
import com.chua.common.support.collection.DoubleLinkedList;

/**
 * @author CH
 */
public class LinkedExample {

    public static void main(String[] args) {

        DoubleLinkedList<Integer> doubleLinkedList = new DoubleLinkedLinkedList<>();
        for (int i = 2; i < 6; i++) {
            doubleLinkedList.add(i);
        }

        doubleLinkedList.add(7);
        doubleLinkedList.add(0, 1);
        doubleLinkedList.add(5, 6);
        doubleLinkedList.first();
        doubleLinkedList.last();
        doubleLinkedList.add(0, 1);
        doubleLinkedList.remove(0);
        doubleLinkedList.add(2, 1);
        doubleLinkedList.remove(2);
        System.out.println();
    }
}
