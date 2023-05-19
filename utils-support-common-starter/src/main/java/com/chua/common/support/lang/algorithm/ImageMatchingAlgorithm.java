package com.chua.common.support.lang.algorithm;

import java.io.File;

/**
 * 匹配算法
 *
 * @author CH
 */
public interface ImageMatchingAlgorithm extends Algorithm {

    static final String MODELS_PATH = "models.path";
    static final String MODELS_TYPE = "models.type";
    static final String MODELS_BIN_DATA_DIR = "models.bin.data.dir";

    /**
     * 配置
     *
     * @param source 来源
     * @param target 目标
     * @return 积分
     */
    double match(File source, File target);
}
