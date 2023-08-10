package com.chua.paddlepaddle.support.face.land;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 关键点
 *
 * @author CH
 */
@Spi("FaceLandmark")
public class FaceLandmarkDetector extends AbstractPytorchDetector<float[][]> {

    private final Detector detector;

    public FaceLandmarkDetector(DetectionConfiguration configuration, Detector detector) {
        super(configuration,
                new FaceLandmarkTranslator(),
                "PaddlePaddle",
                "inference",
                StringUtils.defaultString(configuration.modelPath(), "face_landmark"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/face_landmark.zip",
                true);
        this.detector = detector;
    }

    @Override
    protected Class<float[][]> type() {
        return float[][].class;
    }

    @Override
    public List<PredictResult> predict(Object face) {
        List<PredictResult> detect = detector.predict(face);
        if (CollectionUtils.isEmpty(detect)) {
            return Collections.emptyList();
        }

        List<PredictResult> results = new LinkedList<>();
        Image img = LocationUtils.getImage(face);
        for (PredictResult predictResult : detect) {

            try (Predictor<Image, float[][]> predictor = model.newPredictor()) {
                Image subImage = LocationUtils.getSubImage(img, LocationUtils.getBoundingBox(predictResult), 0f);
                float[][] o = predictor.predict(subImage);
                PredictResult item = new PredictResult();
                BeanUtils.copyProperties(predictResult, item);
                item.setNdArray(Json.toJson(o));

                results.add(item);
            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
        }

        return results;
    }

    @Override
    public void detect(Object face, OutputStream outputStream) throws Exception {
        List<PredictResult> detect = detector.predict(face);
        if (CollectionUtils.isEmpty(detect)) {
            return;
        }

        Image img = LocationUtils.getImage(face);
        Image image = img.duplicate();
        for (PredictResult predictResult : detect) {

            try (Predictor<Image, float[][]> predictor = model.newPredictor()) {
                Image subImage = LocationUtils.getSubImage(img, LocationUtils.getBoundingBox(predictResult), 0f);
                float[][] o = predictor.predict(subImage);
                LocationUtils.drawLandmark(image, LocationUtils.getBoundingBox(predictResult), o[0]);

            } catch (TranslateException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            ImageIO.write((RenderedImage) image.getWrappedImage(), "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected List<PredictResult> toDetect(float[][] o, Image img) {
        return null;
    }


    @Override
    public void close() throws Exception {
        super.close();
        detector.close();
    }
}
