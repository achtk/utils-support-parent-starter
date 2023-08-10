package com.chua.pytorch.support.feature;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.function.Compare;
import com.chua.common.support.lang.function.CosinSimilar;
import com.chua.common.support.lang.function.Similar;

import java.util.Collections;
import java.util.List;

/**
 * 文本相似度
 * @author CH
 */
@Spi("TextCompare")
public class TextCompare implements Compare {

    private final TextFeature textFeature;
    private final Similar<float[]> similar = new CosinSimilar();

    public TextCompare(DetectionConfiguration configuration) {
        this.textFeature = new TextFeature(configuration);
    }
    @Override
    public List<PredictResult> calculateSimilar(Object t1, Object t2) {
        return Collections.singletonList(new PredictResult().setScore(similar.calculateSimilar(textFeature.predict(t1), textFeature.predict(t1))));
    }
}
