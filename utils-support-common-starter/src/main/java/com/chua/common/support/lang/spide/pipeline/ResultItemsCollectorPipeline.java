package com.chua.common.support.lang.spide.pipeline;

import com.chua.common.support.lang.spide.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CH
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> collector = new ArrayList<ResultItems>();

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        collector.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }
}
