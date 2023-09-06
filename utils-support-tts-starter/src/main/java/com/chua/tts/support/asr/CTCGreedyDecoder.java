package com.chua.tts.support.asr;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * CTC贪婪(最佳路径)解码器
 *
 * @author Calvin <179209347@qq.com>
 */
public class CTCGreedyDecoder {

    /**
     * 由最可能的令牌组成的路径将被进一步后处理到去掉连续重复和所有空白
     *
     * @param manager
     * @param probsSeq:   每一条都是2D的概率表。每个元素都是浮点数概率的列表一个字符
     * @param vocabulary: 词汇列表
     * @param blankIndex: 需要移除的空白索引
     * @return 解码后得到的 score,字符串
     * @throws Exception
     */
    public static Pair greedyDecoder(
            NDManager manager, NDArray probsSeq, List<String> vocabulary, long blankIndex) {
        // 获得每个时间步的最佳索引
        float[] floats = probsSeq.toFloatArray();
        int rows = (int) probsSeq.getShape().get(0);
        int cols = (int) probsSeq.getShape().get(1);

        long[] maxIndexList = probsSeq.argMax(1).toLongArray();

        List<Float> maxProbList = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            if (maxIndexList[i] != blankIndex) {
                maxProbList.add(probsSeq.getFloat(i, maxIndexList[i]));
            }
        }

        // 删除连续的重复"索引"
        List<Long> indexList = new ArrayList<>();
        long current = maxIndexList[0];
        indexList.add(current);
        for (int i = 1; i < maxIndexList.length; i++) {
            if (maxIndexList[i] != current) {
                indexList.add(maxIndexList[i]);
                current = maxIndexList[i];
            }
        }

        // 删除空索引
        List<Long> pureIndexList = new ArrayList<>();
        for (Long value : indexList) {
            if (value != blankIndex) {
                pureIndexList.add(value);
            }
        }

        // 索引列表转换为字符串
        StringBuffer sb = new StringBuffer();
        for (Long value : pureIndexList) {
            sb.append(vocabulary.get(value.intValue()));
        }

        float score = 0;
        if (maxProbList.size() > 0) {
            float sum = 0;
            for (Float value : maxProbList) {
                sum += value;
            }
            score = (sum / maxProbList.size()) * 100.0f;
        }

        return Pair.of(score, sb.toString());
    }
}
