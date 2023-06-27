package com.chua.common.support.lang.arrange;

import java.util.Map;

/**
 * 无依赖执行器
 * @author CH
 */
public class DependsNullArrangeExecutor implements ArrangeExecutor<ArrangeResult> {
    private final Arrange arrange;
    private ArrangeLogger arrangeLogger;

    public DependsNullArrangeExecutor(Arrange arrange, ArrangeLogger arrangeLogger) {
        this.arrange = arrange;
        this.arrangeLogger = arrangeLogger;
    }

    @Override
    public ArrangeResult execute(Map<String, Object> args) {
        return arrange.getHandler().execute(args);
    }
}
