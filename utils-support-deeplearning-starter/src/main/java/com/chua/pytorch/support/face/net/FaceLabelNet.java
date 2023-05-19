package com.chua.pytorch.support.face.net;

import ai.djl.modality.cv.Image;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * p-net
 *
 * @author CH
 */
public interface FaceLabelNet extends Serializable {
    /**
     * 添加一个特征值
     *
     * @param lab     标签
     * @param feature 特征
     */
    void addFeature(String lab, float[] feature);

    /**
     * 推理获取最大的lab和最大相似度
     *
     * @param feature 特征
     * @return 最大的lab和最大相似度
     */
    default MaxLab predict(float[] feature) {
        return predict(feature, 0.8);
    }

    /**
     * 推理获取最大的lab和最大相似度
     *
     * @param feature   特征
     * @param threshold 阈值
     * @return 最大的lab和最大相似度
     */
    MaxLab predict(float[] feature, double threshold);

    /**
     * 加载磁盘上的网络对象
     *
     * @param name 名称
     */
    void load(String name);

    /**
     * 保存网络到磁盘上
     *
     * @param name name
     */
    void saveNet(String name);

    /**
     * 获取特征值
     *
     * @return 特征值
     */
    Map<String, Set<float[]>> models();

    /**
     * @author xiaoming
     */
    public static class MaxLab {
        /**
         * 最大相似的标签
         */
        public String maxLab;
        /**
         * 最大的相似度比率 1==100%
         */
        public float maxSimilar = 0;
        /**
         * 人脸截图
         */
        public Image face;
    }


    /**
     * 网络标签总数
     *
     * @return 标签总数
     */
    int labs();

    /**
     * 样本总数
     *
     * @return 样本总数
     */
    int features();

    /**
     * 销毁
     */
    void close();
}
