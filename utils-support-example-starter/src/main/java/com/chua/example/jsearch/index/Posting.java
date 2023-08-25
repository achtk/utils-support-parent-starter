package com.chua.example.jsearch.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 倒排表
 * @author 杨尚川
 */
public class Posting {
    private Map<Integer, PostingItem> postingItems = new HashMap<>();

    public int size(){
        return this.postingItems.size();
    }

    public Collection<PostingItem> getPostingItems() {
        return Collections.unmodifiableCollection(this.postingItems.values());
    }

    public void putIfAbsent(int docId){
        this.postingItems.putIfAbsent(docId, new PostingItem(docId));
    }

    public PostingItem get(int docId){
        return this.postingItems.get(docId);
    }

    public void remove(PostingItem postingItem){
        this.postingItems.remove(postingItem);
    }

    public void clear(){
        this.clear();
    }
}
