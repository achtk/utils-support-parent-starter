package com.chua.paddlepaddle.support.ocr.detector;


import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.paddlepaddle.engine.PpNDArray;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author ACHTK
 */
public final class SimnetBowTranslator implements Translator<String[][], Float> {
    private Map<String, String> word2IdDict = new HashMap<String, String>();
    private String unkId = "";

    SimnetBowTranslator() {
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("vocab.txt").openStream()) {
            List<String> words = Utils.readLines(is, true);
            words.stream()
                    .filter(word -> (word != null && word != ""))
                    .forEach(
                            word -> {
                                String[] ws = word.split("	");
                                if (ws.length == 1) {
                                    //                  word2id_dict.put("", ws[0]); // 文字是key,id是value
                                } else {
                                    // 文字是key,id是value
                                    word2IdDict.put(ws[0], ws[1]);
                                }
                            });
        }
        unkId = "" + word2IdDict.size(); // 文字是key,id是value
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String[][] input) {

        NDManager manager = ctx.getNDManager();
        List<Long> lodList = new ArrayList<>(0);
        lodList.add(0L);
        long[] array = tokenizeSingleString(manager, input[0], lodList);
        NDArray ndArray1 = manager.create(array, new Shape(lodList.get(lodList.size() - 1), 1));

        ndArray1.setName("words");
        long[][] lod = new long[1][2];
        lod[0][0] = 0;
        lod[0][1] = lodList.get(lodList.size() - 1);
        ((PpNDArray) ndArray1).setLoD(lod);

        lodList = new ArrayList<>(0);
        lodList.add(0L);
        array = tokenizeSingleString(manager, input[1], lodList);
        NDArray ndArray2 = manager.create(array, new Shape(lodList.get(lodList.size() - 1), 1));

        ndArray2.setName("words");
        lod = new long[1][2];
        lod[0][0] = 0;
        lod[0][1] = lodList.get(lodList.size() - 1);
        ((PpNDArray) ndArray2).setLoD(lod);

        return new NDList(ndArray1, ndArray2);
    }

    @Override
    public Float processOutput(TranslatorContext ctx, NDList list) {
        float[] embedding = list.get(0).toFloatArray();
        System.out.println("embedding:  " + Arrays.toString(embedding));
        float[] result = list.get(1).toFloatArray();
        return result[0];
    }

    private long[] tokenizeSingleString(NDManager manager, String[] input, List<Long> lodList) {
        List<Long> word_ids = new ArrayList<>();
        for (String word : input) {
            String word_id = word2IdDict.get(word);
            word_ids.add(Long.valueOf(StringUtils.isBlank(word_id) ? unkId : word_id));
        }
        lodList.add((long) word_ids.size());

        int size = Long.valueOf(lodList.get(lodList.size() - 1)).intValue();

        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            if (word_ids.size() > i) {
                array[i] = word_ids.get(i);
            } else {
                array[i] = 0;
            }
        }
        return array;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}