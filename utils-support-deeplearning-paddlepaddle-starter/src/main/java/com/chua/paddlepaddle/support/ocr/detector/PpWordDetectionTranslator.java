package com.chua.paddlepaddle.support.ocr.detector;


import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.chua.common.support.feature.DetectionConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author ACHTK
 */
public class PpWordDetectionTranslator implements Translator<Image, DetectedObjects> {

    private final int maxSideLen;

    private DetectionConfiguration configuration;

    public PpWordDetectionTranslator(Map<String, ?> arguments, DetectionConfiguration configuration) {
        maxSideLen =
                arguments.containsKey("maxLength")
                        ? Integer.parseInt(arguments.get("maxLength").toString())
                        : 960;
        this.configuration = configuration;
    }

    @Override
    public DetectedObjects processOutput(TranslatorContext ctx, NDList list) {
        NDArray result = list.singletonOrThrow();
        result = result.squeeze().mul(255f).toType(DataType.UINT8, true).gt(0.3);
        boolean[] flattened = result.toBooleanArray();
        Shape shape = result.getShape();
        int w = (int) shape.get(0);
        int h = (int) shape.get(1);
        boolean[][] grid = new boolean[w][h];
        IntStream.range(0, flattened.length)
                .parallel()
                .forEach(i -> grid[i / h][i % h] = flattened[i]);
        List<BoundingBox> boxes = new BoundFinderV2(configuration, grid).getBoxes();
        List<String> names = new ArrayList<>();
        List<Double> probs = new ArrayList<>();
        int boxSize = boxes.size();
        for (int i = 0; i < boxSize; i++) {
            names.add("word");
            probs.add(1.0);
        }
        return new DetectedObjects(names, probs, boxes);
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray img = input.toNDArray(ctx.getNDManager());
        int h = input.getHeight();
        int w = input.getWidth();
        int resizeW = w;
        int resizeH = h;

        // limit the max side
        float ratio = 1.0f;
        if (Math.max(resizeH, resizeW) > maxSideLen) {
            if (resizeH > resizeW) {
                ratio = (float) maxSideLen / (float) resizeH;
            } else {
                ratio = (float) maxSideLen / (float) resizeW;
            }
        }

        resizeH = (int) (resizeH * ratio);
        resizeW = (int) (resizeW * ratio);

        if (resizeH % 32 == 0) {
            resizeH = resizeH;
        } else if (Math.floor((float) resizeH / 32f) <= 1) {
            resizeH = 32;
        } else {
            resizeH = (int) Math.floor((float) resizeH / 32f) * 32;
        }

        if (resizeW % 32 == 0) {
            resizeW = resizeW;
        } else if (Math.floor((float) resizeW / 32f) <= 1) {
            resizeW = 32;
        } else {
            resizeW = (int) Math.floor((float) resizeW / 32f) * 32;
        }

        img = NDImageUtils.resize(img, resizeW, resizeH);
        img = NDImageUtils.toTensor(img);
        img =
                NDImageUtils.normalize(
                        img,
                        new float[]{0.485f, 0.456f, 0.406f},
                        new float[]{0.229f, 0.224f, 0.225f});
        img = img.expandDims(0);
        return new NDList(img);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

}