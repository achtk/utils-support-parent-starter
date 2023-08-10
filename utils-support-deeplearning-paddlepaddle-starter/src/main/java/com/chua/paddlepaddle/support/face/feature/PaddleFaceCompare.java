package com.chua.paddlepaddle.support.face.feature;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.function.Compare;
import com.chua.common.support.lang.function.CosinSimilar;
import com.chua.common.support.lang.function.Similar;

/**
 * face Compare
 * @author CH
 */
@Spi("FaceCompare")
public class PaddleFaceCompare implements Compare {
    private final PaddleFeature paddleFeature;
    private final Similar<float[]> similar = new CosinSimilar();

    public PaddleFaceCompare(DetectionConfiguration configuration) {
        this.paddleFeature = new PaddleFeature(configuration);
    }
    @Override
    public float calculateSimilar(Object t1, Object t2) {
        return similar.calculateSimilar(paddleFeature.predict(t1), paddleFeature.predict(t1));
    }
}
