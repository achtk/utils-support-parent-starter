package com.chua.paddlepaddle.support.face.feature;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.function.Compare;
import com.chua.common.support.lang.function.CosinSimilar;
import com.chua.common.support.lang.function.Similar;

import java.util.Collections;
import java.util.List;

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
    public List<PredictResult> calculateSimilar(Object t1, Object t2) {
        return Collections.singletonList(new PredictResult().setScore(similar.calculateSimilar(paddleFeature.predict(t1), paddleFeature.predict(t1))));
    }
}
