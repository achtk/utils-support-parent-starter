package com.chua.common.support.feature;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 人脸
 *
 * @author CH
 */
@Data
@Builder
@Accessors(fluent = true)
public class DetectionConfiguration implements Serializable {

    public static final DetectionConfiguration DEFAULT = DetectionConfiguration.builder().build();
    private float shrink;
    private float threshold;

    @Builder.Default
    private String cachePath = "../environment";

    private String groupId;
    /**
     * 使用的模型路径
     */
    private String modelPath;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 驱动
     */
    @Builder.Default
    private String device = "CPU";
    /**
     * gpu驱动序列
     */
    @Builder.Default
    private int deviceId = 0;
    /**
     * 检测
     */
    @Builder.Default
    private String detection = "retina";
    /**
     * 分类
     */

    @Builder.Default
    private String extraction = "amazon";
    /**
     * 分类标签
     */
    @Builder.Default
    private String label = "simple";
    /**
     * 模型名称
     */
    @Builder.Default
    private String labelModel = "simple.xm";
    /**
     * 训练分类数据
     */
    private String synsetArtifactName;
    /**
     * 训练分类数据
     */
    private String synset;
    /**
     * 类型, 用于查询实现类
     */
    private String type;
    /**
     * <p>trainHeight -> 以什么像素训练图片</p>
     * <p>trainWidth > 以什么像素训练图片</p>
     * <p>model -> 真实模型名称</p>
     */
    @Singular("ext")
    private Map<String, Object> ext;
    @Builder.Default
    private float scale = 1.0f;
    @Builder.Default
    private String engine = "PyTorch";
    /**
     * 处理类型
     */
    private Option option;

    /**
     * 模式
     */
    public static enum Option {
        /**
         * 识别
         */
        DETECTION,
        /**
         * 训练
         */
        TRAIN
    }
}
