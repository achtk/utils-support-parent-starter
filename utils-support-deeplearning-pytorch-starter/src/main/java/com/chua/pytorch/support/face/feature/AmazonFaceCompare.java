package com.chua.pytorch.support.face.feature;

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
public class AmazonFaceCompare implements Compare {
    private final AmazonFeature amazonFeature;
    private final Similar<float[]> similar = new CosinSimilar();

    public AmazonFaceCompare(DetectionConfiguration configuration) {
        this.amazonFeature = new AmazonFeature(configuration);
    }
    @Override
    public float calculateSimilar(Object t1, Object t2) {
        return similar.calculateSimilar(amazonFeature.predict(t1), amazonFeature.predict(t1));
    }
}
