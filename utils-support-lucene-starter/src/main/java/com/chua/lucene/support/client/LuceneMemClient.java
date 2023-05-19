package com.chua.lucene.support.client;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;
import lombok.SneakyThrows;

/**
 * lucene
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/8
 */
public class LuceneMemClient extends AbstractClient<LuceneTemplateResolver> {
    protected LuceneTemplateResolver luceneTemplateResolver;

    public LuceneMemClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public LuceneTemplateResolver getClient() {
        return luceneTemplateResolver;
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
        this.luceneTemplateResolver = new LuceneTemplateResolver(DirectoryFactory.DirectoryType.MEM);
    }

    @SneakyThrows
    @Override
    public void close() {
        luceneTemplateResolver.close();
    }
}