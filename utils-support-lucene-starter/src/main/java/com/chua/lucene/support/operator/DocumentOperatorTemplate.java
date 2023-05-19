package com.chua.lucene.support.operator;

import com.chua.lucene.support.entity.DataDocument;
import java.util.List;

/**
 * 文档操作模板
 *
 * @author CH
 * @version 1.0.0
 */
public interface DocumentOperatorTemplate {
    /**
     * 添加文档
     *
     * @param dataDocument 数据文档
     * @throws Exception Exception
     */
    void addDocument(DataDocument dataDocument) throws Exception;

    /**
     * 添加文档
     *
     * @param dataDocument 数据文档
     * @throws Exception Exception
     */
    void addDocuments(List<DataDocument> dataDocument) throws Exception;

    /**
     * 删除文档
     *
     * @param dataId 数据标识
     * @throws Exception Exception
     */
    void deleteDocument(String dataId) throws Exception;

    /**
     * 更新文档
     *
     * @param dataDocument 数据文档
     * @throws Exception Exception
     */
    void updateDocument(DataDocument dataDocument) throws Exception;

    /**
     * 更新文档
     *
     * @param dataDocument 数据文档
     * @throws Exception Exception
     */
    void updateDocuments(List<DataDocument> dataDocument) throws Exception;
}
