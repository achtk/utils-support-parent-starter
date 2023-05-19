package com.chua.tts.support.asr;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Administrator
 */
public final class AudioTranslator implements Translator<NDArray, Pair> {
    private List<String> vocabulary = null;

    AudioTranslator() {
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("zh_vocab.txt").openStream()) {
            vocabulary = Utils.readLines(is, true);
        }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, NDArray audioFeature) {
        NDManager manager = ctx.getNDManager();

        long audioLen = audioFeature.getShape().get(1);
        long maskShape0 = (audioFeature.getShape().get(0) - 1) / 2 + 1;
        long maskShape1 = (audioFeature.getShape().get(1) - 1) / 3 + 1;
        long maskMaxLen = (audioLen - 1) / 3 + 1;

        NDArray maskOnes = manager.ones(new Shape(maskShape0, maskShape1));
        NDArray maskZeros = manager.zeros(new Shape(maskShape0, maskMaxLen - maskShape1));
        NDArray maskArray = NDArrays.concat(new NDList(maskOnes, maskZeros), 1);
        maskArray = maskArray.reshape(1, maskShape0, maskMaxLen);
        NDList list = new NDList();
        for (int i = 0; i < 32; i++) {
            list.add(maskArray);
        }
        NDArray mask = NDArrays.concat(list, 0);

        NDArray audioData = audioFeature.expandDims(0);
        NDArray seqLenData = manager.create(new long[]{audioLen});
        NDArray masks = mask.expandDims(0);
        //    System.out.println(maskArray.toDebugString(1000000000, 1000, 10, 1000));
        return new NDList(audioData, seqLenData, masks);
    }

    @Override
    public Pair processOutput(TranslatorContext ctx, NDList list) {
        NDArray probsSeq = list.singletonOrThrow();
        return CTCGreedyDecoder.greedyDecoder(ctx.getNDManager(), probsSeq, vocabulary, 0);
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}
