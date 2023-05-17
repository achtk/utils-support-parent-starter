package com.chua.common.support.lang.algorithm;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.TypeHashMap;
import com.chua.common.support.lang.tokenizer.Tokenizer;
import com.chua.common.support.lang.tokenizer.Word;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 余弦相似度
 *
 * @author CH
 * @since 2021-12-02
 */
@Slf4j
@Spi("cosine-similarity")
public class CosineSimilarityMatchingAlgorithm extends TypeHashMap implements MatchingAlgorithm {
    @Override
    public double match(String source, String target) {
        //如果wei空，或者字符长度为0，则代表完全相同
        if (StringUtils.isNullOrEmpty(source) && StringUtils.isNullOrEmpty(target)) {
            return 1.0;
        }
        //如果一个为0或者空，一个不为，那说明完全不相似
        if (StringUtils.isNullOrEmpty(source) || StringUtils.isNullOrEmpty(target)) {
            return 0.0;
        }
        //这个代表如果两个字符串相等那当然返回1了（这个我为了让它也分词计算一下，所以注释掉了）
        if (source.equalsIgnoreCase(target)) {
            return 1.0;
        }
        //第一步：进行分词
        List<Word> words1 = Tokenizer.segment(source);
        List<Word> words2 = Tokenizer.segment(target);

        return getSimilarity(words1, words2);
    }

    /**
     * 2、对于计算出的相似度保留小数点后六位
     */
    public double getSimilarity(List<Word> words1, List<Word> words2) {
        double score = getSimilarityImpl(words1, words2);
        //(int) (score * 1000000 + 0.5)其实代表保留小数点后六位 ,因为1034234.213强制转换不就是1034234。对于强制转换添加0.5就等于四舍五入
        score = (int) (score * 1000000 + 0.5) / (double) 1000000;
        return score;
    }

    /**
     * 文本相似度计算 判定方式：余弦相似度，通过计算两个向量的夹角余弦值来评估他们的相似度 余弦夹角原理： 向量a=(x1,y1),向量b=(x2,y2) similarity=a.b/|a|*|b| a.b=x1x2+y1y2
     * |a|=根号[(x1)^2+(y1)^2],|b|=根号[(x2)^2+(y2)^2]
     */
    public double getSimilarityImpl(List<Word> words1, List<Word> words2) {

        // 向每一个Word对象的属性都注入weight（权重）属性值
        taggingWeightByFrequency(words1, words2);

        //第二步：计算词频
        //通过上一步让每个Word对象都有权重值，那么在封装到map中（key是词，value是该词出现的次数（即权重））
        Map<String, Float> weightMap1 = getFastSearchMap(words1);
        Map<String, Float> weightMap2 = getFastSearchMap(words2);

        //将所有词都装入set容器中
        Set<Word> words = new HashSet<>();
        words.addAll(words1);
        words.addAll(words2);

        AtomicFloat ab = new AtomicFloat();
        AtomicFloat aa = new AtomicFloat();
        AtomicFloat bb = new AtomicFloat();

        // 第三步：写出词频向量，后进行计算
        words.parallelStream().forEach(word -> {
            //看同一词在a、b两个集合出现的此次
            Float x1 = weightMap1.get(word.getWord());
            Float x2 = weightMap2.get(word.getWord());
            if (x1 != null && x2 != null) {
                //x1x2
                float oneOfTheDimension = x1 * x2;
                //+
                ab.addAndGet(oneOfTheDimension);
            }
            if (x1 != null) {
                //(x1)^2
                float oneOfTheDimension = x1 * x1;
                //+
                aa.addAndGet(oneOfTheDimension);
            }
            if (x2 != null) {
                //(x2)^2
                float oneOfTheDimension = x2 * x2;
                //+
                bb.addAndGet(oneOfTheDimension);
            }
        });
        //|a| 对aa开方
        double aaa = Math.sqrt(aa.doubleValue());
        //|b| 对bb开方
        double bbb = Math.sqrt(bb.doubleValue());

        //使用BigDecimal保证精确计算浮点数
        //double aabb = aaa * bbb;
        BigDecimal aabb = BigDecimal.valueOf(aaa).multiply(BigDecimal.valueOf(bbb));

        //similarity=a.b/|a|*|b|
        //divide参数说明：aabb被除数,9表示小数点后保留9位，最后一个表示用标准的四舍五入法
        return BigDecimal.valueOf(ab.get()).divide(aabb, 9, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 向每一个Word对象的属性都注入weight（权重）属性值
     */
    protected void taggingWeightByFrequency(List<Word> words1, List<Word> words2) {
        if (words1.get(0).getWeight() != null && words2.get(0).getWeight() != null) {
            return;
        }
        //词频统计（key是词，value是该词在这段句子中出现的次数）
        Map<String, AtomicInteger> frequency1 = getFrequency(words1);
        Map<String, AtomicInteger> frequency2 = getFrequency(words2);

        words1.parallelStream().forEach(word -> word.setWeight(frequency1.get(word.getWord()).floatValue()));
        words2.parallelStream().forEach(word -> word.setWeight(frequency2.get(word.getWord()).floatValue()));
    }

    /**
     * 统计词频
     *
     * @return 词频统计图
     */
    private Map<String, AtomicInteger> getFrequency(List<Word> words) {

        Map<String, AtomicInteger> freq = new HashMap<>(16);
        words.forEach(i -> freq.computeIfAbsent(i.getWord(), k -> new AtomicInteger()).incrementAndGet());
        return freq;
    }

    /**
     * 输出：词频统计信息
     */
    private String getWordsFrequencyString(Map<String, AtomicInteger> frequency) {
        StringBuilder str = new StringBuilder();
        if (frequency != null && !frequency.isEmpty()) {
            AtomicInteger integer = new AtomicInteger();
            frequency.entrySet().stream().sorted((a, b) -> b.getValue().get() - a.getValue().get()).forEach(
                    i -> str.append("\t").append(integer.incrementAndGet()).append("、").append(i.getKey()).append("=")
                            .append(i.getValue()).append("\n"));
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }

    /**
     * 构造权重快速搜索容器
     */
    protected Map<String, Float> getFastSearchMap(List<Word> words) {
        if (CollectionUtils.isEmpty(words)) {
            return Collections.emptyMap();
        }
        Map<String, Float> weightMap = new ConcurrentHashMap<>(words.size());

        words.parallelStream().forEach(i -> {
            if (i.getWeight() != null) {
                weightMap.put(i.getWord(), i.getWeight());
            } else {
                log.error("no word weight info:" + i.getWord());
            }
        });
        return weightMap;
    }


    private class AtomicFloat extends Number {
        private AtomicInteger bits;

        public AtomicFloat() {
            this(0f);
        }

        public AtomicFloat(float initialValue) {
            bits = new AtomicInteger(Float.floatToIntBits(initialValue));
        }

        //叠加
        public final float addAndGet(float delta) {
            float expect;
            float update;
            do {
                expect = get();
                update = expect + delta;
            } while (!this.compareAndSet(expect, update));

            return update;
        }

        public final float getAndAdd(float delta) {
            float expect;
            float update;
            do {
                expect = get();
                update = expect + delta;
            } while (!this.compareAndSet(expect, update));

            return expect;
        }

        public final float getAndDecrement() {
            return getAndAdd(-1);
        }

        public final float decrementAndGet() {
            return addAndGet(-1);
        }

        public final float getAndIncrement() {
            return getAndAdd(1);
        }

        public final float incrementAndGet() {
            return addAndGet(1);
        }

        public final float getAndSet(float newValue) {
            float expect;
            do {
                expect = get();
            } while (!this.compareAndSet(expect, newValue));

            return expect;
        }

        public final boolean compareAndSet(float expect, float update) {
            return bits.compareAndSet(Float.floatToIntBits(expect), Float.floatToIntBits(update));
        }

        public final void set(float newValue) {
            bits.set(Float.floatToIntBits(newValue));
        }

        public final float get() {
            return Float.intBitsToFloat(bits.get());
        }

        @Override
        public float floatValue() {
            return get();
        }

        @Override
        public double doubleValue() {
            return (double) floatValue();
        }

        @Override
        public int intValue() {
            return (int) get();
        }

        @Override
        public long longValue() {
            return (long) get();
        }

        @Override
        public String toString() {
            return Float.toString(get());
        }
    }
}
