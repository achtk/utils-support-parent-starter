package com.chua.common.support.lang.pipeline;

/**
 * 管道模式
 *
 * @author CH
 */
public class ForwardingPipeline<I> implements Pipeline<I> {

    private final Pipeline<I> pipeline;
    private ForwardingPipeline<I> next;

    public ForwardingPipeline(Pipeline<I> pipeline) {
        this.pipeline = pipeline;
    }

    public ForwardingPipeline<I> addNext(Pipeline<I> pipeline) {
        return this.next = new ForwardingPipeline<>(pipeline);
    }

    @Override
    public I process(I i) {
        I process = pipeline.process(i);
        if (next == null) {
            return process;
        }

        return next.process(process);
    }
}
