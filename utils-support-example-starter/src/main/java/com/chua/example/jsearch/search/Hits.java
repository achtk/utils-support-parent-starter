package com.chua.example.jsearch.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索结果
 * @author 杨尚川
 */
public class Hits {
    private int hitCount;
    private List<Doc> docs = new ArrayList<>();

    public Hits(int hitCount, List<Doc> docs) {
        this.hitCount = hitCount;
        this.docs = docs;
    }

    public int getHitCount() {
        return hitCount;
    }

    public List<Doc> getDocs() {
        return Collections.unmodifiableList(this.docs);
    }
}
