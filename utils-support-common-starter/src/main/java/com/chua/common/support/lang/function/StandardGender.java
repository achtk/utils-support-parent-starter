package com.chua.common.support.lang.function;

/**
 * 性别
 *
 * @author CH
 */
public class StandardGender implements Gender {

    private final int gender;

    public StandardGender(int gender) {
        this.gender = gender;
    }

    @Override
    public int gender() {
        return gender;
    }
}