package com.chua.lucene.support.client;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;
import lombok.SneakyThrows;

import java.nio.file.Paths;

/**
 * lucene
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/8
 */
public class LuceneFileClient extends AbstractClient<LuceneTemplateResolver> {

    protected LuceneTemplateResolver luceneTemplateResolver;

    public LuceneFileClient(ClientOption cometOption) {
        super(cometOption);
    }

    @Override
    public LuceneTemplateResolver getClient() {
        return  luceneTemplateResolver;
    }

    @Override
    public void closeClient(LuceneTemplateResolver client) {
        try {
            client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void connectClient() {
        this.luceneTemplateResolver =
                new LuceneTemplateResolver(Paths.get(url), DirectoryFactory.DirectoryType.NIO);
    }

    @SneakyThrows
    @Override
    public void close() {
        luceneTemplateResolver.close();
    }
}
