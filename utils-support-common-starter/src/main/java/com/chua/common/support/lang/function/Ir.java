package com.chua.common.support.lang.function;

import java.util.List;

/**
 * ir/rgb活体检测
 *
 * @author CH
 */
public interface Ir {

    /**
     * 是否真人
     *
     * @param face 图片
     * @return -1：非真人
     */
    List<Body> live(Object face);
}
