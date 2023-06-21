package com.chua.common.support.modularity.resolver;

import com.chua.common.support.modularity.Modularity;
import com.chua.common.support.modularity.ModularityFactory;
import com.chua.common.support.modularity.ModularityResult;

import java.util.Map;

/**
 * 模块类型解析器
 *
 * @author CH
 */
public interface ModularityTypeResolver {
    /**
     * 模块类型处理器
     *
     * @param modularityFactory 模块工厂
     * @param modularity        模块
     * @param args              参数
     * @return 结果
     */
    ModularityResult execute(ModularityFactory modularityFactory, Modularity modularity, Map<String, Object> args);
}
