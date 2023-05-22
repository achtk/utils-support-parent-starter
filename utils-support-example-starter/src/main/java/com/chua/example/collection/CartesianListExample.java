package com.chua.example.collection;

import com.chua.common.support.collection.CartesianList;
import com.chua.common.support.collection.ImmutableBuilder;

import java.util.List;

/**
 * @author CH
 */
public class CartesianListExample {

    public static void main(String[] args) {
        CartesianList<List<Integer>> cartesianList = ImmutableBuilder.<List<Integer>>builder()
                .add(ImmutableBuilder.copyOf(1, 2, 3).build())
                .add(ImmutableBuilder.copyOf(4, 5, 6).build())
                .newCartesianList();

        System.out.println(cartesianList);

    }
}
