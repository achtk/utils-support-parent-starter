package com.chua.common.support.feature;

/**
 * 特征相似度计算
 *
 * @author CH
 * https://zhuanlan.zhihu.com/p/88117781?utm_source=wechat_session
 */
public class FeatureComparison {
    /**
     * cosin相似度比较(cpu版本)
     *
     * @param feature1 feature1
     * @param feature2 feature2
     * @return 返回相似度比例
     */
    public static float calculateSimilar(byte[] feature1, byte[] feature2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
            mod1 += feature1[i] * feature1[i];
            mod2 += feature2[i] * feature2[i];
        }
        return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }
    /**
     * cosin相似度比较(cpu版本)
     *
     * @param feature1 feature1
     * @param feature2 feature2
     * @return 返回相似度比例
     */
    public static float calculateSimilar(float[] feature1, float[] feature2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
            mod1 += feature1[i] * feature1[i];
            mod2 += feature2[i] * feature2[i];
        }
        return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }


    /**
     * 余弦相似度
     *
     * @param feature1 feature1
     * @param feature2 feature2
     * @return 余弦相似度
     */
    public static float cosineSim(float[] feature1, float[] feature2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
            mod1 += feature1[i] * feature1[i];
            mod2 += feature2[i] * feature2[i];
        }
        //    dot(x, y) / (np.sqrt(dot(x, x)) * np.sqrt(dot(y, y))))
        return (float) (ret / Math.sqrt(mod1) / Math.sqrt(mod2));
    }

    /**
     * 欧式距离
     *
     * @param feature1 feature1
     * @param feature2 feature2
     * @return 欧式距离
     */
    public static float dis(float[] feature1, float[] feature2) {
        float sum = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            sum += Math.pow(feature1[i] - feature2[i], 2);
        }
        return (float) Math.sqrt(sum);
    }

    /**
     * 内积
     *
     * @param feature1 feature1
     * @param feature2 feature2
     * @return 内积
     */
    public static float dot(float[] feature1, float[] feature2) {
        float ret = 0.0f;
        int length = feature1.length;
        // dot(x, y)
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
        }

        return ret;
    }
}
