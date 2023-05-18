package com.chua.common.support.lang.proxy.plugin;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.annotations.WatchTime;
import com.chua.common.support.lang.StopWatch;
import com.chua.common.support.reflection.describe.processor.impl.AbstractMethodAnnotationPostProcessor;
import com.chua.common.support.span.Span;
import com.chua.common.support.span.TrackContext;
import com.chua.common.support.span.TrackManager;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Stack;
import java.util.UUID;

/**
 * 缓存注解扫描
 *
 * @author CH
 */
@Extension("watch-time")
public class WatchTimeMethodAnnotationPostProcessor extends AbstractMethodAnnotationPostProcessor<WatchTime> {

    @Override
    public Object execute(Object entity, Object[] args) {
        WatchTime watchTime = getAnnotationValue();
        if (null == watchTime) {
            return invoke(entity, args);
        }

        Span currentSpan = TrackManager.getCurrentSpan();
        if (null == currentSpan) {
            String linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        Method method = getMethod();
        String value = StringUtils.defaultString(watchTime.value(), method.getName());
        TrackManager.createEntrySpan(value);

        try {
            return invoke(entity, args);
        } finally {
            Span exitSpan = TrackManager.getExitSpan();
            if (null != exitSpan) {
                exitSpan.setOutTimeNanos(System.nanoTime());
                exitSpan.setMethod(method.getName());
                exitSpan.setType(method.getDeclaringClass().getName());
                exitSpan.setStack(Thread.currentThread().getStackTrace());

                TrackManager.registerSpan(exitSpan);

                if (null == currentSpan) {
                    StopWatch stopWatch = exitSpan.getStopWatch();
                    Stack<Span> spans = TrackManager.currentSpans();
                    if (null == stopWatch) {
                        stopWatch = new StopWatch(exitSpan.getTitle());
                    }

                    for (int i = 0; i < spans.size(); i++) {
                        Span span = spans.get(i);
                        stopWatch.addTask(span);
                    }

                    stopWatch.setTotalTimeNanos(exitSpan.getOutTimeNanos() - exitSpan.getEntryTimeNanos());

                    Class<?> handler = watchTime.handler();

                    if (!handler.isInterface()) {
                        WatchTime.WatchHandler watchHandler = (WatchTime.WatchHandler) ClassUtils.forObject(handler, WatchTime.WatchHandler.class);
                        if (null != watchHandler) {
                            watchHandler.handler(stopWatch, watchTime);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Class<WatchTime> getAnnotationType() {
        return WatchTime.class;
    }
}
