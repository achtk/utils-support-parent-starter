package com.chua.lucene.support.engine;

import com.chua.common.support.engine.config.EngineConfig;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;

/**
 * lucene
 * @author CH
 */
public class LuceneMemFullTextEngine<T> extends LuceneFileFullTextEngine<T> {


    public LuceneMemFullTextEngine(Class<T> target, EngineConfig engineConfig) {
        super(target, engineConfig);
    }

    @Override
    protected LuceneTemplateResolver createResolver() {
        return new LuceneTemplateResolver(DirectoryFactory.DirectoryType.MEM);
    }
}
