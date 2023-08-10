package com.chua.pytorch.support.face.feature;

import ai.djl.modality.cv.Image;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.function.Compare;
import com.chua.common.support.lang.function.CosinSimilar;
import com.chua.common.support.lang.function.Similar;
import com.chua.pytorch.support.face.detector.RetinaFaceDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * face Compare
 * @author CH
 */
@Spi("FaceCompare")
public class AmazonFaceCompare implements Compare {
    private final AmazonFeature amazonFeature;
    private final Similar<float[]> similar = new CosinSimilar();
    private final RetinaFaceDetector retinaFaceDetector;

    public AmazonFaceCompare(DetectionConfiguration configuration) {
        this.amazonFeature = new AmazonFeature(configuration);
        this.retinaFaceDetector = new RetinaFaceDetector(configuration);
    }
    @Override
    public List<PredictResult> calculateSimilar(Object t1, Object t2) {
        List<PredictResult> rs = new LinkedList<>();
        Map<PredictResult, Image> cache = new HashMap<>();
        List<PredictResult> predict = retinaFaceDetector.predict(t1);
        if(predict.isEmpty()) {
            rs.add(PredictResult.empty());
            return rs;
        }
        List<PredictResult> predict1 = retinaFaceDetector.predict(t2);
        if(predict1.isEmpty()) {
            rs.add(PredictResult.empty());
            return rs;
        }
        for (PredictResult predictResult : predict) {
            Image subImage = LocationUtils.getSubImage(LocationUtils.getImage(t1), predictResult.getBoundingBox());
            for (PredictResult predictResult2 : predict1) {
                Image image = cache.computeIfAbsent(predictResult2, new Function<PredictResult, Image>() {
                    @Override
                    public Image apply(PredictResult predictResult) {
                        return LocationUtils.getSubImage(LocationUtils.getImage(t2), predictResult.getBoundingBox());
                    }
                });
                PredictResult item = new PredictResult();
                float similar1 = similar.calculateSimilar(amazonFeature.predict(subImage), amazonFeature.predict(image));
                item.setScore(similar1);
                item.setClsScore(similar1);
                item.setSign1(predictResult.getBoundingBox());
                item.setSign2(predictResult2.getBoundingBox());

                rs.add(item);
            }
        }

        return rs;
    }
}
