package com.chua.common.support.lang.function;

/**
 * 年龄
 *
 * @author CH
 */
public class StandardAge implements Age {

    private final int age;

    public StandardAge(int age) {
        this.age = age;
    }

    @Override
    public int age() {
        return age;
    }
}