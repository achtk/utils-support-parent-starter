package com.chua.paddlepaddle.support.ocr.recognizer;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.paddlepaddle.support.ocr.detector.OcrDetector;
import com.chua.paddlepaddle.support.ocr.rotation.OcrDirectionDetector;
import com.chua.pytorch.support.AbstractPytorchRecognizer;
import com.chua.pytorch.support.utils.LocationUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * opencv
 *
 * @author CH
 */
@Spi("ocr")
public class OcrRecognizer extends AbstractPytorchRecognizer<String> {

    private final Detector directionDetector;
    public OcrRecognizer(DetectionConfiguration configuration) {
        this(new OcrDirectionDetector(configuration), new OcrDetector(configuration), configuration);
    }
    public OcrRecognizer(Detector directionDetector, Detector detector, DetectionConfiguration configuration) {
        super(detector, configuration,
                new PpWordRecognitionTranslator(new ConcurrentHashMap<>()),
                "PaddlePaddle",
                null,
                "ch_PP-OCRv3_rec_infer",
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/ocr_models/ch_PP-OCRv3_rec_infer.zip",
                true);

        this.directionDetector = directionDetector;
    }

    @Override
    public List<PredictResult> predict(Object face) {
        List<PredictResult> detect = detector.predict(face);

        if (detect.isEmpty()) {
            return Collections.emptyList();
        }

        List<PredictResult> rs = new LinkedList<>();
        Image image = LocationUtils.getImage(face);

        try (Predictor<Image, String> predictor = model.newPredictor()) {
            for (PredictResult predictResult : detect) {
                doAnalysis(rs, predictResult, predictor, image);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    private void doAnalysis(List<PredictResult> rs, PredictResult predictResult, Predictor<Image, String> predictor, Image image) {
        Image subImg = LocationUtils.getSubImage(image, (BoundingBox) predictResult.getBoundingBox());
        if (subImg.getHeight() * 1.0 / subImg.getWidth() > 1.5) {
            subImg = LocationUtils.rotateImg(subImg);
        }

        List<PredictResult> detect = directionDetector.predict(subImg);
        if (detect.isEmpty()) {
            return;
        }

        PredictResult result = detect.get(0);

        if ("Rotate".equals(result.getClsLabel()) && result.getClsScore() > 0.8) {
            subImg = LocationUtils.rotateImg(subImg);
        }

        try {
            String name = predictor.predict(subImg);
            PredictResult predictResult1 = new PredictResult();
            predictResult1.setText(name);
            predictResult1.setScore((float) predictResult.getScore());
            predictResult1.setClsLabel(predictResult.getClsLabel());
            predictResult1.setClsScore((float) predictResult.getScore());
            predictResult1.setBoundingBox(LocationUtils.toBoundingBox((BoundingBox) predictResult.getBoundingBox()));

            rs.add(predictResult1);
        } catch (TranslateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected PredictResult createPredictResult(Image subImage) {
        return null;
    }

    @Override
    protected Class<String> type() {
        return String.class;
    }

    private float distance(float[] point1, float[] point2) {
        float disX = point1[0] - point2[0];
        float disY = point1[1] - point2[1];
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    private Image rotateImg(Image image) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray rotated = NDImageUtils.rotate90(image.toNDArray(manager), 1);
            return ImageFactory.getInstance().fromNDArray(rotated);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
