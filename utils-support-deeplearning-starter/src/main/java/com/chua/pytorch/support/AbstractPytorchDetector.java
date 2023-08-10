package com.chua.pytorch.support;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.pytorch.support.utils.LocationUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 特征值
 *
 * @author CH
 */
public abstract class AbstractPytorchDetector<O> extends AbstractPytorchIODetector<Image, O> {

    public AbstractPytorchDetector(DetectionConfiguration configuration,
                                   Translator<Image, O> translator,
                                   String model,
                                   String defaultModel,
                                   boolean isDirector) {
        this(configuration, translator, "PyTorch", null, model, defaultModel, isDirector);
    }

    public AbstractPytorchDetector(DetectionConfiguration configuration,
                                   Translator<Image, O> translator,
                                   String modelName,
                                   String model,
                                   String defaultModel,
                                   boolean isDirector) {
        this(configuration, translator, "PyTorch", modelName, model, defaultModel, isDirector);

    }

    public AbstractPytorchDetector(DetectionConfiguration configuration,
                                   Translator<Image, O> translator,
                                   String engine,
                                   String modelName,
                                   String model,
                                   String defaultModel,
                                   boolean isDirector) {
        super(configuration, translator, engine, modelName, model, defaultModel, isDirector);
    }


    @Override
    public List<PredictResult> predict(Object face) {
        Image img = LocationUtils.getImage(face);
        if (null == img) {
            return Collections.emptyList();
        }


        try (Predictor<Image, O> predictor = model.newPredictor()) {
            O o = predictor.predict(img);
            return toDetect(o, img);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @SuppressWarnings("ALL")
    public void detect(Object face, OutputStream outputStream) throws Exception {
        List<PredictResult> detect = predict(face);
        if (CollectionUtils.isEmpty(detect)) {
            return;
        }

        Image image = LocationUtils.getImage(face);
        Image duplicate = image.duplicate();

        List<String> names = new ArrayList<>();
        List<Double> prob = new ArrayList<>();
        List<BoundingBox> rect = new ArrayList<>();
        for (PredictResult item : detect) {
            names.add(item.getClsLabel() + " " + item.getScore());
            prob.add(Double.valueOf(item.getScore()));
            rect.add((BoundingBox) item.getBoundingBox());
        }
        DetectedObjects detections = new DetectedObjects(names, prob, rect);
        LocationUtils.saveBoundingBoxImage(duplicate, detections, outputStream);
    }

    @Override
    public void close() throws Exception {
        model.close();
    }

    @Override
    protected Class<O> outType() {
        return type();
    }

    @Override
    protected Class<Image> inType() {
        return Image.class;
    }

    /**
     * 输出类型
     *
     * @return 输出类型
     */
    protected abstract Class<O> type();

    /**
     * 检测
     *
     * @param o   结果
     * @param img 图片
     * @return 检测结果
     */
    protected abstract List<PredictResult> toDetect(O o, Image img);

}
