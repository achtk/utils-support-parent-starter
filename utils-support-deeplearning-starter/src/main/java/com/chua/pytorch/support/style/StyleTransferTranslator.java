package com.chua.pytorch.support.style;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

/**
 * style
 *
 * @author CH
 */
public class StyleTransferTranslator implements Translator<Image, Image> {

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray image = switchFormat(input.toNDArray(ctx.getNDManager())).expandDims(0);
        return new NDList(image.toType(DataType.FLOAT32, false));
    }

    @Override
    public Image processOutput(TranslatorContext ctx, NDList list) {
        NDArray ndArray = list.get(0);
        NDArray output = ctx.getNDManager().create(ndArray.toFloatArray(), ndArray.getShape()).addi(1).muli(128).toType(DataType.UINT8, false);
        return ImageFactory.getInstance().fromNDArray(output.squeeze());
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

    private NDArray switchFormat(NDArray array) {
        return NDArrays.stack(array.split(3, 2)).squeeze();
    }
}
