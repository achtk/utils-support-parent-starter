package com.chua.common.support.lang.date.lunar;

import com.chua.common.support.lang.date.DateTime;

import java.time.LocalDateTime;

/**
 * lunar
 *
 * @author CH
 */
public interface LunarDateTime {

    /**
     * 1天对应的毫秒
     */
    static final long MS_PER_DAY = 86400000L;
    /**
     * 节气表，国标以冬至为首个气令
     */
    public static final String[] JIE_QI = {"冬至", "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪"};
    /**
     * 实际的节气表
     */
    public static final String[] JIE_QI_IN_USE = {"DA_XUE", "冬至", "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "DONG_ZHI", "XIAO_HAN", "DA_HAN", "LI_CHUN", "YU_SHUI", "JING_ZHE"};


    /**
     * 获取{@link DateTime}LocalDateTime
     *
     * @return LocalDateTime
     */
    LocalDateTime toLocalDateTime();

}
