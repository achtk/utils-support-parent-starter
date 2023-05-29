package com.chua.common.support.lang.tokenizer;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.spi.ServiceProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分词<br />
 * | 词性编码 | 词性名称 | 注 解                                                    |<br />
 * | ------------ | ------------ | ------------------------------------------------------------ |<br />
 * | Ag       | 形语素       | 形容词性语素。形容词代码为 a，语素代码ｇ前面置以A。          |<br />
 * | a        | 形容词       | 取英语形容词 adjective的第1个字母。                          |<br />
 * | ad       | 副形词       | 直接作状语的形容词。形容词代码 a和副词代码d并在一起。        |<br />
 * | an       | 名形词       | 具有名词功能的形容词。形容词代码 a和名词代码n并在一起。      |<br />
 * | b        | 区别词       | 取汉字“别”的声母。                                           |<br />
 * | c        | 连词         | 取英语连词 conjunction的第1个字母。                          |<br />
 * | dg       | 副语素       | 副词性语素。副词代码为 d，语素代码ｇ前面置以D。              |<br />
 * | d        | 副词         | 取 adverb的第2个字母，因其第1个字母已用于形容词。            |<br />
 * | e        | 叹词         | 取英语叹词 exclamation的第1个字母。                          |<br />
 * | f        | 方位词       | 取汉字“方”                                                   |<br />
 * | g            | 语素         | 绝大多数语素都能作为合成词的“词根”，取汉字“根”的声母。       |<br />
 * | h            | 前接成分     | 取英语 head的第1个字母。                                     |<br />
 * | i            | 成语         | 取英语成语 idiom的第1个字母。                                |<br />
 * | j            | 简称略语     | 取汉字“简”的声母。                                           |<br />
 * | k            | 后接成分     |                                                              |<br />
 * | l        | 习用语       | 习用语尚未成为成语，有点“临时性”，取“临”的声母。             |<br />
 * | m        | 数词         | 取英语 numeral的第3个字母，n，u已有他用。                    |<br />
 * | Ng       | 名语素       | 名词性语素。名词代码为 n，语素代码ｇ前面置以N。              |<br />
 * | n        | 名词         | 取英语名词 noun的第1个字母。                                 |<br />
 * | nr       | 人名         | 名词代码 n和“人(ren)”的声母并在一起。                        |<br />
 * | ns       | 地名         | 名词代码 n和处所词代码s并在一起。                            |<br />
 * | nt       | 机构团体     | “团”的声母为 t，名词代码n和t并在一起。                       |<br />
 * | nz       | 其他专名     | “专”的声母的第 1个字母为z，名词代码n和z并在一起。            |<br />
 * | o        | 拟声词       | 取英语拟声词 onomatopoeia的第1个字母。                       |<br />
 * | p        | 介词         | 取英语介词 prepositional的第1个字母。                        |<br />
 * | q        | 量词         | 取英语 quantity的第1个字母。                                 |<br />
 * | r        | 代词         | 取英语代词 pronoun的第2个字母,因p已用于介词。                |<br />
 * | s        | 处所词       | 取英语 space的第1个字母。                                    |<br />
 * | tg       | 时语素       | 时间词性语素。时间词代码为 t,在语素的代码g前面置以T。        |<br />
 * | t        | 时间词       | 取英语 time的第1个字母。                                     |<br />
 * | u            | 助词         | 取英语助词 auxiliary                                         |<br />
 * | vg       | 动语素       | 动词性语素。动词代码为 v。在语素的代码g前面置以V。           |<br />
 * | v            | 动词         | 取英语动词 verb的第一个字母。                                |<br />
 * | vd       | 副动词       | 直接作状语的动词。动词和副词的代码并在一起。                 |<br />
 * | vn       | 名动词       | 指具有名词功能的动词。动词和名词的代码并在一起。             |<br />
 * | w        | 标点符号     |                                                              |<br />
 * | x            | 非语素字     | 非语素字只是一个符号，字母 x通常用于代表未知数、符号。       |<br />
 * | y        | 语气词       | 取汉字“语”的声母。                                           |<br />
 * | z        | 状态词       | 取汉字“状”的声母的前一个字母。                               |<br />
 * | un       | 未知词       | 不可识别词及用户自定义词组。取英文Unkonwn首两个字母。(非北大标准，CSW分词中定义) |<br />
 *
 * @author CH
 */
@Spi("hanlp:nlp")
public interface Tokenizer {
    /**
     * 分词
     *
     * @param word 词性
     * @return 词组
     */
    static List<Word> segment(String word) {
        ServiceProvider<Tokenizer> serviceProvider = ServiceProvider.of(Tokenizer.class);
        Tokenizer tokenizer = serviceProvider.getSpiService();
        if (null != tokenizer) {
            return tokenizer.segments(word);
        }
        return Arrays.stream(word.split("")).map(it -> new Word(it, "")).collect(Collectors.toList());
    }

    /**
     * 分词
     *
     * @param word 词性
     * @param type 解析器
     * @return 词组
     */
    static List<Word> segment(String word, String type) {
        ServiceProvider<Tokenizer> serviceProvider = ServiceProvider.of(Tokenizer.class);
        Tokenizer tokenizer = serviceProvider.getExtension(type);
        if (null != tokenizer) {
            return tokenizer.segments(word);
        }
        return Arrays.stream(word.split("")).map(it -> new Word(it, "")).collect(Collectors.toList());
    }

    /**
     * 获取分词器
     * @return 分词器
     */
    static Tokenizer of(String type) {
        ServiceProvider<Tokenizer> serviceProvider = ServiceProvider.of(Tokenizer.class);
        return serviceProvider.getNewExtension(type);
    }

    /**
     * 获取分词器
     * @return 分词器
     */
    static Tokenizer newDefault() {
        ServiceProvider<Tokenizer> serviceProvider = ServiceProvider.of(Tokenizer.class);
        return serviceProvider.getNewExtension(null);
    }

    /**
     * 分词
     *
     * @param word 词性
     * @return 词组
     */
    List<Word> segments(String word);
}
