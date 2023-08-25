package com.chua.example.jsearch.search;

import java.util.Set;

/**
 * 搜索模式
 * @author 杨尚川
 */
public enum SearchMode{
    INTERSECTION, UNION;

    /**
     * 求 existentDocs 和 increasedDocs 的交集，合并结果存储于 existentDocs 中
     * @param existentDocs
     * @param increasedDocs
     */
    public static void intersection(Set<Doc> existentDocs, Set<Doc> increasedDocs){
        existentDocs.parallelStream().forEach(existentDoc -> {
            if (!increasedDocs.contains(existentDoc)) {
                existentDocs.remove(existentDoc);
                return;
            }
            //合并DOC
            for(Doc increasedDoc : increasedDocs){
                if (existentDoc.getId() == increasedDoc.getId()) {
                    existentDoc.merge(increasedDoc);
                    break;
                }
            }
        });
    }

    /**
     * 求 existentDocs 和 increasedDocs 的并集，合并结果存储于 existentDocs 中
     * @param existentDocs
     * @param increasedDocs
     */
    public static void union(Set<Doc> existentDocs, Set<Doc> increasedDocs){
        increasedDocs.parallelStream().forEach(increasedDoc -> {
            if (!existentDocs.contains(increasedDoc)) {
                existentDocs.add(increasedDoc);
            }
        });
    }
}
