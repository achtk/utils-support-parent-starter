package com.chua.example.jsearch.index;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 倒排表项
 * @author 杨尚川
 */
public class PostingItem implements Comparable{
    private int docId;
    private int frequency;
    private Set<Integer> positions = new HashSet<>();

    public PostingItem(int docId) {
        this.docId = docId;
    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getFrequency(){
        return positions.isEmpty()?frequency:positions.size();
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String positionsToStr(){
        StringBuilder str = new StringBuilder();
        this.positions.stream().sorted().forEach(p -> str.append(p).append(":"));
        str.setLength(str.length()-1);
        return str.toString();
    }

    public Set<Integer> getPositions() {
        return Collections.unmodifiableSet(this.positions);
    }

    public void addPosition(int position) {
        this.positions.add(position);
    }

    public void removePosition(int position) {
        this.positions.remove(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostingItem)) return false;

        PostingItem postingItem = (PostingItem) o;

        return docId == postingItem.docId;

    }

    @Override
    public int hashCode() {
        return docId;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.valueOf(docId).compareTo(((PostingItem) o).getDocId());
    }
}
