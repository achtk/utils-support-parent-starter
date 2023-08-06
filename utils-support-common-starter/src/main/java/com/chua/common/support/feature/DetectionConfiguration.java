package com.chua.common.support.feature;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private int height = 224;
    @Builder.Default
    private int width = 224;
    @Builder.Default
    private boolean centerCrop = true;
    @Builder.Default
    private boolean resize = true;
    @Builder.Default
    private boolean applySoftmax = true;
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
     * option
     */
    private String options;

    @Builder.Default
    private int top = 10;

    public Map<String, Object> toMap() {
        Map<String, Object> arguments = new ConcurrentHashMap<>();
        arguments.put("width", width);
        arguments.put("height", height);
        arguments.put("centerCrop", centerCrop);
        arguments.put("resize", resize);
        arguments.put("applySoftmax", applySoftmax);
        arguments.put("synsetFileName", synset);
        arguments.put("threshold", threshold);
        return arguments;
    }


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
