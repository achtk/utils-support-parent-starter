package com.chua.tts.support.asr;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import com.chua.common.support.constant.Projects;
import com.chua.tts.support.utils.AudioArrayAsrUtils;
import com.chua.tts.support.utils.AudioFeaturizer;
import com.chua.tts.support.utils.FeatureNormalizer;

import java.io.File;

/**
 * 对音频预处理的工具
 */
public class AudioProcess {
    public static NDArray processUtterance(NDManager manager, String path) throws Exception {
        // 获取音频的float数组
        float[] floatArray = AudioArrayAsrUtils.audioSegment(path).samples;
        // System.out.println(Arrays.toString(floatArray));

        // 提取语音片段的特征
        NDArray specgram = AudioFeaturizer.featurize(manager, floatArray);

        // 使用均值和标准值计算音频特征的归一化值
        String npzDataPath = "mean_std.npz";
        File file = Projects.copyTempFile(npzDataPath);
        specgram = FeatureNormalizer.apply(manager, file.getAbsolutePath(), specgram);

        return specgram;
    }
}
