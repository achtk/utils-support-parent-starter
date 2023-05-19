package com.chua.pytorch.support.ocr.senta;


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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SentaTranslator implements Translator<String[], float[]> {
    private Map<String, String> word2IdDict = new HashMap<String, String>();
    private String unkId = "";

    public SentaTranslator() {
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("assets/vocab.txt").openStream()) {
            List<String> words = Utils.readLines(is, true);
            words.stream()
                    .filter(word -> (word != null && word != ""))
                    .forEach(
                            word -> {
                                String[] ws = word.split("	");
                                if (ws.length == 1) {
                                    //                  word2id_dict.put("", ws[0]); // 文字是key,id是value
                                } else {
                                    word2IdDict.put(ws[0], ws[1]); // 文字是key,id是value
                                }
                            });
        }
        unkId = "" + word2IdDict.size(); // 文字是key,id是value
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String[] input) {

        NDManager manager = ctx.getNDManager();
        List<Long> lodList = new ArrayList<>(0);
        lodList.add(new Long(0));
        List<Long> sh = tokenizeSingleString(manager, input, lodList);
        int size = Long.valueOf(lodList.get(lodList.size() - 1)).intValue();
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            if (sh.size() > i) {
                array[i] = sh.get(i);
            } else {
                array[i] = 0;
            }
        }
        NDArray ndArray = manager.create(array, new Shape(lodList.get(lodList.size() - 1), 1));

        ndArray.setName("words");
        long[][] lod = new long[1][2];
        lod[0][0] = 0;
        lod[0][1] = lodList.get(lodList.size() - 1);
        ((PpNDArray) ndArray).setLoD(lod);
        return new NDList(ndArray);
    }

    @Override
    public float[] processOutput(TranslatorContext ctx, NDList list) {
        // index = 0 negative
        // index = 1 positive
        // [0.05931241 0.9406876 ]
        return list.get(0).toFloatArray();
    }

    private List<Long> tokenizeSingleString(NDManager manager, String[] input, List<Long> lod) {
        List<Long> word_ids = new ArrayList<>();
        for (String word : input) {
            String word_id = word2IdDict.get(word);
            word_ids.add(Long.valueOf(StringUtils.isBlank(word_id) ? unkId : word_id));
        }
        lod.add((long) word_ids.size());
        return word_ids;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}
