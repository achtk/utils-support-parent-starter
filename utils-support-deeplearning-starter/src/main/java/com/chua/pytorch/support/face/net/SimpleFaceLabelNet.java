package com.chua.pytorch.support.face.net;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.FeatureComparison;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.pytorch.support.utils.LocationUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * p-net
 *
 * @author CH
 */
@Spi("simple")
public class SimpleFaceLabelNet implements FaceLabelNet {
    private static final long serialVersionUID = 1;
    /**
     * 缓冲着所有分类lab的特征数据
     */
    private final Map<String, Set<float[]>> models = new HashMap<String, Set<float[]>>();
    private int featureCount = 0;
    private DetectionConfiguration configuration;

    public SimpleFaceLabelNet(DetectionConfiguration configuration) {
        load(configuration.labelModel());
        this.configuration = configuration;
    }

    /**
     * 添加一个特征值
     *
     * @param lab     标签
     * @param feature 特征
     */
    @Override
    public synchronized void addFeature(String lab, float[] feature) {
        Set<float[]> features = models.computeIfAbsent(lab, k -> new HashSet<>());
        featureCount++;
        features.add(feature);
    }

    /**
     * 推理获取最大的lab和最大相似度
     *
     * @param feature   特征
     * @param threshold 阈值
     * @return 最大的lab和最大相似度
     */
    @Override
    public MaxLab predict(float[] feature, double threshold) {
        MaxLab maxLab = new MaxLab();
        for (Map.Entry<String, Set<float[]>> entry : models.entrySet()) {
            String lab = entry.getKey();
            Set<float[]> features = entry.getValue();
            //在改lab下全部对比一遍
            float maxSimilar = 0;
            if (features != null && features.size() > 0) {
                //最多比较10个样本，如果都小于0.85的不用再比较当前lab
                int min = 20;
                if (features.size() < min) {
                    min = features.size();
                }

                boolean flag = true;
                for (int i = 0; i < features.size(); i++) {
                    float[] tem = CollectionUtils.find(features, i);
                    float temSimilar = FeatureComparison.calculateSimilar(feature, tem);
                    if (temSimilar > maxSimilar) {
                        maxSimilar = temSimilar;
                    }

                    if (i > min && flag) {
                        flag = false;

                        if (maxSimilar < threshold) {
                            //不用再比该lab
                            return null;
                        }
                    }
                }
            }

            //本次lab对比是否是最大值
            if (maxSimilar > maxLab.maxSimilar) {
                maxLab.maxSimilar = maxSimilar;
                maxLab.maxLab = lab;
            }
        }

        return maxLab;
    }

    /**
     * 加载磁盘上的网络对象
     *
     * @param name 名称
     */
    @Override
    public synchronized void load(String name) {
        List<String> url = LocationUtils.getUrl(name);
        if (url.isEmpty()) {
            return;
        }

        for (String s : url) {
            try (ObjectInputStream ois = new ObjectInputStream(new URI(s).toURL().openStream());) {
                SimpleFaceLabelNet simpleFaceNet = (SimpleFaceLabelNet) ois.readObject();
                mega(simpleFaceNet.models());
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private synchronized void mega(Map<String, Set<float[]>> models) {
        for (Map.Entry<String, Set<float[]>> entry : models.entrySet()) {
            String key = entry.getKey();
            Set<float[]> value = entry.getValue();
            for (float[] floats : value) {
                addFeature(key, floats);
            }
        }
    }

    /**
     * 保存网络到磁盘上
     *
     * @param name name
     */
    @Override
    public void saveNet(String name) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(name)))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Set<float[]>> models() {
        return models;
    }


    /**
     * 网络标签总数
     *
     * @return 标签总数
     */
    @Override
    public int labs() {
        return models.size();
    }

    /**
     * 样本总数
     *
     * @return 样本总数
     */
    @Override
    public int features() {
        return featureCount;
    }

    /**
     * 销毁
     */
    @Override
    public void close() {
        models.clear();
    }
}
