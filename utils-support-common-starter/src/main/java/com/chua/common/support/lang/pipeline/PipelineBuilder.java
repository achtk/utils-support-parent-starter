package com.chua.common.support.lang.pipeline;

import lombok.NoArgsConstructor;

/**
 * 管道模式
 *
 * @param <I>
 * @author CH
 */
@NoArgsConstructor(staticName = "newBuilder")
public class PipelineBuilder<I> {

    private ForwardingPipeline<I> pipelines;
    private ForwardingPipeline<I> root;


    public PipelineBuilder<I> next(Pipeline<I> pipeline) {
        if (this.pipelines == null) {
            this.root = this.pipelines = new ForwardingPipeline<>(pipeline);
        } else {
            this.pipelines = this.pipelines.addNext(pipeline);
        }
        return this;
    }

    /**
     * 执行
     *
     * @return 执行
     */
    public I execute() {
        return root.process(null);
    }

    /**
     * 执行
     *
     * @return 执行
     */
    public I execute(I i) {
        return root.process(i);
    }
}
