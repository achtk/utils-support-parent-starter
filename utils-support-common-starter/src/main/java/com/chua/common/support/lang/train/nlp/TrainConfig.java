package com.chua.common.support.lang.train.nlp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 训练配置
 *
 * @author CH
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TrainConfig {
    /**
     * 模型地址
     */
    private String modelPath;
    /**
     * 类目
     */
    private String corpusPath;
    /**
     * 初始化时移除存在的模型
     */
    private boolean deleteModel;
    /**
     * 模型编码
     */
    private String languageCode = "cn";
    /**
     * 实体
     */
    private String[] type;

}
