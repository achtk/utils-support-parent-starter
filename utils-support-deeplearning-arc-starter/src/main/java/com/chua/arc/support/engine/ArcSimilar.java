package com.chua.arc.support.engine;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceSimilar;
import com.chua.common.support.lang.function.Similar;

/**
 * 相似度比
 */
public class ArcSimilar implements Similar<byte[]> {
    private final FaceEngine faceEngine;

    public ArcSimilar(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }

    @Override
    public float calculateSimilar(byte[] t1, byte[] t2) {
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(t1);
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(t2);
        FaceSimilar faceSimilar = new FaceSimilar();

        faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);

        return faceSimilar.getScore();
    }
}
