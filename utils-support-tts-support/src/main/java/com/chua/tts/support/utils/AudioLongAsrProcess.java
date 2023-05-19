package com.chua.tts.support.utils;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

/**
 * 对音频预处理的工具
 */
public class AudioLongAsrProcess {
  public static NDArray processUtterance(NDManager manager, String path) throws Exception {
    // 获取音频的float数组
    float[] floatArray = AudioLongAsrArrayUtils.audioSegment(path).samples;
    // System.out.println(Arrays.toString(floatArray));

    // 提取语音片段的特征
    NDArray specgram = AudioFeaturizer.featurize(manager, floatArray);

    // 使用均值和标准值计算音频特征的归一化值
    String npzDataPath = "src/test/resources/mean_std.npz";
    specgram = FeatureNormalizer.apply(manager, npzDataPath, specgram);
    // System.out.println(specgram.toDebugString(1000000000, 1000, 10, 1000));

    return specgram;
  }

  public static NDArray processUtterance(NDManager manager, float[] floatArray) throws Exception {
    // 提取语音片段的特征
    NDArray specgram = AudioFeaturizer.featurize(manager, floatArray);

    // 使用均值和标准值计算音频特征的归一化值
    String npzDataPath = "src/test/resources/mean_std.npz";
    specgram = FeatureNormalizer.apply(manager, npzDataPath, specgram);
    // System.out.println(specgram.toDebugString(1000000000, 1000, 10, 1000));

    return specgram;
  }

  public static NDArray processUtterance(NDManager manager, NDArray array) throws Exception {
    // 提取语音片段的特征
    NDArray specgram = AudioFeaturizer.featurize(manager, array.toFloatArray());

    // 使用均值和标准值计算音频特征的归一化值
    String npzDataPath = "src/test/resources/mean_std.npz";
    specgram = FeatureNormalizer.apply(manager, npzDataPath, specgram);
    // System.out.println(specgram.toDebugString(1000000000, 1000, 10, 1000));

    return specgram;
  }
}
