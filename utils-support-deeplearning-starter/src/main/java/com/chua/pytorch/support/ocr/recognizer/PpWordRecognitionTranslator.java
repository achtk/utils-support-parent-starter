package com.chua.pytorch.support.ocr.recognizer;


import ai.djl.Model;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ACHTK
 */
public class PpWordRecognitionTranslator implements Translator<Image, String> {
    private final boolean useSpaceChar;
    private List<String> table;

    public PpWordRecognitionTranslator(Map<String, ?> arguments) {
        useSpaceChar =
                arguments.containsKey("use_space_char")
                        ? Boolean.parseBoolean(arguments.get("use_space_char").toString())
                        : false;
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("ppocr_keys_v1.txt").openStream()) {
            table = Utils.readLines(is, true);
            table.add(0, "blank");
            if (useSpaceChar) {
                table.add(" ");
            } else {
                table.add("");
            }
        }
    }

    @Override
    public String processOutput(TranslatorContext ctx, NDList list) throws IOException {
        StringBuilder sb = new StringBuilder();
        NDArray tokens = list.singletonOrThrow();

        long[] indices = tokens.get(0).argMax(1).toLongArray();
        boolean[] selection = new boolean[indices.length];
        Arrays.fill(selection, true);
        for (int i = 1; i < indices.length; i++) {
            if (indices[i] == indices[i - 1]) {
                selection[i] = false;
            }
        }

        // 字符置信度
//        float[] probs = new float[indices.length];
//        for (int row = 0; row < indices.length; row++) {
//            NDArray value = tokens.get(0).get(new NDIndex(""+ row +":" + (row + 1) +"," + indices[row] +":" + ( indices[row] + 1)));
//            probs[row] = value.toFloatArray()[0];
//        }

        int lastIdx = 0;
        for (int i = 0; i < indices.length; i++) {
            if (selection[i] == true && indices[i] > 0 && !(i > 0 && indices[i] == lastIdx)) {
                sb.append(table.get((int) indices[i]));
            }
        }
        return sb.toString();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) {
        NDArray img = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
        int imgC = 3;
        int imgH = 48;
        int imgW = 320;//192 320

        int h = input.getHeight();
        int w = input.getWidth();
        float ratio = (float) w / (float) h;
        imgW = (int) (imgH * ratio);

        int resizedW;
        if (Math.ceil(imgH * ratio) > imgW) {
            resizedW = imgW;
        } else {
            resizedW = (int) (Math.ceil(imgH * ratio));
        }
        img = NDImageUtils.resize(img, resizedW, imgH);
        img = img.transpose(2, 0, 1).div(255).sub(0.5f).div(0.5f);
        NDArray paddingIm = ctx.getNDManager().zeros(new Shape(imgC, imgH, imgW), DataType.FLOAT32);
        paddingIm.set(new NDIndex(":,:,0:" + resizedW), img);

        paddingIm = paddingIm.expandDims(0);
        return new NDList(paddingIm);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

}