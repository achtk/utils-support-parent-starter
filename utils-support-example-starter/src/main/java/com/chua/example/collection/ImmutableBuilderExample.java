package com.chua.example.collection;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.collection.Table;
import com.chua.common.support.utils.RandomUtils;
import com.google.common.collect.ImmutableTable;

import java.util.List;
import java.util.Map;

/**
 * @author CH
 */
public class ImmutableBuilderExample {

    public static void main(String[] args) {
        //创建Collection
        List<Integer> list = ImmutableBuilder.<Integer>builder()
                .add(ImmutableBuilder.copyOf(1, 2, 3).build())
                .add(ImmutableBuilder.copyOf(4, 5, 6).build())
                .newUnmodifiableList();

        System.out.println(list);
        //创建Map

        Map<Integer, String> map = ImmutableBuilder.<Integer, String>builderOfMap()
                .put(1, RandomUtils.randomString(4))
                .put(2, RandomUtils.randomString(4))
                .newHashMap();

        System.out.println(map);
        //创建Table

        Table<Integer, String, String> table = ImmutableBuilder.<Integer, String, String>builderOfTable()
                .put(1, "a", "A")
                .put(2, "a", "B")
                .put(3, "b", "A")
                .build();

        System.out.println(table);
        ImmutableTable<Integer, String, String> immutableTable = ImmutableTable.<Integer, String, String>builder()
                .put(1, "a", "A")
                .put(2, "a", "B")
                .put(3, "b", "A")
                .build();


        System.out.println(immutableTable);
    }
}
