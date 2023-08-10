package com.chua.paddlepaddle.support.face.mask;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.detector.Detector;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 口罩
 *
 * @author CH
 */
public class PaddlePaddleMaskDetector extends AbstractPytorchDetector<DetectedObjects> {
    private Detector detector;
    private ZooModel<Image, Classifications> maskModel;

    public PaddlePaddleMaskDetector(DetectionConfiguration configuration, Detector detector) throws Exception {
        super(null, null, null, null, false);
        this.detector = detector;
        this.maskModel = ModelZoo.loadModel(criteria());
    }


    public Criteria<Image, Classifications> criteria() {
        List<String> model1 = LocationUtils.getUrl("face_mask", "https://aias-home.oss-cn-beijing.aliyuncs.com/models/face_mask/face_mask.zip");

        Criteria<Image, Classifications> criteria =
                Criteria.builder()
                        .optEngine("PaddlePaddle")
                        .setTypes(Image.class, Classifications.class)
                        .optTranslator(
                                ImageClassificationTranslator.builder()
                                        .addTransform(new Resize(128, 128))
                                        .addTransform(new ToTensor())
                                        .addTransform(
                                                new Normalize(
                                                        new float[]{0.5f, 0.5f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f}))
                                        .addTransform(nd -> nd.flip(0))
                                        .build())
                        .optModelUrls(
                                Joiner.on(',').join(model1))
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }

    private int[] extendSquare(
            double xmin, double ymin, double width, double height, double percentage) {
        double centerx = xmin + width / 2;
        double centery = ymin + height / 2;
        double maxDist = Math.max(width / 2, height / 2) * (1 + percentage);
        return new int[]{(int) (centerx - maxDist), (int) (centery - maxDist), (int) (2 * maxDist)};
        //    return new int[] {(int) xmin, (int) ymin, (int) width, (int) height};
    }

    private Image getSubImage(Image img, BoundingBox box) {
        Rectangle rect = box.getBounds();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] squareBox =
                extendSquare(
                        rect.getX() * width,
                        rect.getY() * height,
                        rect.getWidth() * width,
                        rect.getHeight() * height,
                        0); // 0.18
        return img.getSubImage(squareBox[0], squareBox[1], squareBox[2], squareBox[2]);
        //    return img.getSubimage(squareBox[0], squareBox[1], squareBox[2], squareBox[3]);
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects o, Image img) {
        return null;
    }


    @Override
    public List<PredictResult> predict(Object face) {
        Image img = LocationUtils.getImage(face);
        if (null == img) {
            return Collections.emptyList();
        }

        List<PredictResult> results = new LinkedList<>();
        try (
                Predictor<Image, Classifications> predictor = maskModel.newPredictor();
        ) {
            List<PredictResult> faces = detector.predict(img);

            for (PredictResult face1 : faces) {
                Image subImg = getSubImage(img, LocationUtils.getBoundingBox(face1));
                try {
                    Classifications classifications = predictor.predict(subImg);
                    Classifications.Classification best = classifications.best();
                    PredictResult predictResult = LocationUtils.convertPredictResult(best, img);

                    results.add(predictResult);
                } catch (TranslateException ignored) {
                }
            }
        }

        return results;
    }

    @SneakyThrows
    @Override
    public void detect(Object face, OutputStream outputStream) {

        Image img = LocationUtils.getImage(face);
        if (null == img) {
            return;
        }
        try (
                Predictor<Image, Classifications> predictor = maskModel.newPredictor();
        ) {
            List<PredictResult> faces = detector.detect(img);
            List<String> names = new ArrayList<>();
            List<Double> prob = new ArrayList<>();
            List<BoundingBox> rect = new ArrayList<>();

            for (PredictResult face1 : faces) {
                Image subImg = getSubImage(img, LocationUtils.getBoundingBox(face1));
                try {
                    Classifications classifications = predictor.predict(subImg);
                    Classifications.Classification best = classifications.best();
                    names.add(classifications.best().getClassName());
                    prob.add(best.getProbability());
                    rect.add(LocationUtils.getBoundingBox(face1));

                } catch (TranslateException ignored) {
                }
            }
            LocationUtils.saveBoundingBoxImage(img, new DetectedObjects(names, prob, rect), outputStream);
        }
    }


    @Override
    public void close() throws Exception {
        super.close();
        maskModel.close();
    }
}
