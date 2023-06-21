package com.chua.common.support.lang.arrange;

import com.chua.common.support.utils.StringUtils;
import lombok.Data;

/**
 * 编排
 * @author CH
 */
@Data
public class Arrange {
    /**
     * 任务名称
     */
    private String arrangeName;
    /**
     * 类型
     */
    private String arrangeType;
    /**
     * 依赖任务
     */
    private String arrangeDepends;
    /**
     * 任务处理超时时间
     */
    private String arrangeConnectionTimeout = "1min";
    /**
     * 结果
     */
    private ArrangeResult arrangeResult;
    /**
     * 处理器
     */
    private ArrangeHandler handler;

    public boolean hasDepends() {
        return StringUtils.isNotEmpty(arrangeDepends);
    }

    public String getArrangeId() {
        return arrangeType + ":" + arrangeName;
    }
}
