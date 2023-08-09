package com.chua.common.support.lang.function;

import com.chua.common.support.constant.PredictResult;
import lombok.Data;

/**
 * 人体属性
 */
@Data
public class Body extends PredictResult {

    /**
     * 性别
     */
    private Gender gender;
    /**
     * 是否活体
     */
    private Liveness liveness;
    /**
     * 年龄
     */
    private Age age;
}
