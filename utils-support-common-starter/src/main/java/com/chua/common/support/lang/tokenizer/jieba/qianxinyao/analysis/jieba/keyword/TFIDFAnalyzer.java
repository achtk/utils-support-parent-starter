package com.chua.common.support.lang.tokenizer.jieba.qianxinyao.analysis.jieba.keyword;

import com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba.JiebaSegmenter;
import com.chua.common.support.resource.repository.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Tom Qian
 * @email tomqianmaple@outlook.com
 * @github https://github.com/bluemapleman
 * @date Oct 20, 2018
 * tfidf算法原理参考：http://www.cnblogs.com/ywl925/p/3275878.html
 * 部分实现思路参考jieba分词：https://github.com/fxsjy/jieba
 */
public class TFIDFAnalyzer {

    static HashMap<String, Double> idfMap;
    static HashSet<String> stopWordsSet;
    static double idfMedian;

    public static void main(String[] args) {
        String content = "孩子上了幼儿园 安全防拐教育要做好";
        int topN = 5;
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> list = tfidfAnalyzer.analyze(content, topN);
        for (Keyword word : list) {
            System.out.print(word.getName() + ":" + word.getTfidfvalue() + ",");
        }
    }

    /**
     * tfidf分析方法
     *
     * @param content 需要分析的文本/文档内容
     * @param topN    需要返回的tfidf值最高的N个关键词，若超过content本身含有的词语上限数目，则默认返回全部
     * @return
     */
    public List<Keyword> analyze(String content, int topN) {
        List<Keyword> keywordList = new ArrayList<>();

        if (stopWordsSet == null) {
            stopWordsSet = new HashSet<>();
            loadStopWords(stopWordsSet, Repository.classpath().add(Repository.current()).first("**/stop_words.txt*").openInputStream("stop_words.txt"));
        }
        if (idfMap == null) {
            idfMap = new LinkedHashMap<>();
            loadIdfMap(idfMap, Repository.classpath().add(Repository.current()).first("**/idf_dict.txt*").openInputStream("idf_dict.txt"));
        }

        Map<String, Double> tfMap = getTf(content);
        for (String word : tfMap.keySet()) {
            // 若该词不在idf文档中，则使用平均的idf值(可能定期需要对新出现的网络词语进行纳入)
            if (idfMap.containsKey(word)) {
                keywordList.add(new Keyword(word, idfMap.get(word) * tfMap.get(word)));
            } else {
                keywordList.add(new Keyword(word, idfMedian * tfMap.get(word)));
            }
        }

        Collections.sort(keywordList);

        if (keywordList.size() > topN) {
            int num = keywordList.size() - topN;
            for (int i = 0; i < num; i++) {
                keywordList.remove(topN);
            }
        }
        return keywordList;
    }

    /**
     * tf值计算公式
     * tf=N(i,j)/(sum(N(k,j) for all k))
     * N(i,j)表示词语Ni在该文档d（content）中出现的频率，sum(N(k,j))代表所有词语在文档d中出现的频率之和
     *
     * @param content content
     * @return ap<String, Double>
     */
    private Map<String, Double> getTf(String content) {
        Map<String, Double> tfMap = new LinkedHashMap<>();
        if (content == null || "".equals(content)) {
            return tfMap;
        }

        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> segments = segmenter.sentenceProcess(content);
        Map<String, Integer> freqMap = new HashMap<>(segments.size());

        int wordSum = 0;
        for (String segment : segments) {
            //停用词不予考虑，单字词不予考虑
            if (!stopWordsSet.contains(segment) && segment.length() > 1) {
                wordSum++;
                if (freqMap.containsKey(segment)) {
                    freqMap.put(segment, freqMap.get(segment) + 1);
                } else {
                    freqMap.put(segment, 1);
                }
            }
        }

        // 计算double型的tf值
        for (String word : freqMap.keySet()) {
            tfMap.put(word, freqMap.get(word) * 0.1 / wordSum);
        }

        return tfMap;
    }

    /**
     * 默认jieba分词的停词表
     * url:https://github.com/yanyiwu/nodejieba/blob/master/dict/stop_words.utf8
     *
     * @param set
     * @param filePath
     */
    private void loadStopWords(Set<String> set, InputStream in) {
        BufferedReader bufr;
        try {
            bufr = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = bufr.readLine()) != null) {
                set.add(line.trim());
            }
            try {
                bufr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * idf值本来需要语料库来自己按照公式进行计算，不过jieba分词已经提供了一份很好的idf字典，所以默认直接使用jieba分词的idf字典
     * url:https://raw.githubusercontent.com/yanyiwu/nodejieba/master/dict/idf.utf8
     *
     * @param map map
     * @param in  in
     */
    private void loadIdfMap(Map<String, Double> map, InputStream in) {
        try (BufferedReader bufr = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = bufr.readLine()) != null) {
                String[] kv = line.trim().split(" ");
                map.put(kv[0], Double.parseDouble(kv[1]));
            }
            // 计算idf值的中位数
            List<Double> idfList = new ArrayList<>(map.values());
            Collections.sort(idfList);
            idfMedian = idfList.get(idfList.size() / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

