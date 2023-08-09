package com.chua.paddlepaddle.support.face.land;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

/**
 * @author CH
 */
public class FaceLandmarkTranslator implements Translator<Image, float[][]> {

    FaceLandmarkTranslator() {
    }

    @Override
    public float[][] processOutput(TranslatorContext ctx, NDList list) {
        NDList result = new NDList();
        long numOutputs = list.singletonOrThrow().getShape().get(0);
        for (int i = 0; i < numOutputs; i++) {
            result.add(list.singletonOrThrow().get(i));
        }
        return result.stream().map(NDArray::toFloatArray).toArray(float[][]::new);
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        // 转灰度图
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.GRAYSCALE);
        Shape shape = array.getShape();

        array = NDImageUtils.resize(array, 60, 60, Image.Interpolation.BICUBIC);

        NDArray mean = array.mean();
        double std = std(array);

        // HWC -> CHW
        array = array.transpose(2, 0, 1);
        // normalization
        array = array.sub(mean).div(std);
        // make batch dimension
        array = array.expandDims(0);
        return new NDList(array);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

    // 计算全局标准差
    private float std(NDArray points) {
        byte[] arr = points.toByteArray();
        float std = 0;
        for (int i = 0; i < arr.length; i++) {
            std = std + (float) Math.pow(arr[i], 2);
        }
        std = (float) Math.sqrt(std / arr.length);
        return std;
    }
}