package com.chua.common.support.lang.train.nlp;

import java.io.IOException;
import java.util.Map;

/**
 * 训练模式
 *
 * @author CH
 * @version 1.0.0
 */
public interface NlpTrain {

    /**
     * 预测最可能的分类
     *
     * @param source         文本
     * @param classification 分类
     * @return 结果
     */
    String classify(String source, String classification);

    /**
     * 基础配置
     *
     * @param trainConfig 配置
     * @return this
     */
    NlpTrain initial(TrainConfig trainConfig);

    /**
     * 预测分类
     *
     * @param source 文本
     * @return 结果
     */
    Map<String, Double> predict(String source);

    /**
     * 训练
     *
     * @throws IOException 异常
     */
    void train() throws IOException;
}
