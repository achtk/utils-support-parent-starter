package com.chua.common.support.lang.spide.pipeline;

import java.util.List;

/**
 * @author CH
 */
public interface CollectorPipeline<T> extends Pipeline {

    /**
     * Get all results collected.
     *
     * @return collected results
     */
    public List<T> getCollected();
}
