package com.chua.example.jsearch.score;

import com.chua.example.jsearch.search.Doc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 词距评分组件
 * @author 杨尚川
 */
public class ProximityScore implements Score {
    private int slop = 0;

    @Override
    public Float score(Doc doc, List<String> words) {
        if(words.size() < 2){
            return 0f;
        }
        Map<String, List<Integer>> wordPosition = doc.getWordPosition();
        if(words.size() != wordPosition.size()){
            return 0f;
        }
        AtomicInteger score = new AtomicInteger();
        String lastWord = words.get(words.size()-1);
        wordPosition.get(lastWord).stream().forEach(endPosition->{
            //endPosition 是最后一个词在文本中的位置
            int previousPosition = endPosition;
            int permitPosition = previousPosition - slop - 1;
            int times = 0;
            for(int i=words.size()-2; i>-1; i--){
                boolean find = false;
                for(int position : wordPosition.get(words.get(i))){
                    if(position<previousPosition && position>=permitPosition){
                        find = true;
                        previousPosition = position;
                        permitPosition = previousPosition - slop - 1;
                        times++;
                    }
                }
                if(!find){
                    break ;
                }
            }
            if(times == words.size()-1){
                score.incrementAndGet();
            }
        });
        return Float.valueOf(score.get());
    }

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }
}
