package com.chua.common.support.lang.arrange;

import java.util.List;
import java.util.Map;

/**
 * 编排
 * @author CH
 */
public interface ArrangeFactory {

    /**
     * 獲取任務
     * @param name 名稱
     * @return 任務
     */
    Arrange getArrange(String name);

    /**
     * 运行
     *
     * @param args 参数
     * @return 结果
     */
    ArrangeResult run(Map<String, Object> args);

    /**
     * 获取无依赖任务
     * @return 获取无依赖任务
     */
    List<Arrange> getNoDepends();

    /**
     * 所有任务
     * @return 所有任务
     */
    List<Arrange> list();
}
