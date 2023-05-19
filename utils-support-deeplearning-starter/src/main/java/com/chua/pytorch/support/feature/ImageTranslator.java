package com.chua.pytorch.support.feature;

import ai.djl.Model;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;

/**
 * ImageTranslator
 *
 * @author CH
 */
public class ImageTranslator implements Translator<Image, float[]> {

    public void prepare(NDManager manager, Model model) throws IOException {
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        return list.get(0).toFloatArray();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) throws Exception {
        NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);

        float percent = 224f / Math.min(input.getWidth(), input.getHeight());
        int resizedWidth = Math.round(input.getWidth() * percent);
        int resizedHeight = Math.round(input.getHeight() * percent);

        array = NDImageUtils.resize(array, resizedWidth, resizedHeight, Image.Interpolation.BICUBIC);
        array = NDImageUtils.centerCrop(array, 224, 224);

        // The network by default takes float32
        if (!array.getDataType().equals(DataType.FLOAT32)) {
            array = array.toType(DataType.FLOAT32, false);
        }

        array = array.transpose(2, 0, 1).div(255f); // HWC -> CHW RGB

        NDArray mean =
                ctx.getNDManager()
                        .create(new float[]{0.48145466f, 0.4578275f, 0.40821073f}, new Shape(3, 1, 1));
        NDArray std =
                ctx.getNDManager()
                        .create(new float[]{0.26862954f, 0.26130258f, 0.27577711f}, new Shape(3, 1, 1));

        array = array.sub(mean);
        array = array.div(std);

        array = array.expandDims(0);
        float[] a = array.toFloatArray();
        float[] b = new float[224];
        for (int i = 0; i < 224; i++) {
            b[i] = a[a.length - 224 - 1 + i];
        }
        return new NDList(array);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}
