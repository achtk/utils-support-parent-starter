package com.chua.example.jsearch.search;

/**
 * 搜索接口
 * @author 杨尚川
 */
public interface Searcher {
    Hits search(String keyword);
    Hits search(String keyword, SearchMode searchMode);
    Hits search(String keyword, int page);
    Hits search(String keyword, SearchMode searchMode, int page);
}
