package com.chua.lucene.support.client;


import com.chua.common.support.protocol.client.AbstractClientProvider;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;

/**
 * Lucene client provider
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/8
 */
public class LuceneMemClientProvider extends AbstractClientProvider<LuceneTemplateResolver> {

    public LuceneMemClientProvider(ClientOption option) {
        super(option);
    }


    @Override
    protected Class<?> clientType() {
        return LuceneMemClient.class;
    }
}
