package com.chua.common.support.date.lunar;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 三伏
 * <p>从夏至后第3个庚日算起，初伏为10天，中伏为10天或20天，末伏为10天。当夏至与立秋之间出现4个庚日时中伏为10天，出现5个庚日则为20天。</p>
 *
 * @author 6tail
 */
@AllArgsConstructor
@NoArgsConstructor
public class Fu {
    /**
     * 名称：初伏、中伏、末伏
     */
    private String name;

    /**
     * 当前入伏第几天，1-20
     */
    private int index;


    @Override
    public String toString() {
        return name;
    }

    public String toFullString() {
        return name + "第" + index + "天";
    }
}
