package com.chua.common.support.lang.arrange;

import com.chua.common.support.modularity.Modularity;
import lombok.Data;

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
}
