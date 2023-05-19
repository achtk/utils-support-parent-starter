package com.chua.lucene.support.engine;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.engine.AbstractEngine;
import com.chua.common.support.engine.FullTextEngine;
import com.chua.common.support.engine.config.EngineConfig;
import com.chua.common.support.utils.Md5Utils;
import com.chua.lucene.support.entity.HitData;
import com.chua.lucene.support.factory.DirectoryFactory;
import com.chua.lucene.support.operator.DocumentOperatorTemplate;
import com.chua.lucene.support.operator.IndexOperatorTemplate;
import com.chua.lucene.support.operator.SearchOperatorTemplate;
import com.chua.lucene.support.resolver.LuceneTemplateResolver;
import com.chua.lucene.support.util.DocumentUtil;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * lucene
 * @author CH
 */
public class LuceneFileFullTextEngine<T> extends AbstractEngine<T> implements FullTextEngine<T> {

    private final LuceneTemplateResolver luceneTemplateResolver;
    private final String index;
    private SearchOperatorTemplate searchOperatorTemplate;
    private DocumentOperatorTemplate documentOperatorTemplate;

    public LuceneFileFullTextEngine(Class<T> target, EngineConfig engineConfig) {
        super(target, engineConfig);
        this.luceneTemplateResolver = createResolver();
        this.index = Md5Utils.getInstance().getMd5String(target.getTypeName());

        IndexOperatorTemplate indexOperatorTemplate = luceneTemplateResolver.getIndexOperatorTemplate();
        try {
            if(engineConfig.isCleanWhenInitial() && indexOperatorTemplate.exist(index)) {
                indexOperatorTemplate.delete(index);
            }
            if(!indexOperatorTemplate.exist(index)) {
                indexOperatorTemplate.create(index, engineConfig.getFragmentation());
            }
            this.documentOperatorTemplate = luceneTemplateResolver.getDocumentOperatorTemplate(index);
            this.searchOperatorTemplate = luceneTemplateResolver.getSearchOperatorTemplate(index);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected LuceneTemplateResolver createResolver() {
        return new LuceneTemplateResolver(Paths.get("."), DirectoryFactory.DirectoryType.NIO);
    }

    @Override
    public List<T> search(String sl) {
        if(null == searchOperatorTemplate) {
            return Collections.emptyList();
        }

        HitData search = searchOperatorTemplate.search(sl);
        List<Map<String, Object>> data = search.getData();
        return BeanUtils.copyPropertiesList(data, target);
    }

    @Override
    public boolean addAll(List<T> t) {
        if(null == documentOperatorTemplate) {
            return false;
        }
        try {
            documentOperatorTemplate.addDocuments(DocumentUtil.object2DataDocuments((List<Object>) t));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(T t) {
        if(null == documentOperatorTemplate) {
            return false;
        }

        try {
            documentOperatorTemplate.deleteDocument(DocumentUtil.map2Document(BeanMap.of(t)).get("id"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void close() throws Exception {
        luceneTemplateResolver.close();
    }
}
