package com.chua.common.support.lang.arrange;

import java.util.Map;

/**
 * 任务处理器
 * @author CH
 */
public interface ArrangeHandler {
    /**
     * 执行器
     * @param args 参数
     * @return 结果
     */
    ArrangeResult execute(Map<String, Object> args);
}
