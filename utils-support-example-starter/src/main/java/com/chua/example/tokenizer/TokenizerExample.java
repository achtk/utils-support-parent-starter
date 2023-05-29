package com.chua.example.tokenizer;

import com.chua.common.support.lang.tokenizer.Tokenizer;

/**
 * @author CH
 */
public class TokenizerExample {

    public static void main(String[] args) {
        Tokenizer tokenizer = Tokenizer.newDefault();
        System.out.println(tokenizer.segments("测试单词"));
    }
}
