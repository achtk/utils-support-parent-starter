package com.chua.tensorflow.support.resolution;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

/**
 * SuperResolutionTranslator
 *
 * @author CH
 */
public class SuperResolutionTranslator implements Translator<Image, Image> {

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) throws Exception {
        NDManager manager = ctx.getNDManager();
        return new NDList(input.toNDArray(manager).toType(DataType.FLOAT32, false));
    }

    @Override
    public Image processOutput(TranslatorContext ctx, NDList list) throws Exception {
        NDArray output = list.get(0).clip(0, 255).toType(DataType.UINT8, false);
        return ImageFactory.getInstance().fromNDArray(output.squeeze());
    }

    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}
