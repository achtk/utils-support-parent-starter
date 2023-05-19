package com.chua.pytorch.support.image;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.detector.Detector;
import com.chua.pytorch.support.AbstractPytorchTrain;
import com.chua.pytorch.support.utils.LocationUtils;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 识别
 *
 * @author CH
 */
public class ImageDetector extends AbstractPytorchTrain<ImageDetector> implements Detector, AutoCloseable {

    private String modelPath;
    private ImageClassificationTranslator translator;
    private Model model;

    public ImageDetector(int width, int height, String modelPath) {
        super(width, height);
        this.width = width;
        this.height = height;
        this.modelPath = modelPath;
    }

    @SneakyThrows
    @Override
    public void afterPropertiesSet() {
        this.model = createModel(cs);
        //load the model
        model.load(Paths.get(modelPath), modelName);
        //define a translator for pre and post processing
        this.translator =
                ImageClassificationTranslator.builder()
                        .addTransform(new Resize(width, height))
                        .addTransform(new ToTensor())
                        .optApplySoftmax(true)
                        .build();
    }

    @Override
    public List<PredictResult> detect(Object face) {

        Image image = LocationUtils.getImage(face);
        if (null == image) {
            return Collections.emptyList();
        }

        List<PredictResult> results = new LinkedList<>();
        //run the inference using a Predictor
        try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
            Classifications classifications = predictor.predict(image);
            List<Classifications.Classification> classifications1 = classifications.topK(5);
            for (Classifications.Classification classification : classifications1) {
                results.add(LocationUtils.convertPredictResult(classification, image));
            }
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public void detect(Object image, OutputStream outputStream) {

    }

    @Override
    public void close() throws Exception {
        model.close();
    }

    @Override
    public void train(String trainPath, String modelPath) throws Exception {

    }
}
