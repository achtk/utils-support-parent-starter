package com.chua.example.pandoc;

import com.chua.common.support.file.pandoc.Executor;
import com.chua.common.support.file.pandoc.Pandoc;

/**
 * @author CH
 */
public class PandocExample {


    public static void main(String[] args) {
        Pandoc pandoc = new Pandoc();
        Executor executor = pandoc.createExecutor();
        executor.execute("Z://兴隆有礼管理平台接口.md", "z://兴隆有礼管理平台接口.docx");
    }
}
