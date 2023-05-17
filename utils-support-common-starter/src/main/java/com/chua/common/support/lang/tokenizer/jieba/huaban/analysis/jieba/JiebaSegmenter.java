package com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba;

import com.chua.common.support.lang.tokenizer.jieba.huaban.analysis.jieba.viterbi.FinalSeg;

import java.nio.file.Path;
import java.util.*;


/**
 * @author Administrator
 */
public class JiebaSegmenter {
    private static final WordDictionary WORD_DICT = WordDictionary.getInstance();
    private static final FinalSeg FINAL_SEG = FinalSeg.getInstance();
    private static final int M_2 = 2;

    /**
     * initialize the user dictionary.
     *
     * @param path user dict dir
     */
    public void initUserDict(Path path) {
        WORD_DICT.init(path);
    }

    public void initUserDict(String[] paths) {
        WORD_DICT.init(paths);

    }

    private Map<Integer, List<Integer>> createDag(String sentence) {
        Map<Integer, List<Integer>> dag = new LinkedHashMap<>();
        DictSegment trie = WORD_DICT.getTrie();
        char[] chars = sentence.toCharArray();
        int length = chars.length;
        int i = 0, j = 0;
        while (i < length) {
            Hit hit = trie.match(chars, i, j - i + 1);
            if (hit.isPrefix() || hit.isMatch()) {
                if (hit.isMatch()) {
                    if (!dag.containsKey(i)) {
                        List<Integer> value = new ArrayList<Integer>();
                        dag.put(i, value);
                        value.add(j);
                    } else {
                        dag.get(i).add(j);
                    }
                }
                j += 1;
                if (j >= length) {
                    i += 1;
                    j = i;
                }
            } else {
                i += 1;
                j = i;
            }
        }
        for (i = 0; i < length; ++i) {
            if (!dag.containsKey(i)) {
                List<Integer> value = new ArrayList<Integer>();
                value.add(i);
                dag.put(i, value);
            }
        }
        return dag;
    }

    private Map<Integer, Pair<Integer>> calc(String sentence, Map<Integer, List<Integer>> dag) {
        int length = sentence.length();
        HashMap<Integer, Pair<Integer>> route = new LinkedHashMap<>();
        route.put(length, new Pair<>(0, 0.0));
        for (int i = length - 1; i > -1; i--) {
            Pair<Integer> candidate = null;
            for (Integer x : dag.get(i)) {
                double freq = WORD_DICT.getFreq(sentence.substring(i, x + 1)) + route.get(x + 1).freq;
                if (null == candidate) {
                    candidate = new Pair<Integer>(x, freq);
                } else if (candidate.freq < freq) {
                    candidate.freq = freq;
                    candidate.key = x;
                }
            }
            route.put(i, candidate);
        }
        return route;
    }

    public List<SegToken> process(String paragraph, SegMode mode) {
        List<SegToken> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int i = 0; i < paragraph.length(); ++i) {
            char ch = CharacterUtil.regularize(paragraph.charAt(i));
            if (CharacterUtil.ccFind(ch)) {
                sb.append(ch);
            } else {
                if (sb.length() > 0) {
                    if (mode == SegMode.SEARCH) {
                        for (String word : sentenceProcess(sb.toString())) {
                            tokens.add(new SegToken(word, offset, offset += word.length()));
                        }
                    } else {
                        for (String token : sentenceProcess(sb.toString())) {
                            if (token.length() > 2) {
                                String gram2;
                                int j = 0;
                                for (; j < token.length() - 1; ++j) {
                                    gram2 = token.substring(j, j + 2);
                                    if (WORD_DICT.containsWord(gram2)) {
                                        tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                                    }
                                }
                            }
                            if (token.length() > 3) {
                                String gram3;
                                int j = 0;
                                for (; j < token.length() - M_2; ++j) {
                                    gram3 = token.substring(j, j + 3);
                                    if (WORD_DICT.containsWord(gram3)) {
                                        tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                                    }
                                }
                            }
                            tokens.add(new SegToken(token, offset, offset += token.length()));
                        }
                    }
                    sb = new StringBuilder();
                    offset = i;
                }
                if (WORD_DICT.containsWord(paragraph.substring(i, i + 1))) {
                    tokens.add(new SegToken(paragraph.substring(i, i + 1), offset, ++offset));
                } else {
                    tokens.add(new SegToken(paragraph.substring(i, i + 1), offset, ++offset));
                }
            }
        }
        if (sb.length() > 0) {
            if (mode == SegMode.SEARCH) {
                for (String token : sentenceProcess(sb.toString())) {
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            } else {
                for (String token : sentenceProcess(sb.toString())) {
                    if (token.length() > 2) {
                        String gram2;
                        int j = 0;
                        for (; j < token.length() - 1; ++j) {
                            gram2 = token.substring(j, j + 2);
                            if (WORD_DICT.containsWord(gram2)) {
                                tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                            }
                        }
                    }
                    if (token.length() > 3) {
                        String gram3;
                        int j = 0;
                        for (; j < token.length() - M_2; ++j) {
                            gram3 = token.substring(j, j + 3);
                            if (WORD_DICT.containsWord(gram3)) {
                                tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                            }
                        }
                    }
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            }
        }

        return tokens;
    }

    /**
     * sentenceProcess
     */
    public List<String> sentenceProcess(String sentence) {
        List<String> tokens = new ArrayList<String>();
        int length = sentence.length();
        Map<Integer, List<Integer>> dag = createDag(sentence);
        Map<Integer, Pair<Integer>> route = calc(sentence, dag);

        int x = 0;
        int y = 0;
        String buf;
        StringBuilder sb = new StringBuilder();
        while (x < length) {
            y = route.get(x).key + 1;
            String lWord = sentence.substring(x, y);
            if (y - x == 1) {
                sb.append(lWord);
            } else {
                if (sb.length() > 0) {
                    buf = sb.toString();
                    sb = new StringBuilder();
                    if (buf.length() == 1) {
                        tokens.add(buf);
                    } else {
                        if (WORD_DICT.containsWord(buf)) {
                            tokens.add(buf);
                        } else {
                            FINAL_SEG.cut(buf, tokens);
                        }
                    }
                }
                tokens.add(lWord);
            }
            x = y;
        }
        buf = sb.toString();
        if (buf.length() > 0) {
            if (buf.length() == 1) {
                tokens.add(buf);
            } else {
                if (WORD_DICT.containsWord(buf)) {
                    tokens.add(buf);
                } else {
                    FINAL_SEG.cut(buf, tokens);
                }
            }

        }
        return tokens;
    }


    public static enum SegMode {
        /**
         * index
         */
        INDEX,
        /**
         * search
         */
        SEARCH
    }
}
