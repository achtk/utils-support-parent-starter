package com.chua.common.support.lang.function;


import java.util.List;

/**
 * 人体属性
 *
 * @author CH
 */
public interface BodyAttribute {
    /**
     * 人体属性
     *
     * @param face 图片
     * @return 结果
     */

    List<Body> detect(Object face);
}
