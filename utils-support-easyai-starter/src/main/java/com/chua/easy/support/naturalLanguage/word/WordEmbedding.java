package com.chua.easy.support.naturalLanguage.word;


import com.chua.easy.support.MatrixTools.Matrix;
import com.chua.easy.support.MatrixTools.MatrixOperation;
import com.chua.easy.support.config.RZ;
import com.chua.easy.support.config.SentenceConfig;
import com.chua.easy.support.entity.SentenceModel;
import com.chua.easy.support.entity.WordMatrix;
import com.chua.easy.support.entity.WordTwoVectorModel;
import com.chua.easy.support.function.Tanh;
import com.chua.easy.support.i.OutBack;
import com.chua.easy.support.rnnNerveCenter.NerveManager;
import com.chua.easy.support.rnnNerveEntity.SensoryNerve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @DATA
 * @Author LiDaPeng
 * @Description 词嵌入向量训练
 */
public class WordEmbedding {
    private NerveManager nerveManager;
    private SentenceModel sentenceModel;
    private List<String> wordList = new ArrayList<>();//单字集合
    private SentenceConfig config;

    public WordEmbedding(SentenceConfig config) throws Exception {
        this.config = config;
        if (config.getSentenceModel() != null) {
            this.sentenceModel = config.getSentenceModel();
            wordList.addAll(sentenceModel.getWordSet());
            nerveManager = new NerveManager(wordList.size(), config.getWordVectorDimension(), wordList.size()
                    , 1, new Tanh(), false, config.getWeStudyPoint(), RZ.NOT_RZ, 0);
            nerveManager.init(true, false, false, true, 0, 0);
        }
    }

    public void insertModel(WordTwoVectorModel wordTwoVectorModel) throws Exception {
        wordList.clear();
        List<String> myWordList = wordTwoVectorModel.getWordList();
        int size = myWordList.size();
        for (int i = 0; i < size; i++) {
            wordList.add(myWordList.get(i));
        }
        nerveManager = new NerveManager(wordList.size(), config.getWordVectorDimension(), wordList.size()
                , 1, new Tanh(), false, config.getWeStudyPoint(), RZ.NOT_RZ, 0);
        nerveManager.init(true, false, false, true, 0, 0);
        nerveManager.insertModelParameter(wordTwoVectorModel.getModelParameter());
    }

    public Matrix getEmbedding(String word, long eventId) throws Exception {//做截断
        if (word.length() > config.getMaxWordLength()) {
            word = word.substring(0, config.getMaxWordLength());
        }
        int wordDim = config.getWordVectorDimension();
        Matrix matrix = null;
        for (int i = 0; i < word.length(); i++) {
            WordMatrix wordMatrix = new WordMatrix(wordDim);
            String myWord = word.substring(i, i + 1);
            int index = getID(myWord);
            studyDNN(eventId, index, 0, wordMatrix, true, false);
            if (matrix == null) {
                matrix = wordMatrix.getVector();
            } else {
                matrix = MatrixOperation.pushVector(matrix, wordMatrix.getVector(), true);
            }
        }
        return matrix;
    }

    private void studyDNN(long eventId, int featureIndex, int resIndex, OutBack outBack, boolean isEmbedding, boolean isStudy) throws Exception {
        List<SensoryNerve> sensoryNerves = nerveManager.getSensoryNerves();
        int size = sensoryNerves.size();
        Map<Integer, Double> map = new HashMap<>();
        if (resIndex > 0) {
            map.put(resIndex + 1, 1D);
        }
        for (int i = 0; i < size; i++) {
            double feature = 0;
            if (i == featureIndex) {
                feature = 1;
            }
            sensoryNerves.get(i).postMessage(eventId, feature, isStudy, map, outBack, isEmbedding, null);
        }
    }


    public WordTwoVectorModel start() throws Exception {//开始进行词向量训练
        List<String> sentenceList = sentenceModel.getSentenceList();
        int size = sentenceList.size();
        int index = 0;
        for (int i = index; i < size; i++) {
            long start = System.currentTimeMillis();
            study(sentenceList.get(i));
            long end = (System.currentTimeMillis() - start) / 1000;
            index++;
            double r = (index / (double) size) * 100;
            System.out.println("size:" + size + ",index:" + index + ",耗时:" + end + ",完成度:" + r);
        }
        WordTwoVectorModel wordTwoVectorModel = new WordTwoVectorModel();
        wordTwoVectorModel.setModelParameter(nerveManager.getModelParameter());
        wordTwoVectorModel.setWordList(wordList);
        //词向量训练结束
        return wordTwoVectorModel;
    }

    private void study(String word) throws Exception {
        int[] indexArray = new int[word.length()];
        for (int i = 0; i < word.length(); i++) {
            int index = getID(word.substring(i, i + 1));
            indexArray[i] = index;
        }
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            for (int j = 0; j < indexArray.length; j++) {
                if (i != j) {
                    int resIndex = indexArray[j];
                    studyDNN(1, index, resIndex, null, false, true);
                }
            }
        }
    }

    private int getID(String word) {
        int index = 0;
        int size = wordList.size();
        for (int i = 0; i < size; i++) {
            if (wordList.get(i).equals(word)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
