package com.chua.common.support.lang.arrange;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 编排
 * @author CH
 */
@Data
public class ArrangeResult {
    public static final ArrangeResult INSTANCE = new ArrangeResult();
    /**
     * 当前处理结束任务
     */
    private String name;
    /**
     * 数据
     */
    private Object data;
    /**
     * 是否运行
     */
    private boolean isRunning = true;
    /**
     * 各个任务结果
     */
    private Map<String, ArrangeResult> param = new LinkedHashMap<>();
}
