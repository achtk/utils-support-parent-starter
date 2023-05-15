package com.chua.common.support.date.type;

/**
 * 时间
 *
 * @author CH
 */
public enum TimeOfDay {

    /**
     * 天
     */
    DAY("天"),
    /**
     * 小时
     */
    HOUR("小时"),
    /**
     * 分钟
     */
    MINUTE("分"),
    /**
     * 秒
     */
    SECOND("秒"),
    /**
     * 毫秒
     */
    MILLISECOND("毫秒");

    /**
     * 级别名称
     */
    private final String name;

    /**
     * 构造
     *
     * @param name 级别名称
     */
    TimeOfDay(String name) {
        this.name = name;
    }

    /**
     * 获取级别名称
     *
     * @return 级别名称
     */
    public String getName() {
        return this.name;
    }
}
