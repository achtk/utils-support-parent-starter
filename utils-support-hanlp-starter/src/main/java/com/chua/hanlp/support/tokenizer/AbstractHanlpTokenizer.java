package com.chua.hanlp.support.tokenizer;

import com.chua.common.support.resource.ResourceProvider;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.corpus.io.ResourceIOAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 抽象分词器
 *
 * @author CH
 */
public abstract class AbstractHanlpTokenizer {

    static {
        HanLP.Config.IOAdapter = new NewIOAdapter();
    }


    static class NewIOAdapter extends ResourceIOAdapter {
        @Override
        public InputStream open(String path) throws IOException {
            InputStream is = null;
            try {
                is = IOUtil.isResource(path) ? IOUtil.getResourceAsStream("/" + path) : Files.newInputStream(Paths.get(path));
            } catch (Exception ignored) {
            }
            return null == is ? ResourceProvider.of("run:" + path).getResource().openStream() : is;
        }

        @Override
        public OutputStream create(String path) throws IOException {
            return super.create(path);
        }
    }
}