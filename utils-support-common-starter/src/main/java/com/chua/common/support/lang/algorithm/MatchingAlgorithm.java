package com.chua.common.support.lang.algorithm;

/**
 * 匹配算法
 *
 * @author CH
 */
public interface MatchingAlgorithm extends Algorithm {

    /**
     * 配置
     *
     * @param source 来源
     * @param target 目标
     * @return 积分
     */
    double match(String source, String target);
}
