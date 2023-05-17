package com.chua.common.support.lang.tokenizer;

import lombok.*;

/**
 * 词
 *
 * @author CH
 * @version 1.0.0
 */
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Word {

    /**
     * 词语
     */
    @NonNull
    public String word;

    /**
     * 词性
     */
    @NonNull
    public String nature;

    /**
     * 在文本中的起始位置（需开启分词器的offset选项）
     */
    public int offset;

    /**
     * 在文本中的末位
     */
    public int end;
    /**
     * 可能性
     */
    private double prob;
    /**
     * 权
     */
    private Float weight;

    @Override
    public String toString() {
        return word;
    }
}
