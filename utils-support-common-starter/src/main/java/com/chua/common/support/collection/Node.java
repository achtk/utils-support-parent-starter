package com.chua.common.support.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * node
 *
 * @param <E> e
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
public class Node<E> {
    @NonNull
    E data;
    Node<E> prev;
    Node<E> next;

    @Override
    public String toString() {
        return data.hashCode() + "";
    }
}