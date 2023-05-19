package com.chua.tts.support.voiceprint;

import ai.djl.ndarray.NDManager;
import com.chua.common.support.feature.Feature;
import com.chua.tts.support.utils.JLibrasaEx;

/**
 * 声纹
 *
 * @author CH
 */
public class VoiceFeature implements Feature<float[][]> {

    @Override
    public float[][] predict(Object img) {
        NDManager manager = NDManager.newBaseManager();
        try {
            return JLibrasaEx.magnitude(manager, img.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
