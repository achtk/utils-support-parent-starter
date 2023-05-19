package com.chua.pytorch.support.translation;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * TranslationTranslator
 * @author CH
 */
public class TranslationTranslator implements Translator<String[], String[]> {
    TranslationTranslator() {}

    private Map<String, String> srcWord2IdDict = new HashMap<String, String>();
    private Map<String, String> trgId2WordDict = new HashMap<String, String>();
    private String bosId = "0";
    private String eosId = "1";
    private String eosToken = "<e>";
    private String unkId = "2";
    private String padFactor = "8";
    private int maxLength = 256;

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        try (InputStream is = model.getArtifact("assets/vocab.zh").openStream()) {
            List<String> words = Utils.readLines(is, true);
            for (int i = 0; i < words.size(); i++) {
                srcWord2IdDict.put(words.get(i), "" + i); // 文字是key,id是value
            }
        }

        try (InputStream is = model.getArtifact("assets/vocab.en").openStream()) {
            List<String> words = Utils.readLines(is, true);
            for (int i = 0; i < words.size(); i++) {
                trgId2WordDict.put("" + i, words.get(i)); // id是key,文字是value
            }
        }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String[] input) {
        NDManager manager = ctx.getNDManager();
        List<Long> list = tokenizeSingleString(manager, input);
        long[] array = list.stream().mapToLong(Long::valueOf).toArray();
        NDArray ndArray = null;
        if (array.length > maxLength) {
            long[] newArr = (long[]) Arrays.copyOf(array, maxLength);
            ndArray = manager.create(newArr, new Shape(1, maxLength));
        } else {
            //    array = new long[] {6336, 914, 1652, 2051, 2, 44, 1};
            ndArray = manager.create(array, new Shape(1, array.length));
        }
        return new NDList(ndArray);
    }

    @Override
    public String[] processOutput(TranslatorContext ctx, NDList list) {
        // index = 0 negative
        // index = 1 positive
        // [0.05931241 0.9406876 ]
        NDArray ndArray = list.get(0);
        //    ndArray = ndArray.transpose(0, 2, 1);
        //    ndArray = ndArray.squeeze(0);
        long[] array = ndArray.toLongArray();

        Shape shape = ndArray.getShape();
        int rows = (int) shape.get(2);
        int cols = (int) shape.get(1);
        long[][] ids = new long[rows][cols];

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                ids[row][col] = array[col * rows + row];
            }
        }

        String[][] wordsArray = new String[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                wordsArray[row][col] = trgId2WordDict.get("" + ids[row][col]);
            }
        }

        String[] result = new String[rows];
        for (int row = 0; row < rows; row++) {
            result[row] = "";
            for (int col = 0; col < cols; col++) {
                if (wordsArray[row][col].equals(eosToken)) continue;
                result[row] = result[row] + " " + wordsArray[row][col];
            }
        }

        return result;
    }

    private List<Long> tokenizeSingleString(NDManager manager, String[] input) {
        List<Long> word_ids = new ArrayList<>();
        for (String word : input) {
            String word_id = srcWord2IdDict.get(word);
            word_ids.add(Long.valueOf(StringUtils.isBlank(word_id) ? unkId : word_id));
        }
        word_ids.add(Long.valueOf(eosId));
        return word_ids;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}